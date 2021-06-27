package com.example.bpm_service.connection;

import java.util.concurrent.ExecutionException;

public class MovieInformationServer {
    private String url = "";

    public MovieInformationServer(String IP){
        this.url = IP;
    }

    // 검색
    public String search(String query){
        try {
            url += "8081/searchmovie?query="+query;
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
