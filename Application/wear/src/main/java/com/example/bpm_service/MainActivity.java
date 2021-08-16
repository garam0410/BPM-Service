package com.example.bpm_service;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class MainActivity extends WearableActivity implements SensorEventListener{

    private static final String START_ACTIVITY_PATH = "/start-activity";

    private TextView mTextView, movieTitle, movieTime;
    private SensorManager mSensorManager;

    private String bpmData = "";
    private String hour, minute;

    private Calendar calendar;
    private long triggerTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.textView4);
        movieTitle = findViewById(R.id.movieTitle);
        movieTime = findViewById(R.id.movieTime);

        try {
            String message = getIntent().getStringExtra("message");
            String title = getIntent().getStringExtra("title");
            String time = getIntent().getStringExtra("time");

            System.out.println(message);
            System.out.println(title);
            System.out.println(time);

            calendar = Calendar.getInstance();

            // 종료시간 가져오기
            String[] splitTime = time.split("~");

            time = splitTime[1];
            System.out.println(time);

            splitTime = time.split(":");
            hour = splitTime[0];
            minute = splitTime[1];

            //message = "startMonitoring";
            if (message == null) {
                mTextView.setText("휴대폰에서 실행해야합니다.");
                try {
                    Thread.sleep(3000);  //스레드는 1초동안 일시 정지 상태가 된다.
                } catch (InterruptedException e) {
                    e.printStackTrace();    //예외처리가 필요하다.
                }
            }

            setAmbientEnabled();
            permissionRequest();

            // 모바일에서 받은 데이터에 startMonitoring 이 확인되면, 측정 시작
            if (message.equals("startMonitoring")) {
                movieTitle.setText(title);
                movieTime.setText(time);
                mTextView.setText("측정중");

                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
                calendar.set(Calendar.MINUTE, Integer.parseInt(minute));
                calendar.set(Calendar.SECOND, 0);

                triggerTime = calendar.getTimeInMillis(); // 끝나는 시간

                startSensor();
            }

        } catch (NullPointerException ex) {
        }
    }

    // 측정 시작
    private void startSensor() {
        mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));

        Sensor mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        mSensorManager.registerListener(this, mHeartRateSensor,SensorManager.SENSOR_DELAY_FASTEST);
    }

    // 측정 중단
    private void stopOnClick() {
        mSensorManager.unregisterListener(this);
        bpmData = bpmData.substring(0, bpmData.length()-1);
        mTextView.setText(bpmData);
    }

    // 센서값 저장
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE){
            String msg = (int) event.values[0] + " Bpm";
            bpmData += (int)event.values[0] + ",";
            System.out.println(event.values[0]);  //이거 값을 전달해야

            // 현재시간과 끝나는 시간 비교해서 심박수 더 측정할 건지 유무 결정

            long currentTime = System.currentTimeMillis();  //현재 시간

            if(currentTime>=triggerTime){
                onStartMobileActivityClick();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    // 권한 요청
    public void permissionRequest(){

        if (checkSelfPermission(Manifest.permission.BODY_SENSORS)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{Manifest.permission.BODY_SENSORS},
                    Sensor.TYPE_HEART_RATE);
        }
    }

    // 모바일로 데이터 전송
    public void onStartMobileActivityClick() {
        System.out.println("mobile Start");
        stopOnClick();

        new StartWearableActivityTask().execute();
    }

    // 모바일로 데이터 전송하는 클래스
    private class StartWearableActivityTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... args) {
            Collection<String> nodes = getNodes();
            System.out.println(nodes);
            for (String node : nodes) {
                System.out.println(node.getBytes());
                sendStartActivityMessage(node);
            }
            return null;
        }
    }

    //기기 목록 가져오기
    @WorkerThread
    private Collection<String> getNodes() {
        HashSet<String> results = new HashSet<>();

        Task<List<Node>> nodeListTask =
                Wearable.getNodeClient(getApplicationContext()).getConnectedNodes();

        try {
            List<Node> nodes = Tasks.await(nodeListTask);

            for (Node node : nodes) {
                results.add(node.getId());
                System.out.println(node.getDisplayName());
            }

        } catch (ExecutionException exception) {
            System.out.println("Task failed : " + exception);

        } catch (InterruptedException exception) {
            System.out.println("Interrupt occurred : " + exception);
        }

        return results;
    }

    // 모바일로 데이터 전송 시작
    @WorkerThread
    private void sendStartActivityMessage(String node) {

        Task<Integer> sendMessageTask =
                Wearable.getMessageClient(this).sendMessage(node, START_ACTIVITY_PATH, bpmData.getBytes());

        // 전송 유무 체크
        sendMessageTask.addOnSuccessListener(new OnSuccessListener<Integer>() {
            @Override
            public void onSuccess(Integer integer) {
                System.out.println("message sent success");
                finish();
            }
        });

        try {
            Integer result = Tasks.await(sendMessageTask);
            System.out.println("message sent : " + result);

        } catch (ExecutionException exception) {
            System.out.println("task failed : " + exception);

        } catch (InterruptedException exception) {
            System.out.println("interrupt occrurred : " + exception);
        }
    }
}