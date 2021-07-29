package com.example.bpm_service.heartrate;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bpm_service.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class ConnectWearableActivity extends AppCompatActivity {

    private TextView mTextView;

    public static final String WEARABLE_DATA_PATH= "/mypath";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_wearable);


        mTextView = (TextView) findViewById(R.id.textView5);
    }

    public void sendOnClick(View view) {
        mTextView.setText("연결중");
        new SendThread(WEARABLE_DATA_PATH, "startMonitoring").start();

    }

    public class SendThread extends Thread {
        String path;
        String message;
        public SendThread(String path, String message) {

            this.path = path;
            this.message = message;
        }
        @Override
        public void run() {

            //노드로 알려진 연결된 장치 검색

            Task<List<Node>> wearableList =
                    Wearable.getNodeClient(getApplicationContext()).getConnectedNodes();
            try {
                List<Node> nodes = Tasks.await(wearableList);
                for (Node node : nodes) {
                    Task<Integer> sendMessageTask = Wearable.getMessageClient(ConnectWearableActivity.this).sendMessage(node.getId(), path, message.getBytes());
                    System.out.println(node.getDisplayName());
                    sendMessageTask.addOnSuccessListener(new OnSuccessListener<Integer>() {
                        @Override
                        public void onSuccess(Integer integer) {
                            System.out.println("success");
                        }
                    });
                }
            } catch (ExecutionException exception) {

            } catch (InterruptedException exception) {
            }
        }
    }


}