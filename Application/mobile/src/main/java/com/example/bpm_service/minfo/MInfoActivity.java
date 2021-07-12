package com.example.bpm_service.minfo;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Movie;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bpm_service.MainActivity;
import com.example.bpm_service.R;
import com.example.bpm_service.connection.MovieInformationServer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MInfoActivity extends AppCompatActivity {

    private static String IP = "";
    private String userId;
    private String title, image, director, actor;
    private String hotData;

    private RecyclerView movieRankList;
    public MovieListAdapter movieListAdapter_rank;

    private TextView titleText, openText, genreText, directorText, actorText, gradeText, countText, bpmText, loveButton;
    private ImageView poster;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_info);

        IP = getResources().getString(R.string.IP);
        //IP = "http://61.245.226.112:";

        // 상단바 제거
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        // 영화 순위 RecyclerView 연결
        movieRankList = findViewById(R.id.hotMovieRank);

        titleText = (TextView) findViewById(R.id.titleText);
        openText = (TextView) findViewById(R.id.openText);
        genreText = (TextView) findViewById(R.id.genreText);
        directorText = (TextView) findViewById(R.id.directorText);
        actorText = (TextView) findViewById(R.id.actorText);
        gradeText = (TextView) findViewById(R.id.gradeText);
        countText = (TextView) findViewById(R.id.countText);
        bpmText = (TextView) findViewById(R.id.bpmText);
        loveButton = (TextView) findViewById(R.id.loveButton);

        poster = (ImageView) findViewById(R.id.poster);

        // 넘어온 값 가져오기
        Intent intent = getIntent();
        if(intent != null){
            userId = intent.getStringExtra("userId");
            title = intent.getStringExtra("title");
            hotData = intent.getStringExtra("hotData");
            if(hotData==null){
                MovieInformationServer movieInformationServer = new MovieInformationServer(IP);
                hotData = movieInformationServer.hotMovieRank();
            }
        }



        loveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loveButton.getText().toString().equals("❤")){
                    MovieInformationServer changeState = new MovieInformationServer(IP);
                    loveButton.setText("🤍");
                    Toast.makeText(MInfoActivity.this, changeState.changeLove(userId, titleText.getText().toString(),"delete"), Toast.LENGTH_SHORT).show();
                }else{
                    MovieInformationServer changeState = new MovieInformationServer(IP);
                    loveButton.setText("❤");
                    Toast.makeText(MInfoActivity.this, changeState.changeLove(userId, titleText.getText().toString(),"insert"), Toast.LENGTH_SHORT).show();
                }
            }
        });

        System.out.println(title);

        setData(title);

        // 리스트 초기화
        init(movieRankList,movieListAdapter_rank,hotData);
    }

    // 리스트 초기화 함수
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

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        ArrayList<String> itemList = new ArrayList<>();
        itemList.add("1");
        itemList.add("2");
        itemList.add("3");
        itemList.add("4");
        itemList.add("5");

        movieListAdapter = new MovieListAdapter(getApplicationContext(), itemList, titleList,imageList, onClickItem);
        recyclerView.setAdapter(movieListAdapter);

        MyListDecoration decoration = new MyListDecoration();
        recyclerView.addItemDecoration(decoration);
    }

    public void setData(String title){
        MovieInformationServer movieInformationServer = new MovieInformationServer(IP);
        JSONObject data = movieInformationServer.getInfo(title, userId);

        try {
            titleText.setText((String)data.get("title"));
            openText.setText((String)data.get("open"));
            genreText.setText((String)(data.get("genre") + " | " + data.get("runningtime")));
            directorText.setText((String)data.get("director"));
            actorText.setText((String)data.get("actor"));
            gradeText.setText((String)data.get("grade"));
            countText.setText(data.get("count").toString()+"명");
            bpmText.setText("💙 "+data.get("min").toString()+" ❤ "+data.get("max").toString());

            if(data.get("love").toString().equals("1")){
                loveButton.setText("❤");
            }else{
                loveButton.setText("🤍");
            }

            Glide.with(this).load(data.get("image").toString()).into(poster);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private View.OnClickListener onClickItem = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setData((String)v.getTag());
        }
    };
}