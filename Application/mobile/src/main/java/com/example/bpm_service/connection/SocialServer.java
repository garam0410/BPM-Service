package com.example.bpm_service.connection;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class SocialServer {
    private String url = "";
    private String data = "";

    public SocialServer(String IP){
        this.url = IP;
    }

    //댓글 추가
    public String insertComment(String cid, String commentData){
        try {
            url += "8082/comments/"+cid;
            return new PostTask().execute(url,commentData).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
            return "ExecutionError";
        } catch (InterruptedException e) {
            e.printStackTrace();
            return "InterruptedError";
        }
    }

    // 댓글 삭제
    public String deleteComment(Context context, String cid){
        try {
            url += "8082/comments/delete/"+cid;
            return new GetTask().execute(url).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
            return "ExecutionError";
        } catch (InterruptedException e) {
            e.printStackTrace();
            return "InterruptedError";
        }
    }

    // 댓글 목록 조회
    public JSONArray selectCommentList(Context context, String title){
        try {
            url += "8082/comments/"+title;
            data = new GetTask().execute(url).get();
            JSONObject json = new JSONObject(data);
            JSONArray getData = (JSONArray) json.get("commentList");
            return getData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 심박수 데이터 전송
    public String sendBpm(String bpmData){
        try {
            url += "8082/bpmsend/";
            return new PostTask().execute(url, bpmData).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
            return "ExecutionError";
        } catch (InterruptedException e) {
            e.printStackTrace();
            return "InterruptedError";
        }
    }
}
