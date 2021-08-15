package com.example.bpm_service.uinfo;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.bpm_service.R;
import com.example.bpm_service.heartrate.BpmTransactionService;
import com.example.bpm_service.minfo.MInfoActivity;
import com.example.bpm_service.scan.QrActivity;

public class ReservationActivity extends AppCompatActivity {

    private Button bpmState;
    private TextView movieTitle, movieTime, movieDate;
    private ImageButton movieImage;

    private String title, image, time, date;
    private String IP ="";
    private String data = "";
    private String userId = "";

    private SharedPreferences reservationData;
    private boolean reservationState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation);

        bpmState = (Button) findViewById(R.id.bpmState);
        movieTitle = (TextView) findViewById(R.id.movieTitle);
        movieDate = (TextView) findViewById(R.id.movieDate);
        movieTime = (TextView) findViewById(R.id.movieTime);

        movieImage = (ImageButton) findViewById(R.id.movieImage);

        // 예약된 영화 정보 가져오기
        reservationData = this.getSharedPreferences("reservationData", Activity.MODE_PRIVATE);
        if(reservationData != null){
            userId = reservationData.getString("userId","");
            title = reservationData.getString("title","");
            date = reservationData.getString("date","");
            time = reservationData.getString("time","");
            image = reservationData.getString("image","");
            reservationState = reservationData.getBoolean("reservationState",false);

            movieTitle.setText(title);
            movieDate.setText(date);
            movieTime.setText(time);
            Glide.with(this).load(image).into(movieImage);

        }

        movieImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MInfoActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("title", title);
                startActivity(intent);
            }
        });

        // 예약 취소 버튼
        bpmState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ReservationActivity.this).setTitle(title).setMessage(time + "\n 예약을 취소하시겠습니까?")
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(ReservationActivity.this, BpmTransactionService.class);
                                intent.putExtra("bpmStart",false);
                                startService(intent);

                                reservationData = getSharedPreferences("reservationData", MODE_PRIVATE);
                                SharedPreferences.Editor editor = reservationData.edit();
                                editor.clear();
                                editor.commit();
                                editor.putBoolean("reservationState", false);
                                finish();
                            }
                        }).setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
            }
        });
    }
}