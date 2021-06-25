package com.example.bpm_service.connection;


import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

//AsyncTask 연결
public class GetTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... strings) {
        try{
            return GET(strings[0]);
        }catch(IOException e){
            e.printStackTrace();
            return "error";
        }
    }

    @Override
    protected void onPostExecute(String s){
        super.onPostExecute(s);
    }

    // GET 요청
    private String GET(String linkUrl) throws IOException{
        InputStream inputStream = null;
        String resultString = "";

        try{
            URL url = new URL(linkUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("REST GET", "The response is : " + response);
            inputStream = conn.getInputStream();

            resultString = convertInputStreamToString(inputStream);
            Log.d("REST GET", "The value is : " + resultString.trim());

        }catch(IOException e){
            e.printStackTrace();
        }

        return resultString;
    }

    // Buffer 변환
    public String convertInputStreamToString(InputStream stream) throws IOException, UnsupportedEncodingException {
        BufferedReader bf;
        bf = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        return bf.readLine();
    }
}