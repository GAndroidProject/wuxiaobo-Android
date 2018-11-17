package com.xiaoe.shop.wxb.business.column.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSONObject;
import com.xiaoe.common.entitys.AudioPlayEntity;
import com.xiaoe.common.entitys.ColumnDirectoryEntity;
import com.xiaoe.common.entitys.ColumnSecondDirectoryEntity;
import com.xiaoe.common.entitys.LoginUser;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.adapter.tree.ParentViewHolder;
import com.xiaoe.shop.wxb.adapter.tree.TreeRecyclerAdapter;
import com.xiaoe.shop.wxb.base.BaseFragment;
import com.xiaoe.shop.wxb.business.audio.presenter.AudioMediaPlayer;
import com.xiaoe.shop.wxb.business.audio.presenter.AudioPlayUtil;
import com.xiaoe.shop.wxb.business.audio.presenter.AudioPresenter;
import com.xiaoe.shop.wxb.business.download.ui.DownloadActivity;
import com.xiaoe.shop.wxb.common.JumpDetail;
import com.xiaoe.shop.wxb.events.AudioPlayEvent;
import com.xiaoe.shop.wxb.interfaces.OnClickListPlayListener;
import com.xiaoe.shop.wxb.widget.TouristDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class ColumnDirectoryFragment extends BaseFragment implements View.OnClickListener, OnClickListPlayListener {
    private static final String TAG = "ColumnDirectoryFragment";
    private View rootView;
    private RecyclerView directoryRecyclerView;
    private LinearLayout btnBatchDownload;
    private TreeRecyclerAdapter directoryAdapter;
    private boolean isHasBuy = false;
    private boolean isAddPlayList = false;
    private int playParentPosition = -1;
    private String resourceId;

    List<LoginUser> loginUserList;

    TouristDialog touristDialog;

    public ColumnDirectoryFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_column_directory, null, false);
        EventBus.getDefault().register(this);
        loginUserList = getLoginUserList();

        if (loginUserList.size() == 0) {
            touristDialog = new TouristDialog(getActivity());
            touristDialog.setDialogCloseClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    touristDialog.dismissDialog();
                }
            });
            touristDialog.setDialogConfirmClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JumpDetail.jumpLogin(getActivity(), true);
                }
            });
        }

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        directoryRecyclerView = (RecyclerView) rootView.findViewById(R.id.directory_recycler_view);
        btnBatchDownload = (LinearLayout) rootView.findViewById(R.id.btn_batch_download);
        btnBatchDownload.setOnClickListener(this);
    }
    private void initData() {
        LinearLayoutManager treeLinearLayoutManager = new LinearLayoutManager(getContext());
        treeLinearLayoutManager.setAutoMeasureEnabled(true);
        directoryRecyclerView.setLayoutManager(treeLinearLayoutManager);
        directoryRecyclerView.setNestedScrollingEnabled(false);
        directoryAdapter = new TreeRecyclerAdapter(getContext(), this);
        directoryRecyclerView.setAdapter(directoryAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_batch_download:
                clickBatchDownload();
                break;
            default:
                break;
        }
    }

    private void clickBatchDownload() {
        if (loginUserList.size() == 1) {
            if(!isHasBuy){
                toastCustom("未购买课程");
                return;
            }
            List<ColumnDirectoryEntity> newDataList = new ArrayList<ColumnDirectoryEntity>();
            for (ColumnDirectoryEntity directoryEntity : directoryAdapter.getData()){
                List<ColumnSecondDirectoryEntity> newChildDataList = new ArrayList<ColumnSecondDirectoryEntity>();
                for (ColumnSecondDirectoryEntity secondDirectoryEntity : directoryEntity.getResource_list()){
                    if(secondDirectoryEntity.getResource_type() == 3 && !TextUtils.isEmpty(secondDirectoryEntity.getVideo_url())){
                        newChildDataList.add(secondDirectoryEntity);
                    }else if(secondDirectoryEntity.getResource_type() == 2 && !TextUtils.isEmpty(secondDirectoryEntity.getAudio_url())){
                        newChildDataList.add(secondDirectoryEntity);
                    }
                }
                if(newChildDataList.size() > 0){
                    ColumnDirectoryEntity newDirectoryEntity = new ColumnDirectoryEntity();
                    newDirectoryEntity.setApp_id(directoryEntity.getApp_id());
                    newDirectoryEntity.setResource_type(directoryEntity.getResource_type());
                    newDirectoryEntity.setResource_id(directoryEntity.getResource_id());
                    newDirectoryEntity.setTitle(directoryEntity.getTitle());
                    newDirectoryEntity.setResource_list(newChildDataList);
                    newDirectoryEntity.setAudio_compress_url(directoryEntity.getAudio_compress_url());
                    newDirectoryEntity.setAudio_length(directoryEntity.getAudio_length());
                    newDirectoryEntity.setAudio_url(directoryEntity.getAudio_url());
                    newDirectoryEntity.setExpand(false);
                    newDirectoryEntity.setImg_url(directoryEntity.getImg_url());
                    newDirectoryEntity.setImg_url_compress(directoryEntity.getImg_url_compress());
                    newDirectoryEntity.setM3u8_url(directoryEntity.getM3u8_url());
                    newDirectoryEntity.setSelect(false);
                    newDirectoryEntity.setStart_at(directoryEntity.getStart_at());

                    newDataList.add(newDirectoryEntity);
                }
            }
            String dataJSON = JSONObject.toJSONString(newDataList);
            Intent intent = new Intent(getContext(), DownloadActivity.class);
            intent.putExtra("bundle_dataJSON", dataJSON);
            intent.putExtra("from_type", "ColumnDirectoryFragment");
            intent.putExtra("resourceId", resourceId);
            startActivity(intent);
        } else {
            touristDialog.showDialog();
        }
    }

    public void addData(List<ColumnDirectoryEntity> list){
        directoryAdapter.addAll(list);
    }
    public void refreshData(List<ColumnDirectoryEntity> list){
        directoryAdapter.refreshData(list);
    }
    public void setData(List<ColumnDirectoryEntity> list){
        directoryAdapter.setData(list);
    }
    public void clearData(){
        directoryAdapter.clearData();
    }

    @Override
    public void onPlayPosition(View view, int parentPosition, int position, boolean jumpDetail) {
        if (loginUserList.size() == 1) {
            if(!isHasBuy){
                toastCustom("未购买课程");
                return;
            }
            ColumnDirectoryEntity parentEntity = directoryAdapter.getPositionData(parentPosition);
            isAddPlayList = playParentPosition == parentPosition;
            if(position == -1){
                //播放全部
                List<AudioPlayEntity> playList = getAudioPlayList(parentEntity.getResource_list());
                clickPlayAll(playList);
            }else{
                //播放某一个，同时获取播放列表
                ColumnSecondDirectoryEntity playEntity = parentEntity.getResource_list().get(position);
                if(playEntity.getResource_type() == 2){
                    //音频
                    List<AudioPlayEntity> playList = getAudioPlayList(parentEntity.getResource_list());
                    if(!isAddPlayList){
                        AudioPlayUtil.getInstance().setAudioList(playList);
                        AudioPlayUtil.getInstance().setFromTag("Topic");
                        AudioPlayUtil.getInstance().setSingleAudio(false);
                    }
                    isAddPlayList = true;
                    playPosition(playList, playEntity.getResource_id(), playEntity.getColumnId(), playEntity.getBigColumnId(), jumpDetail);
                }
            }
            if(playParentPosition < 0){
                playParentPosition = parentPosition;
            }
            notifyDataSetChanged(parentPosition, position);
            playParentPosition = parentPosition;
        } else {
            touristDialog.showDialog();
        }
    }
    private void notifyDataSetChanged(int parentPosition, int position){
        ParentViewHolder parentViewHolder= (ParentViewHolder) directoryAdapter.getViewHolderList().get(parentPosition);
        parentViewHolder.getTreeChildRecyclerAdapter().notifyDataSetChanged();
        if(parentPosition != playParentPosition){
            ParentViewHolder oldParentViewHolder= (ParentViewHolder) directoryAdapter.getViewHolderList().get(playParentPosition);
            oldParentViewHolder.getTreeChildRecyclerAdapter().notifyDataSetChanged();
        }
//        if(position > 0){
//            parentViewHolder.getTreeChildRecyclerAdapter().notifyItemChanged(position);
//        }else{
//            parentViewHolder.getTreeChildRecyclerAdapter().notifyDataSetChanged();
//        }
    }
    @Override
    public void onJumpDetail(ColumnSecondDirectoryEntity itemData, int parentPosition, int position) {
        if (loginUserList.size() == 1) {
            if(!isHasBuy){
                toastCustom("未购买课程");
                return;
            }
            int resourceType = itemData.getResource_type();
            String resourceId = itemData.getResource_id();
            if(resourceType == 1){
                //图文
                JumpDetail.jumpImageText(getContext(), resourceId, null);
            }else if(resourceType == 2){
                //音频
                onPlayPosition(null, parentPosition, position, true);
                JumpDetail.jumpAudio(getContext(), resourceId, 1);
            }else if(resourceType == 3){
                //视频
                JumpDetail.jumpVideo(getContext(), resourceId, "",false);
            }else{
                toastCustom("未知课程");
                return;
            }
        } else {
            touristDialog.showDialog();
        }
    }

    public void setHasBuy(boolean hasBuy) {
        isHasBuy = hasBuy;
    }

    /**
     * 设置播放列表
     * @param list
     */
    private List<AudioPlayEntity> getAudioPlayList(List<ColumnSecondDirectoryEntity> list){
        List<AudioPlayEntity> playList = new ArrayList<AudioPlayEntity>();
        int index = 0;
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
            playEntity.setHasBuy(isHasBuy ? 1 : 0);
            playEntity.setColumnId(entity.getColumnId());
            playEntity.setBigColumnId(entity.getBigColumnId());
            playEntity.setTotalDuration(entity.getAudio_length());
            playEntity.setProductsTitle(entity.getColumnTitle());
            index++;
            playList.add(playEntity);
        }
        return playList;
    }

    private void clickPlayAll(List<AudioPlayEntity> playList) {

        if(playList.size() > 0){
            if(!isAddPlayList){
                AudioPlayUtil.getInstance().setAudioList(playList);
                AudioPlayUtil.getInstance().setFromTag("Topic");
                AudioPlayUtil.getInstance().setSingleAudio(false);
            }
            isAddPlayList = true;
            AudioMediaPlayer.stop();
            AudioPlayEntity playEntity = playList.get(0);
            playEntity.setPlay(true);
            AudioMediaPlayer.setAudio(playEntity, true);
            new AudioPresenter(null).requestDetail(playEntity.getResourceId());
        }
    }

    private void playPosition(List<AudioPlayEntity> playList, String resourceId, String columnId, String bigColumnId, boolean jumpDetail){
        boolean resourceEquals = false;
        AudioPlayEntity playAudio = AudioMediaPlayer.getAudio();
        if(playAudio != null){
            resourceEquals = AudioPlayUtil.resourceEquals(playAudio.getResourceId(), playAudio.getColumnId(), playAudio.getBigColumnId(),
                                                        resourceId, columnId, bigColumnId);
            playAudio.setPlaying(!playAudio.isPlaying());
        }
        //正在播放的资源和点击的资源相同，则播放暂停操作
        if(playAudio != null && resourceEquals){
            if(AudioMediaPlayer.isStop()){
                AudioMediaPlayer.start();
            }else{
                //如果是跳转详情页，则保持播放状态,如果不是，则进行播放暂停动作
                if(!jumpDetail){
                    AudioMediaPlayer.play();
                }
            }
            return;
        }
        AudioMediaPlayer.stop();
        for (AudioPlayEntity playEntity : playList) {
            if(playEntity.getResourceId().equals(resourceId)){
                playEntity.setPlaying(!playEntity.isPlaying());
                playEntity.setPlay(true);
                AudioMediaPlayer.setAudio(playEntity, true);
                new AudioPresenter(null).requestDetail(playEntity.getResourceId());
                break;
            }
        }
    }

    @Subscribe
    public void onEventMainThread(AudioPlayEvent event) {
        switch (event.getState()){
            case AudioPlayEvent.NEXT:
            case AudioPlayEvent.LAST:
                notifyDataSetChanged(playParentPosition, -1);
                break;
            case AudioPlayEvent.PAUSE:
            case AudioPlayEvent.PLAY:
            case AudioPlayEvent.STOP:
                directoryAdapter.notifyDataSetChanged();
                break;
            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }
}
