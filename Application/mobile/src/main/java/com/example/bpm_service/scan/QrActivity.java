package com.example.bpm_service.scan;

import androidx.annotation.WorkerThread;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bpm_service.R;
import com.example.bpm_service.connection.MovieInformationServer;
import com.example.bpm_service.heartrate.ConnectWearableActivity;
import com.example.bpm_service.minfo.MInfoActivity;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class QrActivity extends AppCompatActivity {
    private TextView movieTitle, movieTime;
    private Button buttonScan;
    private IntentIntegrator qrScan;
    private ImageButton movieImage;

    private String title, image, time;
    private String IP ="";
    private String data = "";
    private String userId = "";

    private static final String START_ACTIVITY_PATH = "/start-activity";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        IP = getResources().getString(R.string.IP);

        buttonScan = (Button) findViewById(R.id.buttonScan);
        movieTitle = (TextView) findViewById(R.id.textViewName);
        movieTime = (TextView) findViewById(R.id.textViewAddress);

        movieImage = (ImageButton) findViewById(R.id.movieImage);

        qrScan = new IntentIntegrator(this);

        Intent intent = getIntent();
        if(intent !=null)
            userId = intent.getStringExtra("userId");

        buttonScan.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                qrScan.initiateScan();
            }
        });

        movieImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MInfoActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("title", title);
                startActivity(intent);
            }
        });

        qrScan.setPrompt("Scanning");
        qrScan.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //qrcode 가 없으면
            if (result.getContents() == null) {
                Toast.makeText(QrActivity.this, "취소!", Toast.LENGTH_SHORT).show();

            } else {
                //qrcode 결과가 있으면
                Toast.makeText(QrActivity.this, "스캔완료!", Toast.LENGTH_SHORT).show();
                try {
                    //data를 json으로 변환
                    JSONObject obj = new JSONObject(result.getContents());
                    title = obj.getString("name");
                    time = obj.getString("time");
                    movieTitle.setText(title);
                    movieTime.setText(time);

                    getImage(title);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void getImage(String title){
        // 데이터 불러오기
        MovieInformationServer movieInformationServer = new MovieInformationServer(IP);
        data = movieInformationServer.search(title);

        // 이미지 적용
        try {
            JSONArray json = new JSONArray(data);
            for(int i = 0; i<json.length(); i++){

                JSONObject obj = (JSONObject)json.get(i);
                image = obj.getString("image");
            }
            Glide.with(this).load(image).into(movieImage);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onStartWearableActivityClick(View view) {
        System.out.println("wearable Start");

        new QrActivity.StartWearableActivityTask().execute();
    }

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
    private void sendStartActivityMessage(String node) {

        Task<Integer> sendMessageTask =
                Wearable.getMessageClient(this).sendMessage(node, START_ACTIVITY_PATH, "startMonitoring".getBytes());

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