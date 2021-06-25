package com.example.bpm_service.login;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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
import com.example.bpm_service.connection.ConnectServer;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {

    private Button login, register, find;
    private EditText setUserId, setUserPw;
    private CheckBox auto_LoginCheck;
    private ProgressDialog progressDialog;

    //자동 로그인 확인 변수
    private SharedPreferences appData;
    private boolean saveLoginData;

    // Edittext 값 변수 선언
    String userId, userPw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // IP
        String IP = getResources().getString(R.string.IP);

        login = (Button) findViewById(R.id.Login);
        register = (Button) findViewById(R.id.Resister);
        find = (Button) findViewById(R.id.findIdPw);

        setUserId = (EditText) findViewById(R.id.setUserId);
        setUserPw = (EditText) findViewById(R.id.setUserPw);

        auto_LoginCheck = (CheckBox) findViewById(R.id.autoLoginCheck);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userId = setUserId.getText().toString();
                userPw = setUserPw.getText().toString();

                ConnectServer connectServer = new ConnectServer(IP+"8080/login?userId="+userId+"&userPw="+userPw);

                String result = connectServer.login();

                if(result.trim().equals("Success")){
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                else if(result.trim().equals("Empty")){
                    Toast.makeText(getApplication(), "회원이 아니거나, 입력하신 정보가 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                }

                else{
                    Toast.makeText(getApplication(), result.trim(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}