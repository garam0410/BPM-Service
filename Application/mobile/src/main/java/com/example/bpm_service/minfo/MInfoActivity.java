package com.example.bpm_service.minfo;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.bpm_service.MainActivity;
import com.example.bpm_service.R;

public class MInfoActivity extends AppCompatActivity {

    private String userId;
    private String title, image, director, actor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_info);

        // 상단바 제거
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Intent intent = getIntent();
        if(intent != null){

        }
    }
}