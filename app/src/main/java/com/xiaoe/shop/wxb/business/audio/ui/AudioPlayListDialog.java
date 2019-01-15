package com.xiaoe.shop.wxb.business.audio.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.scwang.smartrefresh.layout.listener.OnLoadMoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.xiaoe.common.entitys.AudioPlayEntity;
import com.xiaoe.common.entitys.ColumnSecondDirectoryEntity;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.adapter.audio.AudioPlayListNewAdapter;
import com.xiaoe.shop.wxb.business.audio.presenter.AudioListLoadMoreHelper;
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
    public static final String DEFAULT_LAST_ID = "-1";
    private String lastId = DEFAULT_LAST_ID;
    private final int PAGE_SIZE = 10;
    private SmartRefreshLayout mRefreshLayout;
    private StatusPagerView mStatusPagerView;
    private Context mContext;
//    private int page = 1;

    public void setLastId(String lastId) {
        this.lastId = lastId;
    }

//    public void setPage(int page) {
//        this.page = page;
//    }

    public void setLoadAudioListDataCallBack(LoadAudioListDataCallBack loadAudioListDataCallBack) {
        mLoadAudioListDataCallBack = loadAudioListDataCallBack;
        if (loadAudioListDataCallBack != null && DEFAULT_LAST_ID.equals(lastId)) {
            lastId = "";
//            page = 1;
            loadAudioListDataCallBack.onRefresh(PAGE_SIZE);
        }
    }

    public AudioPlayListDialog(Context context) {
        mContext = context;
        rootView = LayoutInflater.from(context).inflate(R.layout.layout_audio_play_list2, null, false);
        padding = Dp2Px2SpUtil.dp2px(context, 20);

        layoutAudioPlayList = (RelativeLayout) rootView.findViewById(R.id.layout_audio_play_list);
        layoutAudioPlayList.setOnClickListener(this);
        mRefreshLayout = (SmartRefreshLayout) rootView.findViewById(R.id.audio_refresh);
        mRefreshLayout.setEnableLoadMoreWhenContentNotFull(false);
//        mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
//            @Override
//            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
//                if (mLoadAudioListDataCallBack != null){
//                    mLoadAudioListDataCallBack.onLoadMoreData(lastId,PAGE_SIZE);
//                }
//            }
//
//            @Override
//            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
//                if (mLoadAudioListDataCallBack != null) {
//                    lastId = "";
//                    mLoadAudioListDataCallBack.onRefresh(PAGE_SIZE);
//                }
//            }
//        });
        mRefreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                if (mLoadAudioListDataCallBack != null){
                    mLoadAudioListDataCallBack.onLoadMoreData(lastId,PAGE_SIZE);
                }
            }
        });
        mRefreshLayout.setEnableRefresh(false);

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
        mStatusPagerView = new StatusPagerView(context);
        mStatusPagerView.setLoadingState(View.VISIBLE);
        mStatusPagerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLoadAudioListDataCallBack != null) {
                    lastId = "";
                    mLoadAudioListDataCallBack.onRefresh(PAGE_SIZE);
                }
            }
        });
        mAudioPlayListNewAdapter.setEmptyView(mStatusPagerView);
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
        String columnId = "";
        String bigColumnId = "";
        if (!AudioPlayUtil.getInstance().getAudioList().isEmpty()){
            AudioPlayEntity entity = AudioPlayUtil.getInstance().getAudioList().get(0);
            if (entity != null){
                columnId = entity.getColumnId();
                bigColumnId = entity.getBigColumnId();
            }
        }
        List<AudioPlayEntity> audioPlayEntities = new ArrayList<>();
        int index = 0;
        if ("" != lastId && DEFAULT_LAST_ID != lastId && mAudioPlayListNewAdapter != null
                && mAudioPlayListNewAdapter.getData().size() > 0)
            index = mAudioPlayListNewAdapter.getData().size();
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
            if (!TextUtils.isEmpty(columnId)){
                playEntity.setColumnId(columnId);
            }else {
                playEntity.setColumnId(entity.getColumnId());
            }
            if (!TextUtils.isEmpty(bigColumnId)){
                playEntity.setBigColumnId(bigColumnId);
            }else {
                playEntity.setBigColumnId(entity.getBigColumnId());
            }
            playEntity.setTotalDuration(entity.getAudio_length());
            playEntity.setProductsTitle(entity.getColumnTitle());
            playEntity.setIsTry(entity.getIsTry());
            index++;
            audioPlayEntities.add(playEntity);
        }
        addPlayListData(audioPlayEntities);
    }

    public void addPlayListData(List<AudioPlayEntity> list){
        addPlayListData(list,false);
    }

    public void addPlayListData(List<AudioPlayEntity> list,boolean isCache){
        if ("" == lastId && (list == null || 0 == list.size()))
            mStatusPagerView.setPagerState(StatusPagerView.FAIL, mContext.getString(
                    R.string.request_fail2), StatusPagerView.DETAIL_NONE);
        if (list != null){
            mRefreshLayout.finishRefresh();

            if ("" == lastId) {
                mAudioPlayListNewAdapter.setNewData(list);
                mRefreshLayout.setEnableLoadMore(list.size() >= PAGE_SIZE);
                if (isCache && !AudioMediaPlayer.isHasMoreData) {
                    mRefreshLayout.setEnableLoadMore(false);
                }else   AudioMediaPlayer.isHasMoreData = list.size() >= PAGE_SIZE;
            }else {
                mRefreshLayout.finishLoadMore();
                mAudioPlayListNewAdapter.addData(list);
                if (list.size() < PAGE_SIZE){
                    mRefreshLayout.finishLoadMoreWithNoMoreData();
                    mRefreshLayout.setEnableLoadMore(false);
                }
//                else{
//                    mRefreshLayout.finishLoadMore();
//                }
                if (isCache && !AudioMediaPlayer.isHasMoreData) {
                    mRefreshLayout.setEnableLoadMore(false);
                }else   AudioMediaPlayer.isHasMoreData = list.size() >= PAGE_SIZE;
            }
            if (list.size() > 0)    lastId = list.get(list.size() - 1).getResourceId();
            AudioMediaPlayer.lastId = lastId;
            AudioPlayUtil.getInstance().setAudioList(mAudioPlayListNewAdapter.getData());
            AudioPlayUtil.getInstance().setAudioList2(mAudioPlayListNewAdapter.getData());
            AudioListLoadMoreHelper.INSTANCE.setIndex(mAudioPlayListNewAdapter.getItemCount());
        }else if ("" != lastId){
            mRefreshLayout.finishRefresh();
            mRefreshLayout.finishLoadMore();
            AudioMediaPlayer.isHasMoreData = false;
        }else if ("" == lastId) {
            mRefreshLayout.finishRefresh();
            lastId = AudioMediaPlayer.lastId;
//            page = AudioMediaPlayer.mCurrentPage;
//            page++;
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
        void onLoadMoreData(String lastId,int pageSize);
    }
}
