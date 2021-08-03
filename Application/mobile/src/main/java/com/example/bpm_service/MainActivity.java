package com.example.bpm_service;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.bpm_service.heartrate.ConnectWearableActivity;
import com.example.bpm_service.home.HomeActivity;
import com.example.bpm_service.minfo.MInfoActivity;
import com.example.bpm_service.scan.ChooseScanActivity;
import com.example.bpm_service.scan.ScanActivity;
import com.example.bpm_service.search.SearchActivity;
import com.example.bpm_service.uinfo.MyPageActivity;
import com.google.android.gms.wearable.CapabilityClient;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity  implements
        DataClient.OnDataChangedListener,
        MessageClient.OnMessageReceivedListener,
        CapabilityClient.OnCapabilityChangedListener{

    private BottomNavigationView bottomNavigationView;

    // fragment manager 생성
    private FragmentManager fragmentManager;

    //QR 버튼
    private ImageButton qrButton;

    //심박수 측정 버튼
    private ImageButton heartButton;

    // fragment로 지정할 Activity
    private HomeActivity fragment_home;
    private SearchActivity fragment_search;
    private MyPageActivity fragment_myPage;
    private MInfoActivity fragment_minfo;

    private FragmentTransaction transaction;

    // 자동로그인 정보 저장
    private boolean SAVE_LOGIN_DATA;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        if(intent != null){
            SAVE_LOGIN_DATA = intent.getBooleanExtra("SAVE_LOGIN_DATA", false);
            userId = intent.getStringExtra("userId");
        }

        bottomNavigationView = findViewById(R.id.bottomNavigationBar);
        bottomNavigationView.setSelectedItemId(R.id.action_home);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.action_search :
                        setFrag(0);
                        break;
                    case R.id.action_home :
                        setFrag(1);
                        break;
                    case R.id.action_mypage :
                        setFrag(2);
                        break;
                }
                return true;
            }
        });

        fragment_search = new SearchActivity();
        fragment_home = new HomeActivity();
        fragment_myPage = new MyPageActivity();
        fragment_minfo = new MInfoActivity();
        Wearable.getMessageClient(this).addListener(this);
        //첫화면을 홈으로
        setFrag(1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar actionBar = getSupportActionBar();

        // Custom Actionbar를 사용하기 위해 CustomEnabled을 true 시키고 필요 없는 것은 false 시킨다
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);            //액션바 아이콘을 업 네비게이션 형태로 표시합니다.
        actionBar.setDisplayShowTitleEnabled(false);        //액션바에 표시되는 제목의 표시유무를 설정합니다.
        actionBar.setDisplayShowHomeEnabled(false);            //홈 아이콘을 숨김처리합니다.


        //layout을 가지고 와서 actionbar에 포팅을 시킵니다.
        LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
        View actionbar = inflater.inflate(R.layout.custom_actionbar, null);

        actionBar.setCustomView(actionbar);

        //액션바 양쪽 공백 없애기
        Toolbar parent = (Toolbar)actionbar.getParent();
        parent.setContentInsetsAbsolute(0,0);

        qrButton = (ImageButton) findViewById(R.id.qrButton);
        qrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent qr = new Intent(getApplicationContext(), ChooseScanActivity.class);
                qr.putExtra("userId", userId);
                startActivity(qr);
            }
        });

        heartButton = findViewById(R.id.heartButton);
        heartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(),ConnectWearableActivity.class);
                startActivity(intent);
            }
        });

        actionBar.show();
        return true;
    }

    private void setFrag(int n){
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        switch(n){
            case 0 :
                Bundle bundle_1 = new Bundle();
                bundle_1.putString("userId", userId);
                fragment_search.setArguments(bundle_1);
                transaction.replace(R.id.mainFrame, fragment_search);
                transaction.commit();
                break;
            case 1 :
                Bundle bundle_2 = new Bundle();
                bundle_2.putString("userId",userId);
                fragment_home.setArguments(bundle_2);
                transaction.replace(R.id.mainFrame, fragment_home);
                transaction.commit();
                break;
            case 2 :
                Bundle bundle_3 = new Bundle();
                bundle_3.putBoolean("SAVE_LOGIN_DATA", SAVE_LOGIN_DATA);
                fragment_myPage.setArguments(bundle_3);
                transaction.replace(R.id.mainFrame, fragment_myPage);
                transaction.commit();
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Wearable.getMessageClient(this).addListener(this);
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        System.out.println("진짜 메시지 받았다");
        Intent intent = new Intent(this,ConnectWearableActivity.class);
        intent.putExtra("bpmData", new String((messageEvent.getData())));
        startActivity(intent);
    }

    @Override
    public void onCapabilityChanged(CapabilityInfo capabilityInfo) {
        System.out.println("메시지 받았다");
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        System.out.println("메시지 받았다");
    }
}