package com.example.bpm_service.minfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.TextView;

import com.example.bpm_service.R;
import com.example.bpm_service.dto.CommentDto;

import java.util.ArrayList;

public class CommentListAdapter extends BaseAdapter {
    private ArrayList<CommentDto> list = new ArrayList<CommentDto>();

    public CommentListAdapter(){}

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService((Context.LAYOUT_INFLATER_SERVICE));
            convertView = inflater.inflate(R.layout.comment_item,parent,false);
        }

        TextView userIdText = (TextView) convertView.findViewById(R.id.userIdText);
        TextView commentTimeText = (TextView) convertView.findViewById(R.id.commentTimeText);
        TextView commentText = (TextView) convertView.findViewById(R.id.commentText);

        CommentDto commentDto = list.get(position);

        String commentTime = "";
        if(!commentDto.getUpdate_time().trim().equals("")){
            commentTime += commentDto.getInsert_time() +" (수정된 날짜 : " + commentDto.getUpdate_time() + ")";
        }else{
            commentTime = commentDto.getInsert_time();
        }

        userIdText.setText(commentDto.getUserId());
        commentTimeText.setText(commentTime);
        commentText.setText((commentDto.getComment()));

        return convertView;
    }

    public void addItem(String userId, Long cid, Long uid, Long mid, String comment, String insert_time, String update_time){
        CommentDto commentDto = new CommentDto();

        commentDto.setUserId(userId);
        commentDto.setCid(cid);
        commentDto.setMid(mid);
        commentDto.setComment(comment);
        commentDto.setInsert_time(insert_time);
        commentDto.setUpdate_time(update_time);

        list.add(commentDto);
    }

    public void clearItem(){
        list.clear();
    }
}
