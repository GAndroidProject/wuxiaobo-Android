package com.xiaoe.shop.wxb.business.download.ui;

import android.app.Dialog;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xiaoe.common.app.Global;
import com.xiaoe.common.entitys.DownloadTableInfo;
import com.xiaoe.common.interfaces.OnDownloadListener;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.common.utils.NetUtils;
import com.xiaoe.network.downloadUtil.DownloadManager;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.adapter.download.DownloadProceedChildListAdapter;
import com.xiaoe.shop.wxb.base.BaseFragment;
import com.xiaoe.shop.wxb.interfaces.OnDownloadListListener;
import com.xiaoe.shop.wxb.widget.CustomDialog;
import com.xiaoe.shop.wxb.widget.StatusPagerView;

import java.util.List;

public class DownloadProceedFragment extends BaseFragment implements View.OnClickListener, OnDownloadListListener, OnDownloadListener {
    private static final String TAG = "DownloadProceedFragment";
    private View rootView;
    private RecyclerView downloadProceedRecyclerView;
    private DownloadProceedChildListAdapter adapter;
    private TextView btnAllDelete;
    private Dialog dialog;
    private Window window;
    private TextView btnAllStart;
    private boolean isAllDownload = false;
    private boolean runClickAllDownload = false;
    private RelativeLayout bottomButton;
    private StatusPagerView statePager;
    private OffLineCacheActivity cacheActivity;
    private DownloadTableInfo startDownload;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_download_proceed, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        initData();
    }

    private void initViews() {
        cacheActivity = (OffLineCacheActivity) getActivity();
        DownloadManager.getInstance().setOnDownloadListener(TAG, this);

        downloadProceedRecyclerView = (RecyclerView) rootView.findViewById(R.id.download_proceed_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setAutoMeasureEnabled(true);
        downloadProceedRecyclerView.setLayoutManager(layoutManager);
        adapter = new DownloadProceedChildListAdapter(getContext(), this);
        downloadProceedRecyclerView.setAdapter(adapter);
        btnAllDelete = cacheActivity.getAllDeleteButton();
        btnAllDelete.setOnClickListener(this);

        btnAllStart = cacheActivity.getAllStartDownloadButton();
        btnAllStart.setOnClickListener(this);
        //底部的按钮
        bottomButton = cacheActivity.getBottomButton();
        //状态页
        statePager = (StatusPagerView) rootView.findViewById(R.id.state_pager);
        setStatePager(View.GONE);
    }
    private void initData() {
        List<DownloadTableInfo> list = DownloadManager.getInstance().getDownloadingList();
        if(list == null || list.size() <= 0){
            setStatePager(View.VISIBLE);
        }else {
            adapter.addAllData(list);
            isAllDownload = isAllDownload();
        }
        setAllDownloadButton(isAllDownload);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser && bottomButton != null && adapter != null && adapter.getItemCount() > 0){
            bottomButton.setVisibility(View.VISIBLE);
        }
        if(isVisibleToUser && bottomButton != null){
            if(bottomButton.getVisibility() == View.VISIBLE){
                cacheActivity.setMiniPlayerPosition(RelativeLayout.ABOVE, R.id.bottom_button);
            }else{
                cacheActivity.setMiniPlayerPosition(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            }
        }
    }

    private void setAllDownloadButton(boolean all) {
        if(all){
            btnAllStart.setText(getString(R.string.all_stop_download));
            btnAllStart.setTextColor(getResources().getColor(R.color.recent_list_color));
            btnAllStart.setBackgroundResource(R.drawable.all_delete_download_white);
        }else{
            btnAllStart.setText(getString(R.string.all_start_download));
            btnAllStart.setTextColor(getResources().getColor(R.color.white));
            btnAllStart.setBackgroundResource(R.drawable.recent_update_btn_pressed);
        }
    }

    private boolean isAllDownload(){
        int downloadCount = 0;
        for(DownloadTableInfo item : adapter.getData()){
            if(item.getDownloadState() == 0 || item.getDownloadState() == 1){
                downloadCount++;
            }
        }
        return downloadCount == adapter.getData().size();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        DownloadManager.getInstance().removeOnDownloadListener(TAG);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_all_delete:
                allDelete();
                break;
            case R.id.btn_all_start_download:
                allStartDownload();
                break;
            default:
                break;
        }
    }

    private void allStartDownload() {
        if(NetUtils.NETWORK_TYPE_NO_NETWORK.equals(NetUtils.getNetworkType(getContext())) || NetUtils.NETWORK_TYPE_UNKONW_NETWORK.equals(NetUtils.getNetworkType(getContext()))){
            Toast.makeText(getContext(), getString(R.string.network_error_text), Toast.LENGTH_SHORT).show();
            return;
        }
        if(adapter.getItemCount() <= 0 || runClickAllDownload){
            return;
        }
        runClickAllDownload = true;
        if(isAllDownload){
            //先停止等待中的下载任务，因为如果先停止下载中的，会自动下载等待中的
            for (DownloadTableInfo download : adapter.getData()){
                if(download.getDownloadState() == 0){
                    DownloadManager.getInstance().pause(download);
                }
            }
            for (DownloadTableInfo download : adapter.getData()){
                if(download.getDownloadState() == 1){
                    DownloadManager.getInstance().pause(download);
                }
            }
        }else{
            if(!NetUtils.NETWORK_TYPE_WIFI.equals(NetUtils.getNetworkType(getContext()))){
                hintNotWIFIDownload(CustomDialog.NOT_WIFI_NET_DOWNLOAD_TAG);
            }else{
                for (DownloadTableInfo download : adapter.getData()){
                    if(download.getDownloadState() == 2){
                        startDownload(download);
                    }
                }
            }
        }
        runClickAllDownload = false;
        isAllDownload = !isAllDownload;
        setAllDownloadButton(isAllDownload);
    }
    private void hintNotWIFIDownload(int tag){
        getDialog().setMessageVisibility(View.GONE);
        getDialog().getTitleView().setGravity(Gravity.START);
        getDialog().getTitleView().setPadding(Dp2Px2SpUtil.dp2px(getContext(), 22), 0, Dp2Px2SpUtil.dp2px(getContext(), 22), 0 );
        getDialog().getTitleView().setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        getDialog().setCancelable(false);
        getDialog().setHideCancelButton(false);
        getDialog().setTitle(getString(R.string.not_wifi_net_download_hint));
        getDialog().setConfirmText(getString(R.string.confirm_title));
        getDialog().setCancelText(getString(R.string.cancel_title));
        getDialog().showDialog(tag);
    }
    /**
     * 开始下载
     * @param download
     */
    private void startDownload(DownloadTableInfo download){
        DownloadManager.getInstance().start(download);
    }

    private void allDelete() {
        if(adapter.getItemCount() > 0){
            deleteAll();
        }
    }

    @Override
    public void onClickConfirm(View view, int tag) {
        super.onClickConfirm(view, tag);
        if(CustomDialog.NOT_WIFI_NET_DOWNLOAD_TAG == tag){
            for (DownloadTableInfo download : adapter.getData()){
                if(download.getDownloadState() == 2){
                    startDownload(download);
                }
            }
        }else if(CustomDialog.NOT_WIFI_NET_DOWNLOAD_ITEM_TAG == tag && startDownload != null){
            DownloadManager.getInstance().start(startDownload);
            startDownload = null;
        }
    }

    @Override
    public void downloadItem(DownloadTableInfo download, int position, int type) {
        if(type == 0){
            //开始、暂停
            if(download.getDownloadState() == 0 || download.getDownloadState() == 1){
                DownloadManager.getInstance().pause(download);
            }else if(download.getDownloadState() == 2){
                if(NetUtils.NETWORK_TYPE_NO_NETWORK.equals(NetUtils.getNetworkType(getContext())) || NetUtils.NETWORK_TYPE_UNKONW_NETWORK.equals(NetUtils.getNetworkType(getContext()))){
                    Toast.makeText(getContext(), getString(R.string.network_error_text), Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!NetUtils.NETWORK_TYPE_WIFI.equals(NetUtils.getNetworkType(getContext()))){
                    startDownload = download;
                    hintNotWIFIDownload(CustomDialog.NOT_WIFI_NET_DOWNLOAD_ITEM_TAG);
                }else{
                    DownloadManager.getInstance().start(download);
                }
            }
        }else if(type == 1){
            //删除
            delete(download, position);
        }
        setAllDownloadButton(isAllDownload());
    }


    public void delete(final DownloadTableInfo download, final int position){
        if(dialog == null){
            dialog = new Dialog(getContext(), R.style.ActionSheetDialogStyle);
        }
        if(window == null){
            window = dialog.getWindow();
        }
        View view = getActivity().getLayoutInflater().inflate(R.layout.layout_delete_dialog, null);

        TextView btnCancel = (TextView) view.findViewById(R.id.radius_dialog_btn_cancel);
        TextView btnConfirm = (TextView) view.findViewById(R.id.radius_dialog_btn_confirm);
        TextView delHintTitle = (TextView) view.findViewById(R.id.radius_dialog_title);
        delHintTitle.setText(getString(R.string.delete_download_hint));

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                DownloadManager.getInstance().removeDownloading(download);
                adapter.getData().remove(position);
                adapter.notifyDataSetChanged();
                if(adapter.getItemCount() <= 0){
                    bottomButton.setVisibility(View.GONE);
                    cacheActivity.setMiniPlayerPosition(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                    setStatePager(View.VISIBLE);
                }
            }
        });
        // 先 show 后才会有宽高
        dialog.show();

        if (window != null) {
            window.setGravity(Gravity.CENTER);
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            Point point = Global.g().getDisplayPixel();
            layoutParams.width = (int) (point.x * 0.8);
            window.setAttributes(layoutParams);
        }
        dialog.setContentView(view);
    }
    public void deleteAll(){
        if(dialog == null){
            dialog = new Dialog(getContext(), R.style.ActionSheetDialogStyle);
        }
        if(window == null){
            window = dialog.getWindow();
        }
        View view = getActivity().getLayoutInflater().inflate(R.layout.layout_delete_dialog, null);

        TextView btnCancel = (TextView) view.findViewById(R.id.radius_dialog_btn_cancel);
        final TextView btnConfirm = (TextView) view.findViewById(R.id.radius_dialog_btn_confirm);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (DownloadTableInfo download : adapter.getData()){
                    DownloadManager.getInstance().removeDownloading(download);
                }
                adapter.clearData();
                dialog.dismiss();
                bottomButton.setVisibility(View.GONE);
                cacheActivity.setMiniPlayerPosition(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                setStatePager(View.VISIBLE);
            }
        });
        // 先 show 后才会有宽高
        dialog.show();

        if (window != null) {
            window.setGravity(Gravity.CENTER);
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            Point point = Global.g().getDisplayPixel();
            layoutParams.width = (int) (point.x * 0.8);
            window.setAttributes(layoutParams);
        }
        dialog.setContentView(view);
    }

    @Override
    public void onDownload(DownloadTableInfo downloadInfo, float progress, int status) {
        if(status == 3){
            //下载完成
            if(dialog != null && dialog.isShowing()){
                dialog.dismiss();
            }
            for (DownloadTableInfo item : adapter.getData()){
                if(item.getResourceId().equals(downloadInfo.getResourceId())){
                    adapter.getData().remove(item);
                    adapter.notifyDataSetChanged();
                    break;
                }
            }
            if(adapter.getItemCount() <= 0){
                bottomButton.setVisibility(View.GONE);
                cacheActivity.setMiniPlayerPosition(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                setStatePager(View.VISIBLE);
            }else{
                setStatePager(View.GONE);
            }
        }
        int index = -1;
        for (DownloadTableInfo item : adapter.getData()){
            index++;
            if(item.getResourceId().equals(downloadInfo.getResourceId())){
                item.setProgress(downloadInfo.getProgress());
                item.setTotalSize(downloadInfo.getTotalSize());
                item.setDownloadState(downloadInfo.getDownloadState());
//                adapter.notifyItemChanged(index);
                adapter.notifyItemChanged(index,item);
                break;
            }
        }
    }

    private void setStatePager(int visible){
        if(visible == View.VISIBLE){
            statePager.setVisibility(View.VISIBLE);
            statePager.setLoadingState(View.GONE);
            statePager.setHintStateVisibility(View.VISIBLE);
            statePager.setStateText(getString(R.string.not_has_download_content));
            statePager.setStateImage(R.mipmap.downloading);
        }else{
            statePager.setVisibility(View.GONE);
        }
    }
}
