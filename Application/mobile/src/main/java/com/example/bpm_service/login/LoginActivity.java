package com.example.bpm_service.login;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.bpm_service.MainActivity;
import com.example.bpm_service.R;
import com.example.bpm_service.connection.UserManagementServer;

public class LoginActivity extends AppCompatActivity {

    // 컴포넌트 정의
    private Button login, register, find;
    private EditText setUserId, setUserPw;
    private CheckBox auto_LoginCheck;

    private ProgressDialog progressDialog;

    //자동 로그인 확인 변수
    private SharedPreferences appData;
    private boolean saveLoginData;

    // EditText 값 변수 선언
    String userId, userPw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //상단바 숨기기
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        // 로딩 정의
        progressDialog = new ProgressDialog(this);

        // IP
        String IP = getResources().getString(R.string.IP);

        // ID 연결
        login = (Button) findViewById(R.id.Login);
        register = (Button) findViewById(R.id.Resister);
        find = (Button) findViewById(R.id.findIdPw);

        setUserId = (EditText) findViewById(R.id.setUserId);
        setUserPw = (EditText) findViewById(R.id.setUserPw);

        auto_LoginCheck = (CheckBox) findViewById(R.id.autoLoginCheck);

        //자동로그인
        Intent intent_2 = getIntent();
        saveLoginData = intent_2.getBooleanExtra("SAVE_LOGIN_DATA", false);

        //계정 정보 저장을 위한 SharedPreferences 생성
        appData = getSharedPreferences("appData", MODE_PRIVATE);
        load();

        if(saveLoginData){
            setUserId.setText(userId);
            setUserPw.setText(userPw);
            auto_LoginCheck.setChecked(saveLoginData);
            login(IP);
        }

        // 로그인 버튼 눌렀을 때,
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login(IP);
            }
        });

        // 회원가입 버튼 눌렀을 때, RegisterActivity로 이동
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        // ID/PW 찾기 버튼 눌렀을 때, FindActivity로 이동
        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FindActivity.class);
                startActivity(intent);
            }
        });
    }

    // DialogHandler
    public void alertHandler(Boolean tf, String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle(title).setMessage(message);

        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(tf){
                    finish();
                }
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    //로그인
    public void login(String IP){

        progressDialog.setMessage("로그인 중입니다.....");
        progressDialog.show();

        // 로그인을 위한 ID, PW 값 가져오기
        userId = setUserId.getText().toString();
        userPw = setUserPw.getText().toString();

        if((userId.equals("") || userPw.equals("")) || (userId.equals("") && userPw.equals(""))){
            alertHandler(false,"입력 오류", "항목을 모두 입력해 주세요.");
        }
        else{
            // 서버 통신 URL 구성
            UserManagementServer userManagementServer = new UserManagementServer(IP);

            // 통신 결과 반환
            String result = userManagementServer.login(userId, userPw);

            //로그인 성공
            if(result.trim().equals("Success")){
                save();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("SAVE_LOGIN_DATA",saveLoginData);
                intent.putExtra("userId", userId);
                startActivity(intent);
                finish();
            }

            // 로그인 실패 (정보 없음)
            else if(result.trim().equals("Empty")){
                alertHandler(false,"로그인 실패", "회원이 아니거나, 입력하신 정보가 올바르지 않습니다.");
                progressDialog.cancel();
            }

            // 로그인 실패 (서버 에러)
            else{
                alertHandler(false,result.trim(),result.trim());
                progressDialog.cancel();
            }
        }
    }
    //계정 정보 저장
    private void save(){
        SharedPreferences.Editor editor = appData.edit();

        editor.putBoolean("SAVE_LOGIN_DATA", auto_LoginCheck.isChecked());
        editor.putString("userId", setUserId.getText().toString().trim());
        editor.putString("userPw", setUserPw.getText().toString().trim());

        editor.apply();
    }

    //저장된 정보 불러오기
    public void load(){
        saveLoginData = appData.getBoolean("SAVE_LOGIN_DATA", false);
        userId = appData.getString("userId", "");
        userPw = appData.getString("userPw", "");
    }
}