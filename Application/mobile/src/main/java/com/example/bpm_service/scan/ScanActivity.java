package com.example.bpm_service.scan;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.bpm_service.R;

public class ScanActivity extends AppCompatActivity {

    private Button applyButton, cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("QR 영화 등록");

        cancelButton = (Button) findViewById(R.id.cancel_button);
        applyButton = (Button) findViewById(R.id.apply_button);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}