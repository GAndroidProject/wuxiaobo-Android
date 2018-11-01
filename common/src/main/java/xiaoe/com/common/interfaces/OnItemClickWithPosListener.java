package xiaoe.com.common.interfaces;

import android.view.View;

import xiaoe.com.common.entitys.SettingItemInfo;

public interface OnItemClickWithPosListener {

    /**
     * item 点击事件
     * @param view 点击的 view
     * @param position 点击的位置
     */
    public void onItemClick(View view, int position);
}
