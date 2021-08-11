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

public class BpmTransactionService extends Service implements
        DataClient.OnDataChangedListener,
        MessageClient.OnMessageReceivedListener,
        CapabilityClient.OnCapabilityChangedListener{

    public BpmTransactionService() {
    }

    String IP = "";
    String userId = "";
    String title = "";
    String time = "";

    String hour, minute;

    private static final String TAG = BpmTransactionService.class.getSimpleName();
    Context mContext;
    AlarmManager alarm_manager;

    Intent alarmIntent;
    PendingIntent pendingIntent;
    private static final int REQUEST_CODE = 1111;
    SharedPreferences pref;

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
        time = intent.getExtras().getString("time","");

        if(state){
            System.out.println("측정예약을 위해 백그라운드 작업 진입");
            Wearable.getMessageClient(this).addListener(this);

            setAlarm(time);
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

        SocialServer socialServer = new SocialServer(IP);
        try{
            JSONObject json = new JSONObject();
            json.put("title", title);
            json.put("userId", userId);
            json.put("bpm", new String(messageEvent.getData()));

            System.out.println(json.toString());

            socialServer.sendBpm(json.toString());

            Intent intent = new Intent(this,BpmTransactionService.class);
            intent.putExtra("bpmStart", false);
            startService(intent);

            SharedPreferences reservationData = getSharedPreferences("reservationData", MODE_PRIVATE);
            SharedPreferences.Editor editor = reservationData.edit();
            editor.clear();
            editor.commit();
            editor.putBoolean("reservationState", false);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void setAlarm(String time){
        // Calendar 객체 생성
        final Calendar calendar = Calendar.getInstance();

        String[] splitTime = time.split("~");

        time = splitTime[0];
        System.out.println(time);

        splitTime = time.split(":");
//        hour = splitTime[0];
//        minute = splitTime[1];

        hour = "17";
        minute = "54";

        // calendar에 시간 셋팅
//        if (Build.VERSION.SDK_INT < 23) {
//            // 시간 가져옴
//            getHourTimePicker = alarm_timepicker.getCurrentHour();
//            getMinuteTimePicker = alarm_timepicker.getCurrentMinute();
//        } else {
//            // 시간 가져옴
//            getHourTimePicker = alarm_timepicker.getHour();
//            getMinuteTimePicker = alarm_timepicker.getMinute();
//        }

        // 현재 지정된 시간으로 알람 시간 설정
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour));
        calendar.set(Calendar.MINUTE, Integer.parseInt(minute));
        calendar.set(Calendar.SECOND, 0);

        pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putInt("set_hour", Integer.parseInt(hour));
        editor.putInt("set_min", Integer.parseInt(minute));
        editor.putString("state", "ALARM_ON");
        editor.commit();

        alarm_manager = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        alarmIntent = new Intent(this, ReservationReceiver.class);

        // reveiver에 string 값 넘겨주기
        alarmIntent.putExtra("state","ALARM_ON");

        // receiver를 동작하게 하기 위해 PendingIntent의 인스턴스를 생성할 때, getBroadcast 라는 메소드를 사용
        // requestCode는 나중에 Alarm을 해제 할때 어떤 Alarm을 해제할지를 식별하는 코드
        pendingIntent = PendingIntent.getBroadcast(this,REQUEST_CODE,alarmIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        long currentTime = System.currentTimeMillis(); // 현재 시간
        //long triggerTime = SystemClock.elapsedRealtime() + 1000*60;
        long triggerTime = calendar.getTimeInMillis(); // 알람을 울릴 시간
        System.out.println(triggerTime);
        System.out.println(currentTime);
        long interval = 1000 * 60 * 60  * 24; // 하루의 시간

        while(currentTime > triggerTime){ // 현재 시간보다 작다면
            triggerTime += interval; // 다음날 울리도록 처리
        }
        Log.e(TAG, "set Reservation : " + Integer.parseInt(hour) + "시 " + Integer.parseInt(minute) + "분");

        // 알림 세팅 : AlarmManager 인스턴스에서 set 메소드를 실행시키는 것은 단발성 Alarm을 생성하는 것
        // RTC_WAKEUP : UTC 표준시간을 기준으로 하는 명시적인 시간에 intent를 발생, 장치를 깨움
        if (Build.VERSION.SDK_INT < 23) {
            if (Build.VERSION.SDK_INT >= 19) {
                alarm_manager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            } else {
                // 알람셋팅
                alarm_manager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            }
        } else {  // 23 이상
            alarm_manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
            //alarm_manager.set(AlarmManager.RTC_WAKEUP, triggerTime,pendingIntent);
            //알람 매니저를 통한 반복알람 설정
            //alarm_manager.setRepeating(AlarmManager.RTC, triggerTime, interval, pendingIntent);
            // interval : 다음 알람이 울리기까지의 시간
        }

        // Unable to find keycodes for AM and PM.
//        if(getHour33TimePicker > 12){
//            am_pm = "오후";
//            getHourTimePicker = getHourTimePicker - 12;
//        } else {
//            am_pm ="오전";
//        }
    }

    public void releaseAlarm(Context context)  {
        Log.e(TAG, "unregisterAlarm");

        pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("state", "ALARM_OFF");
        editor.commit();

        // 알람매니저 취소
        alarm_manager.cancel(pendingIntent);
        alarmIntent.putExtra("state","ALARM_OFF");

        // 알람 취소
        sendBroadcast(alarmIntent);
    }
}