package com.example.bpm_service;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.bpm_service.home.HomeActivity;
import com.example.bpm_service.minfo.MInfoActivity;
import com.example.bpm_service.search.SearchActivity;
import com.example.bpm_service.uinfo.MyPageActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    // fragment manager 생성
    private FragmentManager fragmentManager;

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

        //상단바 숨기기
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

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


        //첫화면을 홈으로
        setFrag(1);
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
}