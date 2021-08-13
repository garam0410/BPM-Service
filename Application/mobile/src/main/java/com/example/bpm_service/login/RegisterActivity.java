package com.example.bpm_service.login;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.bpm_service.R;
import com.example.bpm_service.connection.UserManagementServer;

public class RegisterActivity extends AppCompatActivity {

    // 컴포넌트 정의
    private EditText editName, editId, editPw, editPwCheck, editEmail, editNumber;
    private Spinner editSex;
    private Button cancel_button, register_button;

    private ProgressDialog progressDialog;
    private Context context;

    // 입력값을 저장할 변수
    private String uname, userId, userPw, userPwCheck, usex, userEmail, unumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //상단바 숨기기
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        context = this;

        // 로딩 정의
        progressDialog = new ProgressDialog(this);

        // IP
        String IP = getResources().getString(R.string.IP);

        // ID 연결
        editName = (EditText) findViewById(R.id.editName);
        editId = (EditText) findViewById(R.id.editId);
        editPw = (EditText) findViewById(R.id.editPw);
        editPwCheck = (EditText) findViewById(R.id.editPwCheck);
        editEmail = (EditText) findViewById(R.id.editEmail);
        editNumber = (EditText) findViewById(R.id.editNumber);

        editSex = (Spinner) findViewById(R.id.editSex);

        cancel_button = (Button) findViewById(R.id.cancel_button);
        register_button = (Button) findViewById(R.id.register_button);

        // 성별 Spinner 연결
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_dropdown_item, getResources().getStringArray(R.array.sexArray)
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editSex.setAdapter(adapter);

        // 취소 버튼을 눌렀을 때, RegisterActivity 종료
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 회원가입 버튼을 눌렀을 때
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 입력값 가져오기
                uname = editName.getText().toString();
                userId = editId.getText().toString();
                userPw = editPw.getText().toString();
                userPwCheck = editPwCheck.getText().toString();
                usex = editSex.getSelectedItem().toString();
                userEmail = editEmail.getText().toString();
                unumber = editNumber.getText().toString();

                if(usex.equals("남자")){
                    usex = "1";
                }
                else{
                    usex = "2";
                }

                // 빈칸이 있을 때,
                if(uname.equals("") || userId.equals("") || userPw.equals("") || userPwCheck.equals("") || userEmail.equals("") || unumber.equals("")){
                    Toast.makeText(RegisterActivity.this, "모두 기입해주세요!", Toast.LENGTH_SHORT).show();
                }

                // 비밀번호 확인이 다를 때,
                else if(!userPw.equals(userPwCheck)){
                    Toast.makeText(RegisterActivity.this, "비밀번호를 다시 확인해 주세요", Toast.LENGTH_SHORT).show();
                }
                else{
                    progressDialog.setMessage("진행중....");
                    progressDialog.show();

                    UserManagementServer userManagementServer = new UserManagementServer(IP);
                    String result = userManagementServer.register(context, uname, userId, userPw, usex, userEmail,unumber);

                    // 회원가입 성공
                    if(result.trim().equals("Success")){
                        alertHandler(true,"회원가입 성공", "회원가입에 성공하였습니다!");
                    }

                    // 회원가입 실패 (이미 존재하는 아이디)
                    else if(result.trim().equals("Exist")){
                        alertHandler(false,"중복 아이디", "이미 존재하는 아이디 입니다.");
                        progressDialog.cancel();
                    }

                    // 회원가입 실패 (서버 에러)
                    else{
                        alertHandler(false,result.trim(),result.trim());
                        progressDialog.cancel();
                    }
                }
            }
        });
    }

    // DialogHandler
    public void alertHandler(Boolean tf,String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
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