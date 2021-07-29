package com.example.bpm_service;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class MainActivity extends WearableActivity implements SensorEventListener {

    public static final String WEARABLE_DATA_PATH = "/mypath";
    public static final String WEARABLE_DATA_PATH_Result = "/mypath/result";

    private TextView mTextView;
    private ArrayList<Integer> list;
    private SensorManager mSensorManager;

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
                stopOnClick();
            }
        });


        try {
            String message = getIntent().getStringExtra("message");
            //String message = "startMonitoring";
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
        mTextView.setText("멈춥니다");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE){
            String msg = (int) event.values[0] + " Bpm";
            mTextView.setText(msg);
            list.add((int) event.values[0]);
            System.out.println(event.values[0]);  //이거 값을 전달해야
        }

    }

    // intent.putExtra(RESULT_KEY, event.values[0]);


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
}