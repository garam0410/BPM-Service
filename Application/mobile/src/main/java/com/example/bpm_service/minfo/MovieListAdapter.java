package com.example.bpm_service.minfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bpm_service.R;

import java.util.ArrayList;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.ViewHolder>{

    private ArrayList<String> itemList;
    private Context context;
    private View.OnClickListener onClickItem;

    private ArrayList<String> titleList;
    private ArrayList<String> imageList;

    public MovieListAdapter(Context context, ArrayList<String> itemList, ArrayList<String> titleList, ArrayList<String> imageList ,View.OnClickListener onClickItem) {
        this.context = context;
        this.itemList = itemList;
        this.titleList = titleList;
        this.imageList = imageList;
        this.onClickItem = onClickItem;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // context 와 parent.getContext() 는 같다.
        View view = LayoutInflater.from(context)
                .inflate(R.layout.movierank_item_infoactivity, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        String item = itemList.get(position);
        String title = titleList.get(position);
        String image = imageList.get(position);

        holder.textview.setText(item);
        holder.item.setTag(title);
        Glide.with(holder.itemView.getContext()).load(image).into(holder.imageView);
        holder.item.setOnClickListener(onClickItem);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public LinearLayout item;
        public TextView textview;
        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);

            item = itemView.findViewById(R.id.item);
            textview = itemView.findViewById(R.id.rank);
            imageView = itemView.findViewById(R.id.poster);
        }
    }
}
