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

    private Button login, register;
    private EditText setUserId, setUserPw;
    private CheckBox auto_LoginCheck;
    private ProgressDialog progressDialog;

    //자동 로그인 확인 변수
    private SharedPreferences appData;
    private boolean saveLoginData;

    // Edittext 값 변수 선언
    String userId, userPw;

    // IP
    //String IP = getResources().getString(R.string.IP).toString() + "8080";
    String IP = "http://118.67.132.152:8080";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = (Button) findViewById(R.id.Login);
        register = (Button) findViewById(R.id.Resister);

        setUserId = (EditText) findViewById(R.id.setUserId);
        setUserPw = (EditText) findViewById(R.id.setUserPw);

        auto_LoginCheck = (CheckBox) findViewById(R.id.autoLoginCheck);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userId = setUserId.getText().toString();
                userPw = setUserPw.getText().toString();

                ConnectServer connectServer = new ConnectServer();
                try {
                    String result = connectServer.execute("http://61.245.226.232:8000/login","garam0410","rlarkfka").get();
                    Toast.makeText(getApplicationContext(),result,Toast.LENGTH_LONG);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                //startActivity(intent);
                //finish();
            }
        });
    }
}