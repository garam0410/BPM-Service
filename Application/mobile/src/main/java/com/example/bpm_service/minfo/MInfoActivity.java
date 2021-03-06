package com.example.bpm_service.minfo;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Movie;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.bpm_service.MainActivity;
import com.example.bpm_service.R;
import com.example.bpm_service.connection.MovieInformationServer;
import com.example.bpm_service.connection.SocialServer;
import com.example.bpm_service.dto.CommentDto;
import com.example.bpm_service.login.LoginActivity;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MInfoActivity extends AppCompatActivity{

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
    private Context context;

    private TabLayout.Tab changeIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_m_info);

        IP = getResources().getString(R.string.IP);
        //IP = "http://61.245.226.112:";

        context = this;

        // ????????? ??????
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        // ?????? ?????? RecyclerView ??????
        movieRankList = findViewById(R.id.hotMovieRank);
        commentList = findViewById(R.id.commentList);

        // ?????? ??????????????? ????????? ???????????? ??????
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
        reviewBar = (EditText) findViewById(R.id.reviewBar);

        // ??? ????????? ???
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

        // ?????? ????????? ????????? ??????
        commentListAdapter = new CommentListAdapter();
        commentList.setAdapter(commentListAdapter);

        commentList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                // ?????? ?????? ??????
                PopupMenu popupMenu = new PopupMenu(MInfoActivity.this, view);
                getMenuInflater().inflate(R.menu.comment_menu, popupMenu.getMenu());

                final CommentDto commentItem = (CommentDto) commentListAdapter.getItem(position);
                // ?????? ?????? ?????? ????????? ??????
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.action_modify:
                                modifyAlertHandler("?????? ??????","????????? ????????? ??????????????????", commentItem.getCid().toString(), commentItem.getComment());
                                break;
                            case R.id.action_delete:
                                deleteAlertHandler("?????? ??????", "?????? ????????? ?????????????????????????", commentItem.getCid().toString());
                                break;
                            default :
                                break;
                        }
                        return false;
                    }
                });
                if(commentItem.getUserId().trim().equals(userId)){
                    popupMenu.show();
                }
                return true;
            }
        });

        // ?????? ?????? ?????? ?????? ???
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

        //????????? ?????? ?????? ???
        loveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loveButton.getText().toString().equals("???")){
                    MovieInformationServer changeState = new MovieInformationServer(IP);
                    loveButton.setText("????");
                    Toast.makeText(MInfoActivity.this, changeState.changeLove(context, userId, titleText.getText().toString(),"delete"), Toast.LENGTH_SHORT).show();
                }else{
                    MovieInformationServer changeState = new MovieInformationServer(IP);
                    loveButton.setText("???");
                    Toast.makeText(MInfoActivity.this, changeState.changeLove(context, userId, titleText.getText().toString(),"insert"), Toast.LENGTH_SHORT).show();
                }
            }
        });

        // ????????? ??? ????????????
        Intent intent = getIntent();
        if(intent != null){
            userId = intent.getStringExtra("userId");
            title = intent.getStringExtra("title");
            hotData = intent.getStringExtra("hotData");
            if(hotData==null){
                MovieInformationServer movieInformationServer = new MovieInformationServer(IP);
                hotData = movieInformationServer.hotMovieRank(context);
            }
        }

        // ?????? ????????? ??????
        setData(title);

        // ????????? ?????????
        initHotdata(movieRankList,movieListAdapter_rank,hotData);
        initCommentData(title);
    }

    // ??? ????????? ??? ??????
    private void changeTab(int index){
        detailPage = (LinearLayout) findViewById(R.id.detailPage) ;
        reviewPage = (LinearLayout) findViewById(R.id.reviewPage) ;

        switch (index) {
            case 0 :    // ???????????? ?????????
                detailPage.setVisibility(View.VISIBLE) ;
                reviewPage.setVisibility(View.INVISIBLE) ;
                break ;
            case 1 :    // ?????? ?????????
                detailPage.setVisibility(View.INVISIBLE) ;
                reviewPage.setVisibility(View.VISIBLE) ;
                break ;
        }
    }

    // ?????? ????????? ??????
    private void initCommentData(String title) {

        commentListAdapter.notifyDataSetChanged();
        commentListAdapter.clearItem();

        try{
            SocialServer socialServer = new SocialServer(IP);
            commentData = socialServer.selectCommentList(context, title);

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

    // ????????? ????????? ?????? ( ?????? ?????? ?????? ?????????)
    private void initHotdata(RecyclerView recyclerView, MovieListAdapter movieListAdapter, String data) {

        ArrayList<String> titleList = new ArrayList<>();
        ArrayList<String> imageList = new ArrayList<>();

        try{
            JSONArray json = new JSONArray(data);

            //Json ??????
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

    // ?????? ????????? ?????? ??????
    public void setData(String getTitle){
        // ??????????????? ?????? ????????? ????????????
        MovieInformationServer movieInformationServer = new MovieInformationServer(IP);
        JSONObject data = movieInformationServer.getInfo(context, getTitle, userId);

        try {
            title = getTitle;
            titleText.setText(getTitle);
            openText.setText((String)data.get("open"));
            genreText.setText((String)(data.get("genre") + " | " + data.get("runningtime")));
            directorText.setText((String)data.get("director"));
            actorText.setText((String)data.get("actor"));
            gradeText.setText((String)data.get("grade"));
            countText.setText(data.get("count").toString()+"???");
            bpmText.setText("???? "+data.get("min").toString()+" ??? "+data.get("max").toString());
            summaryText.setText((String)data.get("summary"));

            if(data.get("love").toString().equals("1")){
                loveButton.setText("???");
            }else{
                loveButton.setText("????");
            }

            Glide.with(this).load(data.get("image").toString()).into(poster);
        } catch (Exception e) {
            e.printStackTrace();
            // ???????????? ????????? ???????????? ????????? ???, ?????????
            setData(getTitle);
        }
    }

    // ?????? ????????? ?????? ?????????, ?????? ?????? ?????? ???????????? ??? ?????? ????????????
    private View.OnClickListener onClickItem = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            setData((String)v.getTag());
            initCommentData((String)v.getTag());
        }
    };

    // ?????? ?????? dialog
    public void modifyAlertHandler(String alertTitle, String message, String cid, String comment){

        //?????? ??? ?????????
        EditText editText = new EditText(getApplicationContext());
        FrameLayout container = new FrameLayout(MInfoActivity.this);
        FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        params.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
        editText.setLayoutParams(params);
        container.addView(editText);

        AlertDialog.Builder builder = new AlertDialog.Builder(MInfoActivity.this);
        builder.setTitle(alertTitle).setMessage(message);
        builder.setView(container);
        editText.setText(comment);

        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(editText.getText().toString().equals(comment)){ // ????????? ????????? ?????? ???????????? ?????????, ?????? ????????????
                    initCommentData(title);
                }
                // ???????????? ?????? ???????????? ????????? ????????????
                else{
                    SocialServer socialServer = new SocialServer(IP);
                    try {
                        JSONObject json = new JSONObject();
                        json.put("title", title);
                        json.put("userId", userId);
                        json.put("comment", editText.getText().toString());
                        socialServer.insertComment(cid,json.toString());
                        initCommentData(title);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    // ?????? dialog
    public void deleteAlertHandler(String alertTitle, String message, String cid){
        AlertDialog.Builder builder = new AlertDialog.Builder(MInfoActivity.this);
        builder.setTitle(alertTitle).setMessage(message);

        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SocialServer socialServer = new SocialServer(IP);
                socialServer.deleteComment(context, cid);
                initCommentData(title);
            }
        });
        builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
}