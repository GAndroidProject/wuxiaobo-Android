package com.xiaoe.shop.wxb.adapter.download;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoe.common.entitys.ColumnSecondDirectoryEntity;
import com.xiaoe.common.utils.DateFormat;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseViewHolder;
import com.xiaoe.shop.wxb.interfaces.OnSelectListener;

public class BatchChildDownloadHolder extends BaseViewHolder implements View.OnClickListener {
    private static final String TAG = "BatchDownloadHolder";
    private final View mItemView;
    private RelativeLayout btnSelect;
    private TextView title;
    private View line;
    private TextView time;
    private ImageView selectIcon;
    private ColumnSecondDirectoryEntity mData = null;
    private int mParentPosition;
    private OnSelectListener mSelectListener;

    public BatchChildDownloadHolder(View itemView, int parentPosition, OnSelectListener selectListener) {
        super(itemView);
        mItemView = itemView;
        mParentPosition = parentPosition;
        mSelectListener = selectListener;
        initView();
    }

    private void initView() {
        line = mItemView.findViewById(R.id.line);
        btnSelect = (RelativeLayout) mItemView.findViewById(R.id.btn_select);
        btnSelect.setOnClickListener(this);
        selectIcon = (ImageView) mItemView.findViewById(R.id.select_icon);
        title = (TextView) mItemView.findViewById(R.id.text);
        time = (TextView) mItemView.findViewById(R.id.item_time);
    }

    public void bindView(ColumnSecondDirectoryEntity itemData, int position){
        mData = itemData;
        if(itemData.isSelect() && itemData.isEnable()){
            selectIcon.setImageResource(R.mipmap.download_checking);
        }else{
            if(!itemData.isEnable()){
                selectIcon.setImageResource(R.mipmap.download_alreadychecked);
            }else{
                selectIcon.setImageResource(R.mipmap.download_tocheck);
            }
        }

        title.setText(mData.getTitle());
        if(mData.getResource_type() == 2){
            time.setText(DateFormat.longToString(mData.getAudio_length() * 1000));
        }else{
            time.setText(DateFormat.longToString(mData.getVideo_length() * 1000));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_select:
                clickSelect();
                break;
            default:
                break;
        }
    }
    private void setSelect(boolean select){
        if(select){
            selectIcon.setImageResource(R.mipmap.download_checking);
        }else{
            selectIcon.setImageResource(R.mipmap.download_tocheck);
        }
    }

    private void clickSelect() {
        if(!mData.isEnable()){
            return;
        }
        mData.setSelect(!mData.isSelect());
        setSelect(mData.isSelect());
        if(mSelectListener != null){
            mSelectListener.onSelect(mParentPosition);
        }

    }
}
