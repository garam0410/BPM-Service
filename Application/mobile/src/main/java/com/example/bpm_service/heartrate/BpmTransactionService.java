package com.example.bpm_service.heartrate;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.bpm_service.connection.MovieInformationServer;
import com.example.bpm_service.connection.SocialServer;
import com.example.bpm_service.uinfo.ReservationActivity;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.Calendar;

// 심박수 처리 서비스
public class BpmTransactionService extends Service implements
        DataClient.OnDataChangedListener,
        MessageClient.OnMessageReceivedListener,
        CapabilityClient.OnCapabilityChangedListener{

    public BpmTransactionService() {
    }

    String IP = ""; // 서버 IP
    String userId = ""; // 사용자 ID
    String title = ""; // 영화 제목
    String time = ""; // 영화 running time

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    // 서비스 시작 요청시,
    @Override
    public int onStartCommand(Intent intent, int flag, int startId){

        // 데이터 가져오기
        boolean state = intent.getExtras().getBoolean("bpmStart");
        IP = intent.getExtras().getString("IP","");
        userId = intent.getExtras().getString("userId","");
        title = intent.getExtras().getString("title","");
        time = intent.getExtras().getString("time","");

        if(state){ // 백그라운드 작업 시작
            System.out.println("측정예약을 위해 백그라운드 작업 진입");
            Wearable.getMessageClient(this).addListener(this);
            return START_NOT_STICKY;
        }else{// 백그라운드 작업 종료
            System.out.println("측정 종료 혹은 취소로 인한 백그라운드 작업 취소");
        }

        return super.onStartCommand(intent, flag, startId);
    }

    @Override
    public void onCapabilityChanged(@NonNull CapabilityInfo capabilityInfo) {

    }

    @Override
    public void onDataChanged(@NonNull DataEventBuffer dataEventBuffer) {

    }

    // 스마트워치로부터 심박수 데이터를 받았을 때
    @Override
    public void onMessageReceived(@NonNull MessageEvent messageEvent) {

        // 심박수 데이터 전속을 위한 Server 객체 생성
        SocialServer socialServer = new SocialServer(IP);
        try{
            // JSON에 데이터 담기
            JSONObject json = new JSONObject();
            json.put("title", title);
            json.put("userId", userId);
            json.put("bpm", new String(messageEvent.getData()));

            System.out.println(json.toString());

            // 데이터 전송
            socialServer.sendBpm(json.toString());

            // 백그라운드 작업 종료를 위한 서비스 실행
            Intent intent = new Intent(this,BpmTransactionService.class);
            intent.putExtra("bpmStart", false);
            startService(intent);

            // 예약 된 영화 초기화
            SharedPreferences reservationData = getSharedPreferences("reservationData", MODE_PRIVATE);
            SharedPreferences.Editor editor = reservationData.edit();
            editor.clear();
            editor.commit();
            editor.putBoolean("reservationState", false);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}