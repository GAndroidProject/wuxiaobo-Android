package com.xiaoe.shop.wxb.adapter.audio;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaoe.common.entitys.AudioPlayEntity;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

public class AudioPlayListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final String TAG = "AudioPlayListAdapter";
    private Context mContext;
    private List<AudioPlayEntity> playEntityList;

    public AudioPlayListAdapter(Context context) {
        this.mContext = context;
        playEntityList = new ArrayList<AudioPlayEntity>();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_audio_play_list_item, null, false);
        return new AudioPlayListViewHolder(view, mContext);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        AudioPlayListViewHolder viewHolder = (AudioPlayListViewHolder) holder;
        viewHolder.bindView(playEntityList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return playEntityList.size();
    }

    public void addAll(List<AudioPlayEntity> list){
        playEntityList.addAll(list);
        notifyDataSetChanged();
    }
}
