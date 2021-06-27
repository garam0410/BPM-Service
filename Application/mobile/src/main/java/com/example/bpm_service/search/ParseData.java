package com.example.bpm_service.search;

import com.example.bpm_service.dto.MovieDto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ParseData {
    private String data;

    public ParseData(String data){
        this.data = data;
    }

    public List<MovieDto> getData(){
        List<MovieDto> list = null;

        try {
            JSONArray json = new JSONArray(data);

            for(int i = 0; i<json.length();i++){

            }

            JSONObject obj = new JSONObject(data);
            String title = obj.getString("title");
            String image = obj.getString("image");
            String director = obj.getString("director");
            String actor = obj.getString("actor");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }
}
