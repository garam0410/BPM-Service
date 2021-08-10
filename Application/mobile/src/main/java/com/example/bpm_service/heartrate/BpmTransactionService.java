package com.example.bpm_service.heartrate;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

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

public class BpmTransactionService extends Service implements
        DataClient.OnDataChangedListener,
        MessageClient.OnMessageReceivedListener,
        CapabilityClient.OnCapabilityChangedListener{

    public BpmTransactionService() {
    }

    String IP = "";
    String userId = "";
    String title = "";

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flag, int startId){
        boolean state = intent.getExtras().getBoolean("bpmStart");
        IP = intent.getExtras().getString("IP","");
        userId = intent.getExtras().getString("userId","");
        title = intent.getExtras().getString("title","");

        if(state){
            System.out.println("측정예약을 위해 백그라운드 작업 진입");
            Wearable.getMessageClient(this).addListener(this);

        }else{
            System.out.println("측정 종료 혹은 취소로 인한 백그라운드 작업 취소");
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onCapabilityChanged(@NonNull @NotNull CapabilityInfo capabilityInfo) {

    }

    @Override
    public void onDataChanged(@NonNull @NotNull DataEventBuffer dataEventBuffer) {

    }

    @Override
    public void onMessageReceived(@NonNull @NotNull MessageEvent messageEvent) {
//        Intent intent = new Intent(this,ConnectWearableActivity.class);
//        intent.putExtra("bpmData", new String((messageEvent.getData())));
//        startActivity(intent);

        SocialServer socialServer = new SocialServer(IP);
        try{
            JSONObject json = new JSONObject();
            json.put("title", title);
            json.put("userId", userId);
            json.put("bpm", new String(messageEvent.getData()));

            socialServer.sendBpm(json.toString());

            Intent intent = new Intent(this,BpmTransactionService.class);
            intent.putExtra("bpmStart", false);
            startService(intent);
            
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}