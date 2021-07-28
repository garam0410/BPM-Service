package com.example.bpm_service.connection;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class PostTask extends AsyncTask<String, Void, String> {

    @Override
    protected String doInBackground(String... strings) {
        try{
            return POST(strings[0], strings[1]);
        }catch(IOException e){
            e.printStackTrace();
            return "error";
        }
    }

    @Override
    protected void onPostExecute(String s){
        super.onPostExecute(s);
    }

    // POST 요청
    private String POST(String linkUrl, String data) throws IOException{
        InputStream inputStream = null;
        String resultString = "";

        try{
            URL url = new URL(linkUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");

            byte[] outputInBytes = data.getBytes("UTF-8");
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(outputInBytes);
            outputStream.close();

            int response = conn.getResponseCode();
            Log.d("REST POST", "The response is : " + response);
            inputStream = conn.getInputStream();

            resultString = convertInputStreamToString(inputStream);
            Log.d("REST POST", "The value is : " + resultString.trim());

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