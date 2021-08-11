package com.example.bpm_service.heartrate;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.WorkerThread;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ReservationService extends Service {

    private static final String TAG = ReservationService.class.getSimpleName();
    private static final String START_ACTIVITY_PATH = "/start-activity";

    public ReservationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand startID === "+startId); // 계속 증가되는 값

        String getState = intent.getExtras().getString("state");
        Log.e("TAG","onStartCommand getState : " + getState);

        switch (getState) {
            case "ALARM_ON":
                System.out.println("측정시작");
                onStartWearableActivityClick();
                break;

            case "ALARM_OFF":
                Log.e(TAG, "onStartCommand Stoped!");

                stopSelf();
                break;
            default:
                break;
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Action Service Ended");
        super.onDestroy();
        stopForeground(true);
    }


    public void onStartWearableActivityClick() {
        System.out.println("wearable Start");

        new StartWearableActivityTask().execute();
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