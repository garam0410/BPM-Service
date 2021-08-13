package com.example.bpm_service.search;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.service.autofill.OnClickAction;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import com.example.bpm_service.MainActivity;
import com.example.bpm_service.R;
import com.example.bpm_service.connection.MovieInformationServer;
import com.example.bpm_service.login.LoginActivity;
import com.example.bpm_service.minfo.MInfoActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SearchActivity extends Fragment {

    // 컴포넌트 정의
    private ListView searchList;
    private EditText search_bar;
    private Button search;
    private ProgressDialog progressDialog;

    private SearchListAdapter adapter;
    public String IP;
    private String data;
    private String userId;

    private Context context;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle saveInstanceState){
        View view = inflater.inflate(R.layout.activity_search, container, false);

        IP = container.getResources().getString(R.string.IP);

        userId = getArguments().getString("userId");

        context = getActivity();

        // 컴포넌트 연결
        searchList = (ListView) view.findViewById(R.id.searchlist);
        search_bar = (EditText) view.findViewById(R.id.search_bar);
        search = (Button) view.findViewById(R.id.search);

        // Adpapter 연결
        adapter = new SearchListAdapter();
        searchList.setAdapter(adapter);

        search_bar.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)){
                    search();
                    return true;
                }
                return false;
            }
        });

        // 검색버튼 클릭 시,
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });

        searchList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SearchListItem item = (SearchListItem)parent.getItemAtPosition(position);

                Intent intent = new Intent(getActivity(), MInfoActivity.class);
                intent.putExtra("userId", userId);
                intent.putExtra("title", item.getTitle());
                startActivity(intent);
            }
        });

        return view;
    }
    // DialogHandler
    public void alertHandler(Boolean tf, String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(title).setMessage(message);

        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(tf){
                }
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void search(){
        String query = search_bar.getText().toString();

        // 공백 검사
        if(query.equals("")){
            alertHandler(false,"검색어 없음", "검색어를 입력해주세요.");
        }
        else{

            adapter.notifyDataSetChanged();
            adapter.clearItem();

            // 데이터 불러오기
            MovieInformationServer movieInformationServer = new MovieInformationServer(IP);
            data = movieInformationServer.search(context, query);

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
        }
    }
}