package com.xiaoe.shop.wxb.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

import com.xiaoe.shop.wxb.adapter.common.VersionLogAdapter;

public class LinearLayoutForListView extends LinearLayout {

    private static final String TAG = "LinearLayoutForListView";

    private VersionLogAdapter optionsAdapter;

    public LinearLayoutForListView(Context context) {
        super(context);
    }

    public LinearLayoutForListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAdapter(VersionLogAdapter optionsAdapter) {
        this.optionsAdapter = optionsAdapter;
    }

    /**
     * 绑定布局（版本更新说明）
     */
    public void bindLinearLayoutV() {
        this.removeAllViews();
        int count = optionsAdapter.getCount();
        for (int i = 0; i < count; i++) {
            addView(optionsAdapter.getView(i, null, null));
        }
        Log.v(TAG, "VersionLogAdapter countTAG: " + count);
    }

}
