package com.xiaoe.shop.wxb.business.setting.presenter;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;

import com.xiaoe.shop.wxb.R;

public class SettingTimeCount extends CountDownTimer {

    private Context mContext;
    private View container;
    private TextView tv;

    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public SettingTimeCount(Context context, long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        this.mContext = context;
    }

    public SettingTimeCount(Context context, long millisInFuture, long countDownInterval, View viewWrap) {
        super(millisInFuture, countDownInterval);
        this.container = viewWrap;
        this.mContext = context;
    }

    @Override
    public void onTick(long millisUntilFinished) {
        tv = (TextView) container.findViewById(R.id.phone_num_desc);
        tv.setClickable(false);
        int second = (int) (millisUntilFinished / 1000);
        tv.setText(String.format(mContext.getResources().getString(R.string.setting_phone_num_desc), second));
    }

    @Override
    public void onFinish() {
        tv.setText("重新获取验证码");
        tv.setClickable(true);
    }
}
