package com.example.bpm_service.login;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.bpm_service.R;
import com.example.bpm_service.connection.UserManagementServer;

public class FindActivity extends AppCompatActivity {

    // 컴포넌트 정의
    private EditText editName, editEmail, editNumber;
    private Button cancel_button, find_button;

    private ProgressDialog progressDialog;

    //AlertDialog.Builder builder;

    // 입력값을 저장할 변수
    private String uname, userEmail, unumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);

        //상단바 숨기기
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        // 로딩 정의
        progressDialog = new ProgressDialog(this);

        // IP
        String IP = getResources().getString(R.string.IP);

        // ID 연결
        editName = (EditText) findViewById(R.id.editName);
        editEmail = (EditText) findViewById(R.id.editEmail);
        editNumber = (EditText) findViewById(R.id.editNumber);

        cancel_button = (Button) findViewById(R.id.cancel_button);
        find_button = (Button) findViewById(R.id.find_button);

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 확인 버튼을 눌렀을 때
        find_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 입력값 가져오기
                uname = editName.getText().toString();
                userEmail = editEmail.getText().toString();
                unumber = editNumber.getText().toString();

                // 빈칸이 있을 때,
                if(uname.equals("") || userEmail.equals("") || unumber.equals("")){
                    alertHandler(false,"입력 오류", "모두 입력해 주세요!");
                }
                else{
                    progressDialog.setMessage("진행중....");
                    progressDialog.show();

                    UserManagementServer userManagementServer = new UserManagementServer(IP);
                    String result = userManagementServer.find(uname, userEmail, unumber);

                    // 찾기 성공
                    if(result.trim().equals("[]")){
                        progressDialog.cancel();
                        alertHandler(false,"ID/PW 찾기 결과", "존재하지 않는 사용자 입니다.");
                    }
                    else {
                        ParseData parseData = new ParseData(result);
                        result = parseData.getData();
                        progressDialog.cancel();
                        alertHandler(true,"ID/PW 찾기 결과", parseData.getData());
                    }
                }
            }
        });
    }

    // DialogHandler
    public void alertHandler(Boolean tf, String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(FindActivity.this);
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
}