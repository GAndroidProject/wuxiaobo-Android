package com.xiaoe.common.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

public class MeasureUtil {

    /**
     * 根据 listView 的 item 测量 listView 的高度
     * @param listView 被测量的 listView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {

        // 获取 ListView 对应的 Adapter
        ListAdapter adapter = listView.getAdapter();
        if (adapter == null) {
            return;
        }
        int totalHeight = 0;
        for(int i = 0, len = adapter.getCount(); i < len; i++) {
            // 拿到每个 View
            View listItem = adapter.getView(i, null, listView);
            // 计算宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        params.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
