package xiaoe.com.common.interfaces;

import android.view.View;

import xiaoe.com.common.entitys.BoughtListItem;

public interface OnItemClickWithBoughtItemListener {

    /**
     * item 点击事件
     * @param view 点击的 view
     * @param boughtListItem 点击的 view 对象的知识商品组件对象
     */
    public void onBoughtListItemClick(View view, BoughtListItem boughtListItem);
}
