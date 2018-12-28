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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.xiaoe.common.entitys.AudioPlayEntity;
import com.xiaoe.common.entitys.ColumnDirectoryEntity;
import com.xiaoe.common.entitys.ColumnSecondDirectoryEntity;
import com.xiaoe.common.entitys.LoginUser;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.network.downloadUtil.DownloadManager;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.adapter.tree.TreeChildRecyclerAdapter;
import com.xiaoe.shop.wxb.base.BaseFragment;
import com.xiaoe.shop.wxb.business.audio.presenter.AudioMediaPlayer;
import com.xiaoe.shop.wxb.business.audio.presenter.AudioPlayUtil;
import com.xiaoe.shop.wxb.business.audio.presenter.AudioPresenter;
import com.xiaoe.shop.wxb.business.download.ui.DownloadActivity;
import com.xiaoe.shop.wxb.common.JumpDetail;
import com.xiaoe.shop.wxb.events.AudioPlayEvent;
import com.xiaoe.shop.wxb.events.OnClickEvent;
import com.xiaoe.shop.wxb.interfaces.OnClickListPlayListener;
import com.xiaoe.shop.wxb.utils.ToastUtils;
import com.xiaoe.shop.wxb.widget.DashlineItemDivider;
import com.xiaoe.shop.wxb.widget.TouristDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class MemberFragment extends BaseFragment implements View.OnClickListener, OnClickListPlayListener {
    private static final String TAG = "ColumnDirectoryFragment";
    private View rootView;
    private RecyclerView directoryRecyclerView;
    private TreeChildRecyclerAdapter directoryAdapter;
    private LinearLayout btnPlayAll;
    private List<AudioPlayEntity> playList = null;
    private boolean isAddPlayList = false;
    private boolean isHasBuy = false;
    private String resourceId;

    List<LoginUser> loginUserList;
    TouristDialog touristDialog;
    private boolean isAddList = false;
    private List<ColumnSecondDirectoryEntity> tempList;
    private ImageView allPlayIcon;
    private TextView allPlayText;

    public MemberFragment() {
        playList = new ArrayList<AudioPlayEntity>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_little_column_directory, null, false);
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
                    touristDialog.dismissDialog();
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
        btnPlayAll = (LinearLayout) rootView.findViewById(R.id.btn_all_play);
        btnPlayAll.setOnClickListener(new OnClickEvent(OnClickEvent.DEFAULT_SECOND) {
            @Override
            public void singleClick(View v) {
                clickPlayAll();
            }
        });

        //全部播放状态icon
        allPlayIcon = (ImageView) rootView.findViewById(R.id.all_play_icon);
        //全部播放状态
        allPlayText = (TextView) rootView.findViewById(R.id.all_play_text);
        setAllPlayState();

        LinearLayout btnBatchDownload = (LinearLayout) rootView.findViewById(R.id.btn_batch_download);
        btnBatchDownload.setOnClickListener(this);
    }
    private void initData() {
        LinearLayoutManager treeLinearLayoutManager = new LinearLayoutManager(getContext());
        treeLinearLayoutManager.setAutoMeasureEnabled(true);
        directoryRecyclerView.setLayoutManager(treeLinearLayoutManager);
        directoryRecyclerView.setNestedScrollingEnabled(false);
        int padding = Dp2Px2SpUtil.dp2px(getContext(), 16);
        directoryRecyclerView.addItemDecoration(new DashlineItemDivider(padding, padding));
        directoryAdapter = new TreeChildRecyclerAdapter(getContext(), this);
        directoryRecyclerView.setAdapter(directoryAdapter);
        if(tempList != null && !isAddList){
            directoryAdapter.addAll(tempList);
        }
    }

    public void addData(List<ColumnSecondDirectoryEntity> list){
        if(directoryAdapter != null){
            isAddList = true;
            directoryAdapter.addAll(list);
        }else{
            tempList = list;
        }
        setAudioPlayList(list);
    }
    public void setData(List<ColumnSecondDirectoryEntity> list){
        directoryAdapter.setData(list);
        setAudioPlayList(list);
    }

    public void refreshData(List<ColumnSecondDirectoryEntity> list){
        if (directoryAdapter == null) {
            directoryAdapter = new TreeChildRecyclerAdapter(getContext(), this);
        }
        directoryAdapter.refreshData(list);
    }

    public void clearData(){
        directoryAdapter.clearData();
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
                toastCustom(getString(R.string.courses_not_purchased));
                return;
            }
            List<ColumnSecondDirectoryEntity> newChildDataList = new ArrayList<ColumnSecondDirectoryEntity>();
            int downloadCount = 0;
            for (ColumnSecondDirectoryEntity item : directoryAdapter.getData()){
                if(item.getResource_type() == 3 && !TextUtils.isEmpty(item.getVideo_url())){
                    boolean isDownload = DownloadManager.getInstance().isDownload(item.getApp_id(),item.getResource_id());
                    item.setEnable(!isDownload);
                    newChildDataList.add(item);
                }else if(item.getResource_type() == 2 && !TextUtils.isEmpty(item.getAudio_url())){
                    boolean isDownload = DownloadManager.getInstance().isDownload(item.getApp_id(),item.getResource_id());
                    item.setEnable(!isDownload);
                    newChildDataList.add(item);
                }
                if(!item.isEnable()){
                    downloadCount++;
                }
            }

            List<ColumnDirectoryEntity> newDataList = new ArrayList<ColumnDirectoryEntity>();
            if(newChildDataList.size() > 0){
                ColumnDirectoryEntity directoryEntity = new ColumnDirectoryEntity();
                directoryEntity.setEnable(!(downloadCount == newChildDataList.size()));
                directoryEntity.setTitle(((ColumnActivity)getActivity()).getColumnTitle());
                directoryEntity.setResource_list(newChildDataList);
                newDataList.add(directoryEntity);
            }
            String dataJSON = JSONObject.toJSONString(newDataList);
            Intent intent = new Intent(getContext(), DownloadActivity.class);
            intent.putExtra("bundle_dataJSON", dataJSON);
            intent.putExtra("from_type", "LittleColumnDirectoryFragment");
            intent.putExtra("resourceId", resourceId);
            intent.putExtra("resourceType", ColumnActivity.RESOURCE_TYPE_MEMBER + "");
            intent.putExtra("down_title", ((ColumnActivity)getActivity()).title);
            startActivity(intent);
        } else {
            touristDialog.showDialog();
        }
    }

    private void setAudioPlayList(List<ColumnSecondDirectoryEntity> list){
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
            playEntity.setIsTry(entity.getIsTry());
            index++;
            playList.add(playEntity);
        }
    }

    private void clickPlayAll() {
        if (loginUserList.size() == 1) {
            if(!isHasBuy && playList.size() <= 0){
                toastCustom(getString(R.string.courses_not_purchased));
                return;
            }
            if(playList.size() > 0){
                if(!isAddPlayList){
                    AudioPlayUtil.getInstance().setAudioList(playList);
                    AudioPlayUtil.getInstance().setFromTag("Column");
                    AudioPlayUtil.getInstance().setSingleAudio(false);
                }

                //比较播放中的专栏是否和点击的状态相同
                AudioPlayEntity audioPlayEntity = AudioMediaPlayer.getAudio();
                boolean resourceEquals = !TextUtils.isEmpty(resourceId) && audioPlayEntity != null
                        && resourceId.equals(audioPlayEntity.getColumnId());
                if(resourceEquals && AudioMediaPlayer.isPlaying()){
                    //点击全部暂停
                    AudioMediaPlayer.play();
                    return;
                }

                isAddPlayList = true;
                AudioMediaPlayer.stop();
                AudioPlayEntity playEntity = playList.get(0);
                playEntity.setPlaying(!playEntity.isPlaying());
                playEntity.setPlay(true);
                AudioMediaPlayer.setAudio(playEntity, true);
                new AudioPresenter(null).requestDetail(playEntity.getResourceId());
                directoryAdapter.notifyDataSetChanged();
            }else {
                ToastUtils.show(getContext(), R.string.no_audio_playback);
            }
        } else {
            touristDialog.showDialog();
        }
    }

    private void playPosition(String resourceId, String columnId, String bigColumnId, boolean jumpDetail){
        if(!isAddPlayList){
            isAddPlayList = true;
            AudioPlayUtil.getInstance().setAudioList(playList);
            AudioPlayUtil.getInstance().setFromTag("Column");
            AudioPlayUtil.getInstance().setSingleAudio(false);
        }
        AudioPlayEntity playAudio = AudioMediaPlayer.getAudio();

        boolean resourceEquals = false;
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
        directoryAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPlayPosition(View view,int parentPosition, int position, boolean jumpDetail) {
        if (loginUserList.size() == 1) {
            if(!isHasBuy){
                toastCustom(getString(R.string.courses_not_purchased));
                return;
            }
            ColumnSecondDirectoryEntity itemData = directoryAdapter.getData().get(position);
            if(itemData.getResource_type() == 2){
                playPosition(itemData.getResource_id(), itemData.getColumnId(), itemData.getBigColumnId(), jumpDetail);
            }
        } else {
            touristDialog.showDialog();
        }
    }

    @Override
    public void onJumpDetail(ColumnSecondDirectoryEntity itemData, int parentPosition, int position) {
        if (loginUserList.size() == 1) {
            if(!isHasBuy){
                toastCustom(getString(R.string.courses_not_purchased));
                return;
            }
            int resourceType = itemData.getResource_type();
            String resourceId = itemData.getResource_id();
            if(resourceType == 1){
                //图文
                JumpDetail.jumpImageText(getContext(), resourceId, null, "");
            }else if(resourceType == 2){
                //音频
                onPlayPosition(null, parentPosition, position, true);
                JumpDetail.jumpAudio(getContext(), resourceId, 1);
            }else if(resourceType == 3){
                //视频
                JumpDetail.jumpVideo(getContext(), resourceId, "",false, "");
            }else{
                toastCustom(getString(R.string.unknown_course));
            }
        } else {
            touristDialog.showDialog();
        }
    }

    public void setHasBuy(boolean hasBuy) {
        isHasBuy = hasBuy;
    }

    @Subscribe
    public void onEventMainThread(AudioPlayEvent event) {
        switch (event.getState()){
            case AudioPlayEvent.NEXT:
            case AudioPlayEvent.LAST:
            case AudioPlayEvent.PAUSE:
            case AudioPlayEvent.PLAY:
            case AudioPlayEvent.STOP:
                setAllPlayState();
                directoryAdapter.notifyDataSetChanged();
            default:
                break;
        }
    }

    private void setAllPlayState(){
        if(!TextUtils.isEmpty(resourceId) && AudioMediaPlayer.isPlaying() && resourceId.equals(AudioMediaPlayer.getAudio().getColumnId())){
            allPlayText.setText(getString(R.string.stop_all));
            allPlayIcon.setImageResource(R.mipmap.audiolist_playing);
        }else{
            allPlayText.setText(getString(R.string.play_all));
            allPlayIcon.setImageResource(R.mipmap.audiolist_playall);
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
