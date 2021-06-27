package com.example.bpm_service;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.bpm_service.home.HomeActivity;
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

    private FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        //첫화면을 홈으로
        setFrag(1);
    }

    private void setFrag(int n){
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();

        switch(n){
            case 0 :
                transaction.replace(R.id.mainFrame, fragment_search);
                transaction.commit();
                break;
            case 1 :
                transaction.replace(R.id.mainFrame, fragment_home);
                transaction.commit();
                break;
            case 2 :
                transaction.replace(R.id.mainFrame, fragment_myPage);
                transaction.commit();
                break;
        }
    }
}