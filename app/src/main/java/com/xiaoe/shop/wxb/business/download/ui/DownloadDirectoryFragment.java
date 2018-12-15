package com.xiaoe.shop.wxb.business.download.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.xiaoe.common.entitys.ColumnDirectoryEntity;
import com.xiaoe.common.entitys.ColumnSecondDirectoryEntity;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.common.utils.NetUtils;
import com.xiaoe.network.downloadUtil.DownloadManager;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.adapter.download.BatchDownloadAdapter;
import com.xiaoe.shop.wxb.base.BaseFragment;
import com.xiaoe.shop.wxb.interfaces.OnSelectListener;
import com.xiaoe.shop.wxb.widget.CustomDialog;

import java.util.ArrayList;
import java.util.List;

public class DownloadDirectoryFragment extends BaseFragment implements View.OnClickListener, OnSelectListener {
    private static final String TAG = "DownloadDirectory";
    private RecyclerView downloadRecyclerView;
    private BatchDownloadAdapter adapter;
    private ImageView allSetectImage;
    private TextView allSelectText;
    private RelativeLayout btnAllSelect;
    private boolean isAllSelect = false;
    private View mRootView;
    private TextView btnDownload;
    private String resourceId;
    private String fromType;
    private TextView selectCount;
    private boolean allSelectEnable;
    private List<ColumnSecondDirectoryEntity> mDownloadList;
    private int mDownloadCount;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_download_directory, container, false);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initData();
    }

    private void initData() {
        Intent intent = getActivity().getIntent();
        String dataJson = intent.getStringExtra("bundle_dataJSON");
        resourceId = intent.getStringExtra("resourceId");
        fromType = intent.getStringExtra("from_type");
        List<ColumnDirectoryEntity> dataList = JSONObject.parseArray(dataJson, ColumnDirectoryEntity.class);
        int downloadCount = 0;
        for (ColumnDirectoryEntity directoryEntity : dataList){
            if (!directoryEntity.isEnable()){
                downloadCount++;
            }
        }
        allSelectEnable = !(downloadCount == dataList.size());
        //全选是否可选
        btnAllSelect.setEnabled(allSelectEnable);
        btnDownload.setEnabled(allSelectEnable);
        if(!allSelectEnable){
            allSetectImage.setImageResource(R.mipmap.download_alreadychecked);
        }
        adapter.addAllData(dataList);
        mDownloadList = new ArrayList<ColumnSecondDirectoryEntity>();
    }

    private void initView() {
        downloadRecyclerView = (RecyclerView) mRootView.findViewById(R.id.download_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setAutoMeasureEnabled(true);
        downloadRecyclerView.setLayoutManager(layoutManager);
        adapter = new BatchDownloadAdapter(getContext());
        adapter.setSelectListener(this);
        downloadRecyclerView.setAdapter(adapter);

        allSetectImage = (ImageView) mRootView.findViewById(R.id.all_select_image);
        allSelectText = (TextView) mRootView.findViewById(R.id.all_select_text);
        btnAllSelect = (RelativeLayout) mRootView.findViewById(R.id.btn_all_select);
        btnAllSelect.setOnClickListener(this);

        btnDownload = (TextView) mRootView.findViewById(R.id.btn_download);
        btnDownload.setOnClickListener(this);

        selectCount = (TextView) mRootView.findViewById(R.id.select_size);
        selectCount.setText(String.format(getString(R.string.the_selected_count), 0));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_all_select:
                clickAllSelect();
                break;
            case R.id.btn_download:
                clickDownload();
                break;
            default:
                break;
        }
    }

    private void clickDownload() {
        boolean download = false;
        mDownloadCount = 0;
        for (ColumnDirectoryEntity directoryEntity : adapter.getDate()){
            int childDownloadCount = 0;
            for (ColumnSecondDirectoryEntity secondDirectoryEntity : directoryEntity.getResource_list()){
                if(secondDirectoryEntity.isSelect() && secondDirectoryEntity.isEnable()){
                    download = true;
                    childDownloadCount++;
                    mDownloadList.add(secondDirectoryEntity);
                }else if(!secondDirectoryEntity.isEnable()){
                    childDownloadCount++;
                }
            }
            if(childDownloadCount == directoryEntity.getResource_list().size()){
                directoryEntity.setEnable(false);
                mDownloadCount++;
            }
        }

        if(download){
            if(!NetUtils.NETWORK_TYPE_WIFI.equals(NetUtils.getNetworkType(getContext()))){
                getDialog().setMessageVisibility(View.GONE);
                getDialog().getTitleView().setGravity(Gravity.START);
                getDialog().getTitleView().setPadding(Dp2Px2SpUtil.dp2px(getContext(), 22), 0, Dp2Px2SpUtil.dp2px(getContext(), 22), 0 );
                getDialog().getTitleView().setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                getDialog().setCancelable(false);
                getDialog().setHideCancelButton(false);
                getDialog().setTitle(getString(R.string.not_wifi_net_download_hint));
                getDialog().setConfirmText(getString(R.string.confirm_title));
                getDialog().setCancelText(getString(R.string.cancel_title));
                getDialog().showDialog(CustomDialog.NOT_WIFI_NET_DOWNLOAD_TAG);
            }else{
                downloadResource();
            }
        }else{
            toastCustom(getString(R.string.please_select_download));
        }
    }

    private void downloadResource(){
        //全选是否可选
        allSelectEnable = !(mDownloadCount == adapter.getDate().size());
        if(!allSelectEnable){
            allSetectImage.setImageResource(R.mipmap.download_alreadychecked);
            allSelectText.setText(getResources().getString(R.string.all_select_text));
        }
        selectCount.setText(String.format(getString(R.string.the_selected_count), 0));
        btnAllSelect.setEnabled(allSelectEnable);
        btnDownload.setEnabled(allSelectEnable);
        for (ColumnSecondDirectoryEntity secondDirectoryEntity : mDownloadList){
            secondDirectoryEntity.setEnable(false);
            DownloadManager.getInstance().addDownload(null, null, secondDirectoryEntity);
        }
        toastCustom(getString(R.string.add_download_list));
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClickConfirm(View view, int tag) {
        super.onClickConfirm(view, tag);
        if(CustomDialog.NOT_WIFI_NET_DOWNLOAD_TAG == tag){
            downloadResource();
        }
    }

    private void setAllSelectState(){
        if(!allSelectEnable){
            allSelectText.setText(getResources().getString(R.string.all_select_text));
            allSetectImage.setImageResource(R.mipmap.download_alreadychecked);
            return;
        }
        if(isAllSelect){
            allSelectText.setText(getResources().getString(R.string.cancel_text));
            allSetectImage.setImageResource(R.mipmap.download_checking);
        }else{
            allSelectText.setText(getResources().getString(R.string.all_select_text));
            allSetectImage.setImageResource(R.mipmap.download_tocheck);
        }
    }
    private void clickAllSelect() {
        isAllSelect = !isAllSelect;
        setAllSelectState();
        int count = 0;
        for (ColumnDirectoryEntity item : adapter.getDate()) {
            item.setSelect(isAllSelect);
            for (ColumnSecondDirectoryEntity childItem : item.getResource_list()) {
                if(childItem.isEnable()){
                    childItem.setSelect(isAllSelect);
                    count = isAllSelect ? count + 1 : 0;
                }
            }
        }
        adapter.notifyDataSetChanged();
        selectCount.setText(String.format(getString(R.string.the_selected_count), count));
    }

    @Override
    public void onSelect(int position) {
        int count = 0;
        int childCount = 0;
        for (ColumnDirectoryEntity item: adapter.getDate()) {
            if(item.isSelect() || !item.isEnable()){
                count++;
            }
            for (ColumnSecondDirectoryEntity childItem : item.getResource_list()){
                if(childItem.isSelect() && childItem.isEnable()){
                    childCount++;
                }
            }
        }
        isAllSelect = count == adapter.getDate().size();
        setAllSelectState();
        if(!allSelectEnable){
            childCount = 0;
        }
        selectCount.setText(String.format(getString(R.string.the_selected_count), childCount));
    }
}
