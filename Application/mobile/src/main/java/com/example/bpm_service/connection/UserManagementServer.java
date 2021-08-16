package com.example.bpm_service.connection;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.loader.content.AsyncTaskLoader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class UserManagementServer {

    private String url = "";

    public UserManagementServer(String IP){
        this.url = IP;
    }

    // 로그인
    public String login(Context context, String userId, String userPw){
        try {
            url += "8080/login?userId="+userId+"&userPw="+userPw;
            return new GetTask().execute(url).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
            return "ExecutionError";
        } catch (InterruptedException e) {
            e.printStackTrace();
            return "InterruptedError";
        }
    }

    // 회원가입
    public String register(Context context, String uname, String userId, String userPw, String uAge, String usex, String userEmail, String unumber){
        try {
            url += "8080/register?uname="+uname+"&userId="+userId+"&userPw="+userPw+"&uAge="+uAge+"&usex="+usex+"&userEmail="+userEmail+"&unumber="+unumber;
            return new GetTask().execute(url).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
            return "ExecutionError";
        } catch (InterruptedException e) {
            e.printStackTrace();
            return "InterruptedError";
        }
    }

    // ID,PW 찾기
    public String find(Context context, String uname, String userEmail, String unumber){
        try {
            url += "8080/findidpw?uname="+uname+"&userEmail="+userEmail+"&unumber="+unumber;
            return new GetTask().execute(url).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
            return "ExecutionError";
        } catch (InterruptedException e) {
            e.printStackTrace();
            return "InterruptedError";
        }
    }
}
