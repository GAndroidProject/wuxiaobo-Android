package xiaoe.com.common.interfaces;

import android.view.View;

import xiaoe.com.common.entitys.GraphicNavItem;


public interface OnItemClickWithNavItemListener {

    /**
     * item 点击事件
     * @param view 点击的 view
     * @param graphicNavItem 点击的 view 对象的图文导航组件对象
     */
    public void onNavItemClick(View view, GraphicNavItem graphicNavItem);
}
