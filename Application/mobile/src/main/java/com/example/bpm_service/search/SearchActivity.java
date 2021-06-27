package com.example.bpm_service.search;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.bpm_service.MainActivity;
import com.example.bpm_service.R;
import com.example.bpm_service.connection.MovieInformationServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SearchActivity extends Fragment {

    // 컴포넌트 정의
    private ListView searchList;
    private SearchView searchView;

    private SearchListAdapter adapter;
    private String data;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle saveInstanceState){
        View view = inflater.inflate(R.layout.activity_search, container, false);

        String IP = container.getResources().getString(R.string.IP);

        // 상단바 제거
        ActionBar actionBar = ((MainActivity)getActivity()).getSupportActionBar();
        actionBar.hide();

        // 컴포넌트 연결
        searchList = (ListView) view.findViewById(R.id.searchlist);
        searchView = (SearchView) view.findViewById(R.id.search_bar);

        // Adpapter 연결
        adapter = new SearchListAdapter();
        searchList.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                adapter.notifyDataSetChanged();
                adapter.clearItem();

                // 데이터 불러오기
                MovieInformationServer movieInformationServer = new MovieInformationServer(IP);
                data = movieInformationServer.search(query);

                // 리스트 추가
                try {
                    JSONArray json = new JSONArray(data);
                    for(int i = 0; i<json.length(); i++){

                        JSONObject obj = (JSONObject)json.get(i);

                        String title = obj.getString("title");
                        String image = obj.getString("image");
                        String director = obj.getString("director");
                        String actor = obj.getString("actor");

                        adapter.addItem(image,title,director,actor);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        return view;
    }
}