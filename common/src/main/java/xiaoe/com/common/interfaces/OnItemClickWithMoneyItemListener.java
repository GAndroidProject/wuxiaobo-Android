package xiaoe.com.common.interfaces;

import android.view.View;

import xiaoe.com.common.entitys.MineMoneyItemInfo;

public interface OnItemClickWithMoneyItemListener {

    /**
     * item 点击事件
     * @param view 点击的 view
     * @param mineMoneyItemInfo 点击的 view 对象的知识商品组件对象
     */
    public void onMineMoneyItemInfoClickListener(View view, MineMoneyItemInfo mineMoneyItemInfo);
}
