package com.xiaoe.shop.wxb.business.download.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xiaoe.common.entitys.DownloadResourceTableInfo;
import com.xiaoe.common.entitys.DownloadTableInfo;
import com.xiaoe.common.interfaces.OnDownloadListener;
import com.xiaoe.network.downloadUtil.DownloadManager;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.adapter.download.FinishDownloadListAdapter;
import com.xiaoe.shop.wxb.base.BaseFragment;
import com.xiaoe.shop.wxb.common.JumpDetail;
import com.xiaoe.shop.wxb.interfaces.IonSlidingViewClickListener;
import com.xiaoe.shop.wxb.widget.StatusPagerView;

import java.util.List;

public class FinishDownloadFragment extends BaseFragment implements IonSlidingViewClickListener, OnDownloadListener {
    private static final String TAG = "FinishDownloadFragment";
    private View rootView;
    private RecyclerView finishDownloadRecyclerView;
    private FinishDownloadListAdapter finishDownloadListAdapter;
    private StatusPagerView statePager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_download_finish, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
    }

    private void initViews() {
        DownloadManager.getInstance().setOnDownloadListener(TAG, this);

        finishDownloadRecyclerView = (RecyclerView) rootView.findViewById(R.id.finish_download_recycler_view);
        finishDownloadRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        finishDownloadListAdapter = new FinishDownloadListAdapter(getContext(), this);
        finishDownloadRecyclerView.setAdapter(finishDownloadListAdapter);
        List<DownloadResourceTableInfo> list = DownloadManager.getInstance().getDownloadFinishList();
        statePager = (StatusPagerView) rootView.findViewById(R.id.state_pager);
        setStatePager(View.GONE);
        if(list != null && list.size() > 0){
            finishDownloadListAdapter.setData(list);
        }else{
            setStatePager(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        if(finishDownloadListAdapter.menuIsOpen()){
            finishDownloadListAdapter.closeMenu();
        }else{
            DownloadResourceTableInfo download = finishDownloadListAdapter.getPositionData(position);

            if(download == null){
                return;
            }else if(download.getResourceType() == 1){
                //音频
                JumpDetail.jumpAudio(getContext(), download.getResourceId(), 1);
            }else if(download.getResourceType() == 2){
                //视频
                JumpDetail.jumpVideo(getContext(), download.getResourceId(), null, true, "");
            }
        }
    }
    @Override
    public void onDeleteBtnClick(View view, int position) {
        DownloadResourceTableInfo remove = finishDownloadListAdapter.removeData(position);
        DownloadManager.getInstance().removeDownloadFinish(remove);
        if(finishDownloadListAdapter.getItemCount() <= 0){
            setStatePager(View.VISIBLE);
        }else{
            setStatePager(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        DownloadManager.getInstance().removeOnDownloadListener(TAG);
    }

    @Override
    public void onDownload(DownloadTableInfo downloadInfo, float progress, int status) {
        if(status == 3){
            //下载完成
            DownloadResourceTableInfo downloadResourceTableInfo = new DownloadResourceTableInfo();
            downloadResourceTableInfo.setAppId(downloadInfo.getAppId());
            downloadResourceTableInfo.setResourceId(downloadInfo.getResourceId());
            downloadResourceTableInfo.setTitle(downloadInfo.getTitle());
            downloadResourceTableInfo.setImgUrl(downloadInfo.getImgUrl());
            if(downloadInfo.getResourceType() == 2){
                downloadResourceTableInfo.setResourceType(1);
            }else if(downloadInfo.getResourceType() == 3){
                downloadResourceTableInfo.setResourceType(2);
            }
            downloadResourceTableInfo.setDepth(0);
            downloadResourceTableInfo.setLocalFilePath(downloadInfo.getLocalFilePath());
            downloadResourceTableInfo.setFileUrl(downloadInfo.getFileDownloadUrl());
            finishDownloadListAdapter.addData(downloadResourceTableInfo);
            setStatePager(View.GONE);
        }
    }

    private void setStatePager(int visible){
        if(visible == View.VISIBLE){
            statePager.setVisibility(View.VISIBLE);
            statePager.setLoadingState(View.GONE);
            statePager.setHintStateVisibility(View.VISIBLE);
            statePager.setStateText(getString(R.string.not_has_content));
            statePager.setStateImage(R.mipmap.downloading);
        }else{
            statePager.setVisibility(View.GONE);
        }
    }
}
