package xiaoe.com.shop.business.column.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import xiaoe.com.common.entitys.AudioPlayEntity;
import xiaoe.com.common.entitys.ColumnSecondDirectoryEntity;
import xiaoe.com.common.utils.Dp2Px2SpUtil;
import xiaoe.com.shop.R;
import xiaoe.com.shop.adapter.tree.TreeChildRecyclerAdapter;
import xiaoe.com.shop.base.BaseFragment;
import xiaoe.com.shop.business.audio.presenter.AudioMediaPlayer;
import xiaoe.com.shop.business.audio.presenter.AudioPlayUtil;
import xiaoe.com.shop.business.audio.presenter.AudioPresenter;
import xiaoe.com.shop.interfaces.OnClickListPlayListener;
import xiaoe.com.shop.widget.DashlineItemDivider;

public class LittleColumnDirectoryFragment extends BaseFragment implements View.OnClickListener, OnClickListPlayListener {
    private static final String TAG = "ColumnDirectoryFragment";
    private View rootView;
    private RecyclerView directoryRecyclerView;
    private TreeChildRecyclerAdapter directoryAdapter;
    private LinearLayout btnPlayAll;
    private List<AudioPlayEntity> playList = null;
    private boolean isAddPlayList = false;

    public LittleColumnDirectoryFragment() {
        playList = new ArrayList<AudioPlayEntity>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_little_column_directory, null, false);
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
        btnPlayAll.setOnClickListener(this);
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
    }

    public void addData(List<ColumnSecondDirectoryEntity> list){
        directoryAdapter.addAll(list);
        setAudioPlayList(list);
    }
    public void setData(List<ColumnSecondDirectoryEntity> list){
        directoryAdapter.setData(list);
        setAudioPlayList(list);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_all_play:
                clickPlayAll();
                break;
            default:
                break;
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
            index++;
            playList.add(playEntity);
        }
    }

    private void clickPlayAll() {
        if(playList.size() > 0){
            isAddPlayList = true;
            AudioPlayUtil.getInstance().setAudioList(playList);
            AudioMediaPlayer.stop();
            AudioPlayEntity playEntity = playList.get(0);
            playEntity.setPlay(true);
            AudioMediaPlayer.setAudio(playEntity, false);
            new AudioPresenter(null).requestDetail(playEntity.getResourceId());
        }
    }

    private void playPosition(int position){
        if(playList.size() > 0){
            if(!isAddPlayList){
                isAddPlayList = true;
                AudioPlayUtil.getInstance().setAudioList(playList);
            }
            AudioMediaPlayer.stop();
            AudioPlayEntity playEntity = playList.get(position);
            playEntity.setPlay(true);
            AudioMediaPlayer.setAudio(playEntity, false);
            new AudioPresenter(null).requestDetail(playEntity.getResourceId());
        }
    }

    @Override
    public void OnPlayPosition(View view, int position) {
        ColumnSecondDirectoryEntity itemData = directoryAdapter.getData().get(position);
        if(itemData.getResource_type() == 2){
            playPosition(position);
        }
    }
}
