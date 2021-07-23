package com.example.bpm_service.scan;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.bpm_service.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.googlecode.tesseract.android.TessBaseAPI;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScanActivity extends AppCompatActivity {

    private Button applyButton, cancelButton;
    private ImageButton qrButton;
    private TextView titleText, timeText;

    private String userId="";
    private String translateText="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("QR 영화 등록");

        // 촬영 시작
        qrButton = findViewById(R.id.qrStart);

        //결과 텍스트
        titleText = findViewById(R.id.titleText);
        timeText = (TextView) findViewById(R.id.timeText);

        cancelButton = findViewById(R.id.cancel_button);
        applyButton = findViewById(R.id.apply_button);

        Intent intent = getIntent();

        if(intent != null){
            userId = intent.getStringExtra("userId");
            translateText = intent.getStringExtra("translateText");
            showData(translateText);
        }

        // 취소 버튼
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 적용 버튼
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // 재인식 하기 위한 포스터 클릭 리스너
        qrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ChooseScanActivity.class);
                intent.putExtra("userId", userId);
                startActivity(intent);
                finish();
            }
        });

        titleText.setText(translateText);
    }

    public void showData(String data){
        System.out.println(data);

        // 영화 제목 처리


        //시간 처리
        data = data.replaceAll("\\(오전\\)","");
        data = data.replaceAll("\\(오후\\)","");
        data = data.replaceAll(" ","");

        String movieTime = "";
        Pattern p = Pattern.compile("\\d{2}:\\d{2}~\\d{2}:\\d{2}");
        Matcher mMatcher = p.matcher(data);
        if(mMatcher.find()){
            movieTime = mMatcher.group();
        }else{
            System.out.println("찾을 수 없습니다");
        }
        timeText.setText(movieTime);
    }
}