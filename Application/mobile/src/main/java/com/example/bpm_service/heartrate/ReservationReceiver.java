package com.example.bpm_service.heartrate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class ReservationReceiver extends BroadcastReceiver {

    private static final String TAG = ReservationReceiver.class.getSimpleName();
    Context mContext;

    SharedPreferences pref;
    String get_state;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");
    }

    private void AlarmReceiverChk(final Context context, final Intent intent){
        Log.d(TAG, "Alarm Receiver started!");
        switch (get_state){
            case "ALARM_ON":
                // RingtoneService 서비스 intent 생성
                Intent serviceIntent = new Intent(mContext, ReservationService.class);
                serviceIntent.putExtra("state", get_state);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
                    context.startForegroundService(serviceIntent);
                } else {
                    context.startService(serviceIntent);
                }
                break;
            case "ALARM_OFF": // stopService 가 동작하지 않아서 startService 로 처리하고 나서....
                Intent stopIntent = new Intent(context, ReservationService.class);
                stopIntent.putExtra("state", get_state);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
                    context.startForegroundService(stopIntent);
                } else {
                    context.startService(stopIntent);
                }
                break;
        }
    }
}