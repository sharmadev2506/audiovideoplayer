package com.musicandvideoplayer.mainscreen.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.musicandvideoplayer.mainscreen.utils.Constant;
import com.musicandvideoplayer.R;
import com.musicandvideoplayer.mainscreen.pojo.AudioVideoBean;
import com.musicandvideoplayer.mainscreen.listener.RecyclerOnClick;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.ViewHolder> {
    private ArrayList<AudioVideoBean> arrayList;
    private RecyclerOnClick recyclerOnClick;

    VideoAdapter(ArrayList<AudioVideoBean> audioBeanArrayList, RecyclerOnClick recyclerOnClick) {
        this.arrayList = audioBeanArrayList;
        this.recyclerOnClick = recyclerOnClick;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.video_songs_row, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String url = Constant.VIDEO + arrayList.get(i).getImage();
        Glide.with(viewHolder.ivIcon.getContext()).load(url).
                placeholder(R.drawable.ic_home)
                .dontAnimate().diskCacheStrategy(DiskCacheStrategy.ALL).
                into(viewHolder.ivIcon);
        viewHolder.tvAlbum.setText(arrayList.get(i).getAlbum());
        viewHolder.tvTitle.setText(arrayList.get(i).getTitle());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.ivIcon)
        AppCompatImageView ivIcon;
        @BindView(R.id.frame)
        FrameLayout frame;
        @BindView(R.id.tvTitle)
        AppCompatTextView tvTitle;
        @BindView(R.id.tvAlbum)
        AppCompatTextView tvAlbum;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            recyclerOnClick.onClick(getAdapterPosition());
        }
    }
}
