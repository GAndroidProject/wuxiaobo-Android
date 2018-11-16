package com.xiaoe.shop.wxb.business.audio.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.xiaoe.shop.wxb.R;


public class AudioDetailsLayout extends LinearLayout {
    private static final String TAG = "TestTextView";

    private ScrollView scrollView;

    public AudioDetailsLayout(Context context) {
        this(context,null);
    }

    public AudioDetailsLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }



    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        scrollView = (ScrollView) findViewById(R.id.ScrollView);
    }

    public boolean canChildScrollUp() {
        if(scrollView != null){
            if(scrollView.getScrollY() > 0){
                return true;
            }
        }
        return false;
    }
}
