package com.xiaoe.shop.wxb.interfaces;

import android.view.View;

import com.xiaoe.shop.wxb.widget.SlidingButtonView;

public interface IonSlidingButtonListener {
    void onMenuIsOpen(View view);
    void onDownOrMove(SlidingButtonView slidingButtonView);
}
