package com.example.bpm_service.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.bpm_service.R;

import java.util.ArrayList;

public class SearchListAdapter extends BaseAdapter {

    private ArrayList<SearchListItem> list = new ArrayList<SearchListItem>();

    public SearchListAdapter(){}

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
            convertView = inflater.inflate(R.layout.searchlist_item,parent,false);
        }

        ImageView image = (ImageView) convertView.findViewById(R.id.image);
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView director = (TextView) convertView.findViewById(R.id.director);
        TextView actor = (TextView) convertView.findViewById(R.id.actor);

        SearchListItem searchListItem = list.get(position);

        Glide.with(convertView).load(searchListItem.getImage()).into(image);
        title.setText(searchListItem.getTitle());
        director.setText(searchListItem.getDirector());
        actor.setText((searchListItem.getActor()));

        return convertView;
    }

    public void addItem(String image, String title, String director, String actor){
        SearchListItem item = new SearchListItem();

        item.setImage(image);
        item.setTitle(title);
        item.setDirector(director);
        item.setActor(actor);

        list.add(item);
    }

    public void clearItem(){
        list.clear();
    }
}
