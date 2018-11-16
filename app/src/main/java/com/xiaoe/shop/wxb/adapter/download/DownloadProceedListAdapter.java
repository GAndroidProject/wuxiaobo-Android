package com.xiaoe.shop.wxb.adapter.download;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import com.xiaoe.common.entitys.DownloadResourceTableInfo;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseViewHolder;

public class DownloadProceedListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final String TAG = "DownloadProceedListAdap";
    private final Context mContext;
    private List<DownloadResourceTableInfo> downloadResourceList;

    public DownloadProceedListAdapter(Context context) {
        mContext = context;
        downloadResourceList = new ArrayList<DownloadResourceTableInfo>();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == 0){
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_child_download_proceed, parent, false);
            return new DownloadProceedChildListHolder(view, null, null);
        }else if (viewType == 1){
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_download_proceed, parent, false);
            return new DownloadProceedListHolder(mContext, view);
        }
       return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        if(holder == null){
            return;
        }
        if(holder instanceof DownloadProceedListHolder){
            ((DownloadProceedListHolder)holder).bindView(downloadResourceList.get(position), position);
        }else if(holder instanceof DownloadProceedChildListHolder){
            ((DownloadProceedChildListHolder)holder).bindView(downloadResourceList.get(position).getResource(), position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        int type =downloadResourceList.get(position).getResourceType();
        if(type == 1 || type == 2){
            return 0;
        }else{
            return 1;
        }
    }

    @Override
    public int getItemCount() {
        return downloadResourceList.size();
    }

    public void addAllData(List<DownloadResourceTableInfo> list){
        if(list == null || list.size() <= 0){
            return;
        }
        downloadResourceList.addAll(list);
        notifyDataSetChanged();
    }

    public List<DownloadResourceTableInfo> getData(){
        return downloadResourceList;
    }
}
