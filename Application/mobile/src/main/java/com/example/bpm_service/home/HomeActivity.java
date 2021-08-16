package com.example.bpm_service.home;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Movie;
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

    public static Context context;
    private RecyclerView movieRankList, userRankList;

    // 심박수 분석 영화 순위 어댑터
    public MovieListAdapter movieListAdapter_rank;

    // 협업필터링 이용 영화 순위 어댑터
    public MovieListAdapter movieListAdapter_user;

    public String hotData, userData;
    public String userId="";

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle saveInstanceState){
        View view = inflater.inflate(R.layout.activity_home, container, false);

        // 서버 주소 연결
        String IP = getResources().getString(R.string.IP);

        context = getActivity();

        movieRankList = view.findViewById(R.id.hotMovieRank);
        userRankList = view.findViewById(R.id.userMovieRank);

        //MainActivity에서 Bundle로 받아온 변수 저장
        Bundle bundle = getArguments();
        userId = bundle.getString("userId");

        MovieInformationServer bpmRanking = new MovieInformationServer(IP);
        MovieInformationServer userRanking = new MovieInformationServer(IP);

        hotData = bpmRanking.hotMovieRank(context);
        userData = userRanking.userMovieRank(context, userId);

        init(movieRankList,movieListAdapter_rank, hotData);
        init(userRankList,movieListAdapter_user, userData);

        return view;
    }

    // RecyclerView 생성 함수
    private void init(RecyclerView recyclerView, MovieListAdapter movieListAdapter, String data) {

        // 영화 제목과 이미지 url을 담을 ArrayList
        ArrayList<String> titleList = new ArrayList<>();
        ArrayList<String> imageList = new ArrayList<>();

        try{
            JSONArray json = new JSONArray(data);

            // Json 파싱
            for(int i = 0; i<3; i++){
                JSONObject obj = (JSONObject)json.get(i);

                String title = obj.getString("title");
                String image = obj.getString("image");

                System.out.println(image);

                titleList.add(title);
                imageList.add(image);
            }
        }catch(JSONException e){
            e.printStackTrace();
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        // 1위부터 5위까지
        ArrayList<String> itemList = new ArrayList<>();
        itemList.add("1");
        itemList.add("2");
        itemList.add("3");
//        itemList.add("4");
//        itemList.add("5");

        // Adapter에 연결
        movieListAdapter = new MovieListAdapter(getActivity(), itemList, titleList,imageList, onClickItem);
        recyclerView.setAdapter(movieListAdapter);

        MyListDecoration decoration = new MyListDecoration();
        recyclerView.addItemDecoration(decoration);
    }


    // 영화 클릭시 이벤트 발생, 영화 상세정보 페이지 띄워줌
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