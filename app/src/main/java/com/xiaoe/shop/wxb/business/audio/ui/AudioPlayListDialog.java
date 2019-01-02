package com.xiaoe.shop.wxb.business.audio.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.xiaoe.common.entitys.AudioPlayEntity;
import com.xiaoe.common.entitys.ColumnSecondDirectoryEntity;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.adapter.audio.AudioPlayListNewAdapter;
import com.xiaoe.shop.wxb.business.audio.presenter.AudioMediaPlayer;
import com.xiaoe.shop.wxb.business.audio.presenter.AudioPlayUtil;
import com.xiaoe.shop.wxb.events.HideAudioPlayListEvent;
import com.xiaoe.shop.wxb.widget.DashlineItemDivider;
import com.xiaoe.shop.wxb.widget.StatusPagerView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class AudioPlayListDialog implements View.OnClickListener {
    private static final String TAG = "AudioPlayListLayout";
    private View rootView;
    private RecyclerView playListRecyclerView;
    private int padding = 0;
//    private AudioPlayListAdapter playListAdapter;
    private AudioPlayListNewAdapter mAudioPlayListNewAdapter;
    private TextView btnCancel;
    private RelativeLayout layoutAudioPlayList;
    private TextView productsTitle;
    private AlertDialog dialog;
    private LoadAudioListDataCallBack mLoadAudioListDataCallBack;
    private int page = 1;
    private final int PAGE_SIZE = 10;
    private SmartRefreshLayout mRefreshLayout;

    public void setLoadAudioListDataCallBack(LoadAudioListDataCallBack loadAudioListDataCallBack) {
        mLoadAudioListDataCallBack = loadAudioListDataCallBack;
        if (loadAudioListDataCallBack != null) {
            page = 1;
            loadAudioListDataCallBack.onRefresh(PAGE_SIZE);
        }
    }

    public AudioPlayListDialog(Context context) {
        rootView = LayoutInflater.from(context).inflate(R.layout.layout_audio_play_list2, null, false);
        padding = Dp2Px2SpUtil.dp2px(context, 20);

        layoutAudioPlayList = (RelativeLayout) rootView.findViewById(R.id.layout_audio_play_list);
        layoutAudioPlayList.setOnClickListener(this);
        mRefreshLayout = (SmartRefreshLayout) rootView.findViewById(R.id.audio_refresh);
        mRefreshLayout.setEnableLoadMoreWhenContentNotFull(false);
        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (mLoadAudioListDataCallBack != null){
                    mLoadAudioListDataCallBack.onLoadMoreData(page,PAGE_SIZE);
                }
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (mLoadAudioListDataCallBack != null) {
                    page = 1;
                    mLoadAudioListDataCallBack.onRefresh(PAGE_SIZE);
                }
            }
        });

        //取消按钮
        btnCancel = (TextView) rootView.findViewById(R.id.btn_cancel_play_list);
        btnCancel.setOnClickListener(this);
        //列表
        playListRecyclerView = (RecyclerView) rootView.findViewById(R.id.play_list_recycler_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setAutoMeasureEnabled(true);
        playListRecyclerView.setLayoutManager(layoutManager);
        playListRecyclerView.addItemDecoration(new DashlineItemDivider(padding, padding));
//        playListAdapter = new AudioPlayListAdapter(context);
//        playListRecyclerView.setAdapter(playListAdapter);
        mAudioPlayListNewAdapter =new AudioPlayListNewAdapter(R.layout.layout_audio_play_list_item);
        StatusPagerView statusPagerView = new StatusPagerView(context);
        statusPagerView.setPagerState(StatusPagerView.FAIL, context.getString(R.string.request_fail), StatusPagerView.DETAIL_NONE);
        mAudioPlayListNewAdapter.setEmptyView(statusPagerView);
        mAudioPlayListNewAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                //点击的item和现在播放的资源不相同才播放，如果相同则放弃点击事件
                String resourceId = mAudioPlayListNewAdapter.getData().get(position).getResourceId();
                if(!resourceId.equals(AudioMediaPlayer.getAudio().getResourceId())){
                    AudioMediaPlayer.playAppoint(position);
                    EventBus.getDefault().post(new HideAudioPlayListEvent(true));
                }
            }
        });
//        mAudioPlayListNewAdapter.disableLoadMoreIfNotFullPage(playListRecyclerView);
        playListRecyclerView.setAdapter(mAudioPlayListNewAdapter);
        productsTitle = (TextView) rootView.findViewById(R.id.products_title);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        dialog = builder.create();
    }

    public void showDialog() {
        dialog.show();
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setWindowAnimations(R.style.ActionSheetDialogAnimation);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(null);
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setGravity(Gravity.BOTTOM);
        dialog.setContentView(rootView);
    }

//    public void addPlayData(List<AudioPlayEntity> list){
//        playListAdapter.addAll(list);
//    }
//
//    public void notifyDataSetChanged(){
//        if (playListAdapter != null)
//            playListAdapter.notifyDataSetChanged();
//    }

    public void addData(List<ColumnSecondDirectoryEntity> list,int isHasBuy){
        setAudioPlayList(list,isHasBuy);
    }

    private void setAudioPlayList(List<ColumnSecondDirectoryEntity> list,int isHasBuy){
        List<AudioPlayEntity> audioPlayEntities = new ArrayList<>();
        int index = 10 * (page - 1);
        for (ColumnSecondDirectoryEntity entity : list) {
            if(entity.getResource_type() != 2){
                continue;
            }
            AudioPlayEntity playEntity = new AudioPlayEntity();
            playEntity.setAppId(entity.getApp_id());
            playEntity.setResourceId(entity.getResource_id());
            playEntity.setIndex(index);
            playEntity.setCurrentPlayState(0);
            playEntity.setTitle(entity.getTitle());
            playEntity.setState(0);
            playEntity.setPlay(false);
            playEntity.setPlayUrl(entity.getAudio_url());
            playEntity.setCode(-1);
            playEntity.setHasBuy(isHasBuy);
            playEntity.setColumnId(entity.getColumnId());
            playEntity.setBigColumnId(entity.getBigColumnId());
            playEntity.setTotalDuration(entity.getAudio_length());
            playEntity.setProductsTitle(entity.getColumnTitle());
            playEntity.setIsTry(entity.getIsTry());
            index++;
            audioPlayEntities.add(playEntity);
        }
        addPlayListData(audioPlayEntities);
    }

    public void addPlayListData(List<AudioPlayEntity> list){
        if (list != null){
            mRefreshLayout.finishRefresh();
            if (1 == page) {
                mAudioPlayListNewAdapter.setNewData(list);
                mRefreshLayout.setEnableLoadMore(list.size() > 6);
            }else {
                mAudioPlayListNewAdapter.addData(list);
                if (list.size() < PAGE_SIZE){
                    mRefreshLayout.finishLoadMoreWithNoMoreData();
                    mRefreshLayout.setEnableLoadMore(false);
                }else{
                    mRefreshLayout.finishLoadMore();
                }
            }
            page++;
            AudioPlayUtil.getInstance().setAudioList(mAudioPlayListNewAdapter.getData());
        }else if (page > 1){
            mRefreshLayout.finishRefresh();
            mRefreshLayout.finishLoadMore();
        }
    }

    public void notifyDataSetChanged(){
        if (mAudioPlayListNewAdapter != null)
            mAudioPlayListNewAdapter.notifyDataSetChanged();
    }

    public boolean isShow(){
        return dialog != null && dialog.isShowing();
    }

    public void dismissDialog(){
        if (isShow())
            dialog.dismiss();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_audio_play_list:
            case R.id.btn_cancel_play_list:
                if (isShow())
                    dismissDialog();
                break;
            default:
                break;
        }
    }

    public void setProductsTitle(String title) {
        if (productsTitle != null)
            productsTitle.setText(title);
    }

    public interface LoadAudioListDataCallBack{
        void onRefresh(int pageSize);
        void onLoadMoreData(int page,int pageSize);
    }
}
