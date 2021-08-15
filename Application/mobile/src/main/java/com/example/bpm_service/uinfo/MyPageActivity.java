package com.example.bpm_service.uinfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.bpm_service.R;
import com.example.bpm_service.connection.MovieInformationServer;
import com.example.bpm_service.login.LoginActivity;
import com.example.bpm_service.minfo.MInfoActivity;
import com.example.bpm_service.minfo.MyListDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MyPageActivity extends Fragment {

    private String userId;
    private String IP;
    private String loveList, watchList;

    private Button logout, reservationMovie;
    private boolean SAVE_LOGIN_DATA;

    private RecyclerView loveMovieList, watchMovieList;
    private MovieListAdapter loveMovieListAdapter;
    private MovieListAdapter watchMovieListAdapter;

    private Context context;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle saveInstanceState){
        View view = inflater.inflate(R.layout.activity_mypage, container, false);

        IP = getResources().getString(R.string.IP);

        SharedPreferences appData = this.getActivity().getSharedPreferences("appData", Activity.MODE_PRIVATE);

        context = getActivity();

        logout = (Button) view.findViewById(R.id.logout);
        reservationMovie = (Button) view.findViewById(R.id.reservationMovie);

        Bundle bundle = getArguments();
        userId = bundle.getString("userId");

        loveMovieList = view.findViewById(R.id.loveMovieList);
        watchMovieList = view.findViewById(R.id.watchMovieList);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext()).setTitle("로그아웃").setMessage("로그아웃 하시겠습니까?")
                        .setPositiveButton("로그아웃", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent(getContext(), LoginActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                SAVE_LOGIN_DATA = false;
                                i.putExtra("SAVE_LOGIN_DATA", false);
                                startActivity(i);
                                SharedPreferences.Editor editor = appData.edit();
                                editor.clear();
                                editor.commit();
                                getActivity().finish();
                            }
                        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                        }).show();
            }
        });

        reservationMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences reservationData = getActivity().getSharedPreferences("reservationData", Activity.MODE_PRIVATE);
                if(reservationData.getBoolean("reservationState",false) == true){
                    Intent intent = new Intent(getActivity(), ReservationActivity.class);
                    startActivity(intent);
                }
            }
        });

        // 좋아요 누른 영화 리스트
        MovieInformationServer movieInformationServer_1 = new MovieInformationServer(IP);
        loveList = movieInformationServer_1.getLoveMovie(context,userId);
        init(loveMovieList, loveMovieListAdapter, loveList);

        // 측정한 영화 리스트
        MovieInformationServer movieInformationServer_2 = new MovieInformationServer(IP);
        watchList = movieInformationServer_2.getWatchMovie(context,userId);
        init(watchMovieList, watchMovieListAdapter, watchList);

        return view;
    }

    // 리스트 업데이트
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
        for(int i = 0; i<titleList.size(); i++)
            itemList.add(String.valueOf(i+1));

        movieListAdapter = new MovieListAdapter(getActivity(), itemList, titleList, imageList, onClickItem);
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
            startActivity(intent);
        }
    };
}