package com.example.bpm_service.login;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ParseData {

    private String data = "";

    public ParseData(){}

    public ParseData(String data){
        this.data = data;
    }

    public String getData() {

        try {
            JSONArray jsonArray = new JSONArray(data);
            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String userId = jsonObject.getString("userid");
                String userPw = jsonObject.getString("userPw");

                data = "아이디 : " + userId +"\n비밀번호 : " + userPw;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return data;
    }
}
