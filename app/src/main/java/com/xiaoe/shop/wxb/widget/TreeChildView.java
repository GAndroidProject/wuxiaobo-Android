package com.xiaoe.shop.wxb.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoe.shop.wxb.R;
import com.xiaoe.common.utils.Dp2Px2SpUtil;

public class TreeChildView extends RelativeLayout {
    private String TAG = "TreeChildView";
    private final TextView title;

    public TreeChildView(Context context) {
        super(context);
        View rootView = View.inflate(context, R.layout.item_recycler_tree_child, this);
        RelativeLayout container = (RelativeLayout) rootView.findViewById(R.id.temp_container);
        LayoutParams layoutParams = (LayoutParams) container.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        int margin = Dp2Px2SpUtil.dp2px(context, 16);
        layoutParams.setMargins(0, margin, 0, margin);
        container.setLayoutParams(layoutParams);

        title = (TextView) rootView.findViewById(R.id.text);
    }
    public void setTitle(String strTitle){
        title.setText(strTitle);
    }
}
