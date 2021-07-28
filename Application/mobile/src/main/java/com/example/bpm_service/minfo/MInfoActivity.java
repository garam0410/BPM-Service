package com.example.bpm_service.minfo;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Movie;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bpm_service.MainActivity;
import com.example.bpm_service.R;
import com.example.bpm_service.connection.MovieInformationServer;
import com.example.bpm_service.connection.SocialServer;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MInfoActivity extends AppCompatActivity {

    private static String IP = "";
    private String userId;
    private String title, image, director, actor;
    private String hotData;
    private JSONArray commentData;

    private RecyclerView movieRankList;
    private ListView commentList;
    LinearLayout detailPage;
    LinearLayout reviewPage;

    public MovieListAdapter movieListAdapter_rank;
    public CommentListAdapter commentListAdapter;

    private TextView titleText, openText, genreText, directorText, actorText, gradeText, countText, bpmText, loveButton, summaryText;
    private ImageView poster;
    private TabLayout tabLayout;

    private EditText reviewBar;
    private Button insertReview;

    private TabLayout.Tab changeIndex;

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
        commentList = findViewById(R.id.commentList);

        titleText = (TextView) findViewById(R.id.titleText);
        openText = (TextView) findViewById(R.id.openText);
        genreText = (TextView) findViewById(R.id.genreText);
        directorText = (TextView) findViewById(R.id.directorText);
        actorText = (TextView) findViewById(R.id.actorText);
        gradeText = (TextView) findViewById(R.id.gradeText);
        countText = (TextView) findViewById(R.id.countText);
        bpmText = (TextView) findViewById(R.id.bpmText);
        loveButton = (TextView) findViewById(R.id.loveButton);
        summaryText = (TextView) findViewById(R.id.summaryText);

        poster = (ImageView) findViewById(R.id.poster);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                changeIndex = tab;
                changeTab(pos);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

//        poster.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SocialServer socialServer = new SocialServer(IP);
//                //System.out.println(socialServer.selectCommentList("1"));
//
//                try {
//                    JSONObject json = new JSONObject();
//                    json.put("mid", "1");
//                    json.put("userId", "garam040");
//                    json.put("comment", "test 변경");
//                    socialServer.insertComment("25",json.toString());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });

        reviewBar = (EditText) findViewById(R.id.reviewBar);
        insertReview = (Button) findViewById(R.id.insertReview);
        insertReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocialServer socialServer = new SocialServer(IP);

                try {
                    JSONObject json = new JSONObject();
                    json.put("title", title);
                    json.put("userId", userId);
                    json.put("comment", reviewBar.getText().toString());
                    socialServer.insertComment("",json.toString());
                    initCommentData(title);
                    reviewBar.setText("");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

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

        commentListAdapter = new CommentListAdapter();
        commentList.setAdapter(commentListAdapter);

        // 리스트 초기화
        initHotdata(movieRankList,movieListAdapter_rank,hotData);
        initCommentData(title);
    }

    // 탭 변경시 뷰 전환
    private void changeTab(int index){
        detailPage = (LinearLayout) findViewById(R.id.detailPage) ;
        reviewPage = (LinearLayout) findViewById(R.id.reviewPage) ;

        switch (index) {
            case 0 :    // 상세정보 페이지
                detailPage.setVisibility(View.VISIBLE) ;
                reviewPage.setVisibility(View.INVISIBLE) ;
                break ;
            case 1 :    // 리뷰 페이지
                detailPage.setVisibility(View.INVISIBLE) ;
                reviewPage.setVisibility(View.VISIBLE) ;
                break ;
        }
    }

    // 댓글 초기화 함수
    private void initCommentData(String title) {

        commentListAdapter.notifyDataSetChanged();
        commentListAdapter.clearItem();

        try{
            SocialServer socialServer = new SocialServer(IP);
            commentData = socialServer.selectCommentList(title);

            for(int i = 0; i<commentData.length(); i++){
                JSONObject json = (JSONObject) commentData.get(i);

                String Id = json.getString("userId");
                Long cid = Long.valueOf(json.getString("cid"));
                Long mid = Long.valueOf(json.getString("mid"));
                Long uid = Long.valueOf(json.getString("uid"));
                String insertTime = json.getString("insert_time");
                String updateTime = json.getString("update_time");
                String comment = json.getString("comment");

                commentListAdapter.addItem(Id,cid,uid,mid,comment,insertTime,updateTime);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    // 리스트 초기화 함수
    private void initHotdata(RecyclerView recyclerView, MovieListAdapter movieListAdapter, String data) {

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
            summaryText.setText((String)data.get("summary"));

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
            initCommentData((String)v.getTag());
            detailPage.setVisibility(View.VISIBLE) ;
            reviewPage.setVisibility(View.INVISIBLE) ;
            tabLayout.selectTab(changeIndex);
        }
    };

    private View.OnClickListener onClickCommentItem = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            System.out.println(v.getTag());
            //setData((String)v.getTag());
        }
    };
}