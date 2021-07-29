package com.example.bpm_service;

import android.content.Intent;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class MyListenerService extends WearableListenerService {

    public static final String TAG = "dm";
    public static final String WEARABLE_DATA_PATH = "/mypath";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (messageEvent.getPath().equals(WEARABLE_DATA_PATH)) {
            System.out.println("받았다!");
            final String message = new String((messageEvent.getData()));

            if (message.equals("startMonitoring")) {

                Intent startIntent = new Intent (this, MainActivity.class);
                startIntent.putExtra("message", message);
                startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startIntent);
            }
        }else

            super.onMessageReceived(messageEvent);
    }
}