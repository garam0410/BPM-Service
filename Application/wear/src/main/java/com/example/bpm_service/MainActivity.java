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
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class MainActivity extends WearableActivity implements SensorEventListener{

    private static final String START_ACTIVITY_PATH = "/start-activity";
    public static final String WEARABLE_DATA_PATH = "/mypath";
    public static final String WEARABLE_DATA_PATH_Result = "/mypath/result";

    private TextView mTextView;
    private ArrayList<Integer> list;
    private SensorManager mSensorManager;

    private String bpmData = "";

    private Button mBut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.textView4);
        mBut = (Button) findViewById(R.id.button);

        mBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onStartMobileActivityClick();
            }
        });


        try {
            String message = getIntent().getStringExtra("message");
            message = "startMonitoring";
            if (message == null) {
                mTextView.setText("The app must be launched from the mobile.");
                try {
                    Thread.sleep(3000);  //스레드는 1초동안 일시 정지 상태가 된다.
                } catch (InterruptedException e) {
                    e.printStackTrace();    //예외처리가 필요하다.
                }
            }

            setAmbientEnabled();
            permissionRequest();

            if (message.equals("startMonitoring")) {
                mTextView.setText("심박수를 측정하는 중입니다.");
                startSensor();
            }

        } catch (NullPointerException ex) {
        }
    }

    private void startSensor() {
        list = new ArrayList<>();
        mSensorManager = ((SensorManager) getSystemService(SENSOR_SERVICE));

        Sensor mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        mSensorManager.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void stopOnClick() {
        mSensorManager.unregisterListener(this);
        bpmData = bpmData.substring(0, bpmData.length()-1);
        mTextView.setText(bpmData);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE){
            String msg = (int) event.values[0] + " Bpm";
            mTextView.setText(msg);
            bpmData += (int)event.values[0] + ",";
            //list.add((int) event.values[0]);
            System.out.println(event.values[0]);  //이거 값을 전달해야
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void permissionRequest(){

        if (checkSelfPermission(Manifest.permission.BODY_SENSORS)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(
                    new String[]{Manifest.permission.BODY_SENSORS},
                    Sensor.TYPE_HEART_RATE);
        }
    }

    TextView tvDate;

    @Override   //현재시간 코드
    protected void onResume() {
        super.onResume();
        tvDate = (TextView) findViewById(R.id.textView2);
        tvDate.setText(getTime());
        //Text에 시간 세팅
    }

    //현재 시간을 yyyy-MM-dd hh:mm::ss"로 표시하는 메소드
    private String getTime() {
        long now = System.currentTimeMillis();  //현재 시간을 long 변수에 넣어준다
        Date date = new Date(now);  //Date 형식으로 Convert
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String getTime = dateFormat.format(date);
        return getTime;
    }
    ///////////////////////////////////////////////////////////

    /** Sends an RPC to start a fullscreen Activity on the wearable. */
    public void onStartMobileActivityClick() {
        System.out.println("mobile Start");
        stopOnClick();

        new StartWearableActivityTask().execute();
    }

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

    @WorkerThread
    private void sendStartActivityMessage(String node) {

        Task<Integer> sendMessageTask =
                Wearable.getMessageClient(this).sendMessage(node, START_ACTIVITY_PATH, bpmData.getBytes());

        sendMessageTask.addOnSuccessListener(new OnSuccessListener<Integer>() {
            @Override
            public void onSuccess(Integer integer) {
                System.out.println("message sent success");
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