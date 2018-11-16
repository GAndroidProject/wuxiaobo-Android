package com.xiaoe.shop.wxb.interfaces;

import android.view.View;

import com.xiaoe.common.entitys.ColumnSecondDirectoryEntity;

public interface OnClickListPlayListener {
    void onPlayPosition(View view,int parentPosition, int position, boolean jumpDetail);
    void onJumpDetail(ColumnSecondDirectoryEntity itemData, int parentPosition, int position);
}
