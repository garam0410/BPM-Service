/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.bpm_service;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DataLayerListenerService extends WearableListenerService {

    private static final String TAG = "DataLayerService";

    private static final String START_ACTIVITY_PATH = "/start-activity";
//
//
//    @Override
//    public void onDataChanged(DataEventBuffer dataEvents) {
//        Log.d(TAG, "onDataChanged: " + dataEvents);
//
//        for (DataEvent event : dataEvents) {
//            Uri uri = event.getDataItem().getUri();
//            String path = uri.getPath();
//
//            if (COUNT_PATH.equals(path)) {
//
//                String nodeId = uri.getHost();
//
//                byte[] payload = uri.toString().getBytes();
//
//                Task<Integer> sendMessageTask =
//                        Wearable.getMessageClient(this)
//                                .sendMessage(nodeId, DATA_ITEM_RECEIVED_PATH, payload);
//
//                sendMessageTask.addOnCompleteListener(
//                        new OnCompleteListener<Integer>() {
//                            @Override
//                            public void onComplete(Task<Integer> task) {
//                                if (task.isSuccessful()) {
//                                    Log.d(TAG, "Message sent successfully");
//                                } else {
//                                    Log.d(TAG, "Message failed.");
//                                }
//                            }
//                        });
//            }
//        }
//    }

    // 모바일에서 측정 시작 신호를 받으면 동작
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "onMessageReceived: " + messageEvent.getData());
        String message = "";
        String title = "", time = "";

        try {
            JSONObject json = new JSONObject(new String((messageEvent.getData())));
            message = json.getString("message");
            title = json.getString("title");
            time = json.getString("time");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 심박수 측정 Activity 시작
        if (messageEvent.getPath().equals(START_ACTIVITY_PATH)) {
            Intent startIntent = new Intent(this, MainActivity.class);
            startIntent.putExtra("message", message);
            startIntent.putExtra("title", title);
            startIntent.putExtra("time", time);
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startIntent);
        }
    }
}
