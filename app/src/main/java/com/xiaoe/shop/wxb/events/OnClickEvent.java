package com.xiaoe.shop.wxb.events;

import android.view.View;

import com.xiaoe.common.app.XiaoeApplication;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.utils.ToastUtils;

/**
 * @author flynnWang
 * @date 2018/11/23
 * <p>
 * 描述：防止连续点击两次（注：适用于点击太快不触发事件的情况，比如：网络请求等耗时操作时使用）
 */
public abstract class OnClickEvent implements View.OnClickListener {

    public static final long DEFAULT_SECOND = 500;
    private static long lastTime;

    public abstract void singleClick(View v);

    private long delay;

    public OnClickEvent(long delay) {
        this.delay = delay;
    }

    public OnClickEvent() {
        this.delay = DEFAULT_SECOND;
    }

    @Override
    public void onClick(View view) {
        if (onMoreClick()) {
            ToastUtils.show(XiaoeApplication.applicationContext, R.string.not_double_click);
            return;
        }
        singleClick(view);
    }

    public boolean onMoreClick() {
        boolean flag = false;
        long time = System.currentTimeMillis() - lastTime;
        if (time < delay) {
            flag = true;
        }
        lastTime = System.currentTimeMillis();
        return flag;
    }

    public static boolean onMoreAction(long delay) {
        boolean flag = false;
        long time = System.currentTimeMillis() - lastTime;
        if (time < delay) {
            flag = true;
        }
        lastTime = System.currentTimeMillis();
        return flag;
    }

}
