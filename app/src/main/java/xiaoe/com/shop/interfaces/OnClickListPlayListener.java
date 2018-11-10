package xiaoe.com.shop.interfaces;

import android.view.View;

import xiaoe.com.common.entitys.ColumnSecondDirectoryEntity;

public interface OnClickListPlayListener {
    void onPlayPosition(View view,int parentPosition, int position, boolean jumpDetail);
    void onJumpDetail(ColumnSecondDirectoryEntity itemData, int parentPosition, int position);
}
