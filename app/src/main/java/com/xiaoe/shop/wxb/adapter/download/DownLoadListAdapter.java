package com.xiaoe.shop.wxb.adapter.download;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.xiaoe.common.entitys.CommonDownloadBean;
import com.xiaoe.common.interfaces.OnItemClickWithCdbItemListener;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: zak
 * @date: 2019/1/3
 */
public class DownloadListAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    public static final int GROUP_TYPE = 4001;
    public static final int SINGLE_TYPE = 4002;

    private Context mContext;
    private List<CommonDownloadBean> mDownloadGroupBeanList;
    private int mInitType;
    private LayoutInflater mLayoutInflater;
    private FrameLayout.LayoutParams layoutParams;
    private OnItemClickWithCdbItemListener onItemClickWithCdbItemListener;

    public DownloadListAdapter(Context context) {
        this.mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mDownloadGroupBeanList = new ArrayList<>();
        layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public DownloadListAdapter(Context context, int initType, List<CommonDownloadBean> downloadGroupBeanList) {
        this.mContext = context;
        this.mInitType = initType;
        this.mDownloadGroupBeanList = downloadGroupBeanList;
        mLayoutInflater = LayoutInflater.from(context);
        layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void setOnItemClickWithCdbItemListener(OnItemClickWithCdbItemListener onItemClickWithCdbItemListener) {
        this.onItemClickWithCdbItemListener = onItemClickWithCdbItemListener;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case GROUP_TYPE:
                view = mLayoutInflater.inflate(R.layout.download_list_group_item, parent, false);
                view.setLayoutParams(layoutParams);
                return new DownloadListItemViewHolder(mContext, view, mInitType);
            case SINGLE_TYPE:
                view = mLayoutInflater.inflate(R.layout.download_list_single_item, parent, false);
                view.setLayoutParams(layoutParams);
                return new DownloadListItemViewHolder(mContext, view, mInitType);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int currentPos = holder.getAdapterPosition();
        DownloadListItemViewHolder viewHolder = (DownloadListItemViewHolder) holder;
        viewHolder.initViewHolder(mDownloadGroupBeanList.get(currentPos), onItemClickWithCdbItemListener);
    }

    @Override
    public int getItemCount() {
        return mDownloadGroupBeanList == null ? 0 : mDownloadGroupBeanList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mInitType == GROUP_TYPE) {
            return GROUP_TYPE;
        } else if (mInitType == SINGLE_TYPE) {
            return SINGLE_TYPE;
        } else {
            return -1;
        }
    }

    /**
     * 设置新的数据
     *
     * @param commonDownloadBeanList 新的下载数据
     */
    public void setNewData(List<CommonDownloadBean> commonDownloadBeanList) {
        if (mDownloadGroupBeanList == null) {
            mDownloadGroupBeanList = new ArrayList<>();
        }
        if (mDownloadGroupBeanList.size() > 0) {
            mDownloadGroupBeanList.clear();
        }
        mDownloadGroupBeanList.addAll(commonDownloadBeanList);
        notifyDataSetChanged();
    }

    /**
     * 设置初始化类型，在设置数据之前使用
     *
     * @param initType 列表初始化类型
     */
    public void setInitType(int initType) {
        mInitType = initType;
    }
}