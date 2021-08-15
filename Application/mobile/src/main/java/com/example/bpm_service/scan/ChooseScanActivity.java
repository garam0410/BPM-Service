package com.example.bpm_service.scan;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
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
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.example.bpm_service.R;
import com.google.zxing.integration.android.IntentIntegrator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

public class ChooseScanActivity extends AppCompatActivity {

    private final int GET_GALLERY_IMAGE = 200;

    private String imageFilePath;
    private ImageButton newButton,loadButton;

    Bitmap image;


    private String userId = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_scan);
        SharedPreferences sharedPreferences;

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("QR 영화 등록");

        Intent intent = getIntent();
        if(intent!=null){
            userId = intent.getStringExtra("userId");
        }

        newButton = (ImageButton) findViewById(R.id.newPicture);
        loadButton = (ImageButton) findViewById(R.id.loadPicture);

        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),QrActivity.class);
                intent.putExtra("userId",userId);
                startActivity(intent);
            }
        });

        // QR 사진 불러오기 버튼
        loadButton = (ImageButton) findViewById(R.id.loadPicture);
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                PermissionCheck();
//                Intent intent = new Intent(Intent.ACTION_PICK);
//                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
//                startActivityForResult(intent, GET_GALLERY_IMAGE);
                Toast.makeText(getApplicationContext(),"준비중입니다!",Toast.LENGTH_LONG).show();
            }
        });
    }

    // 카메라 및 앨범 권한 체크
    public void PermissionCheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED &&
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED &&
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                // 권한 없음
                ActivityCompat.requestPermissions(ChooseScanActivity.this,
                        new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        2);
            } else {
                // 권한 있음
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri selectedImageUri = data.getData();
            try {
                image = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                imageFilePath = selectedImageUri.getPath();

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
                byte[] bytes = byteArrayOutputStream.toByteArray();
                String temp = Base64.getEncoder().encodeToString(bytes);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}