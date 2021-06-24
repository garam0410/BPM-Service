package com.example.bpm_service.connection;

import android.os.AsyncTask;

import androidx.loader.content.AsyncTaskLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConnectServer extends AsyncTask<String, Void, String> {

    //받은 데이터
    String sendData;
    String recieveData;

    //반환 값
    String result;

    @Override
    protected String doInBackground(String... strings) {

        try{
            String str;

            //접속할 주소
            URL url = new URL(strings[0]);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestMethod("GET");
            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

            sendData = "userId=" + strings[1] + "&userPw=" + strings[2];

            osw.write(sendData);
            osw.flush();

            if (conn.getResponseCode() == conn.HTTP_OK) {
                InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                BufferedReader reader = new BufferedReader(tmp);
                StringBuffer buffer = new StringBuffer();

                // jsp에서 보낸 값을 받는 부분
                while ((str = reader.readLine()) != null) {
                    buffer.append(str);
                }
                recieveData = buffer.toString();
            } else {
                // 통신 실패
            }

        }catch(IOException e){
            e.printStackTrace();
        }

        return recieveData;
    }
}
