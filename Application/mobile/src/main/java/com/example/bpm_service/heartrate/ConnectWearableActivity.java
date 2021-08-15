package com.example.bpm_service.heartrate;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bpm_service.R;
import com.example.bpm_service.scan.QrActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.internal.ConnectionCallbacks;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;

// 지정된 시간에 스마트워치의 심박수 측정 시작 신호를 보내는 액티비티
public class ConnectWearableActivity extends AppCompatActivity{

    // 스마트워치에서 액티비티 시작을 알리는 Path
    private static final String START_ACTIVITY_PATH = "/start-activity";

    // 영화 제목 및 시간
    private String title, time;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_wearable);

        // 스마트워치로 보낼 데이터를 받아오기
        Intent intent = getIntent();
        if(intent != null){
            title = intent.getExtras().getString("title");
            time = intent.getExtras().getString("time");
        }

        // 워치 액티비티 시작
        onStartWearableActivityClick();

        // 시작 신호 보내고 현재 액티비티는 종료
        finish();
    }

    // 액티비티 시작 신호 전송
    public void onStartWearableActivityClick() {
        System.out.println("wearable Start");

        new StartWearableActivityTask().execute();
    }

    // 스마트워치 기기를 찾고 데이터를 전송하는 클래스
    private class StartWearableActivityTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... args) {
            Collection<String> nodes = getNodes();
            for (String node : nodes) {
                sendStartActivityMessage(node);
            }
            return null;
        }
    }

    // 기기 찾기
    @WorkerThread
    private Collection<String> getNodes() {
        HashSet<String> results = new HashSet<>();

        Task<List<Node>> nodeListTask =
                Wearable.getNodeClient(getApplicationContext()).getConnectedNodes();

        try {
            List<Node> nodes = Tasks.await(nodeListTask);

            for (Node node : nodes) {
                results.add(node.getId());
            }

        } catch (ExecutionException exception) {
            System.out.println("Task failed : " + exception);

        } catch (InterruptedException exception) {
            System.out.println("Interrupt occurred : " + exception);
        }

        return results;
    }

    @WorkerThread
    private void sendStartActivityMessage(String node){

        try {
            JSONObject json = new JSONObject();
            json.put("title", title);
            json.put("time", time);
            json.put("message", "startMonitoring");

            // 스마트 워치로 시작신호 전송 Task
            Task<Integer> sendMessageTask =
                    Wearable.getMessageClient(this).sendMessage(node, START_ACTIVITY_PATH, json.toString().getBytes());
            Integer result = Tasks.await(sendMessageTask);

            System.out.println("message sent : " + result);

        } catch (ExecutionException exception) {
            System.out.println("task failed : " + exception);

        } catch (InterruptedException exception) {
            System.out.println("interrupt occrurred : " + exception);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}