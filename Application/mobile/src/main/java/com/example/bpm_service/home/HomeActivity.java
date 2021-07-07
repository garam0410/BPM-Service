package com.example.bpm_service.home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bpm_service.MainActivity;
import com.example.bpm_service.R;
import com.example.bpm_service.connection.MovieInformationServer;
import com.example.bpm_service.minfo.MInfoActivity;
import com.example.bpm_service.minfo.MyListDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeActivity extends Fragment {

    private RecyclerView movieRankList, userRankList;
    public MovieListAdapter movieListAdapter_rank;
    public MovieListAdapter movieListAdapter_user;

    public String hotData;
    public String userId="";

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle saveInstanceState){
        View view = inflater.inflate(R.layout.activity_home, container, false);

        //String IP = getResources().getString(R.string.IP);
        String IP = "http://61.245.226.112:";

        movieRankList = view.findViewById(R.id.hotMovieRank);
        userRankList = view.findViewById(R.id.userMovieRank);

        ActionBar actionbar = ((MainActivity)getActivity()).getSupportActionBar();
        actionbar.hide();

        Bundle bundle = getArguments();
        userId = bundle.getString("userId");

        MovieInformationServer movieInformationServer = new MovieInformationServer(IP);

        hotData = movieInformationServer.hotMovieRank();
        //String userData = movieInformationServer.userMovieRank();

        init(movieRankList,movieListAdapter_rank, hotData);
        init(userRankList,movieListAdapter_user, hotData);

        return view;
    }

    private void init(RecyclerView recyclerView, MovieListAdapter movieListAdapter, String data) {

        ArrayList<String> titleList = new ArrayList<>();
        ArrayList<String> imageList = new ArrayList<>();

        try{
            JSONArray json = new JSONArray(data);
            for(int i = 0; i<json.length(); i++){
                JSONObject obj = (JSONObject)json.get(i);

                String title = obj.getString("title");
                String image = obj.getString("image");

                titleList.add(title);
                imageList.add(image);
            }
        }catch(JSONException e){
            e.printStackTrace();
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        ArrayList<String> itemList = new ArrayList<>();
        itemList.add("1");
        itemList.add("2");
        itemList.add("3");
        itemList.add("4");
        itemList.add("5");

        movieListAdapter = new MovieListAdapter(getActivity(), itemList, titleList,imageList, onClickItem);
        recyclerView.setAdapter(movieListAdapter);

        MyListDecoration decoration = new MyListDecoration();
        recyclerView.addItemDecoration(decoration);
    }


    private View.OnClickListener onClickItem = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getActivity(), MInfoActivity.class);
            intent.putExtra("userId", userId);
            intent.putExtra("title", (String) v.getTag());
            intent.putExtra("hotData", hotData);
            startActivity(intent);
        }
    };
}