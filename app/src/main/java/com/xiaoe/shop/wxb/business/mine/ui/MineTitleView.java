package com.xiaoe.shop.wxb.business.mine.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoe.shop.wxb.R;

public class MineTitleView extends RelativeLayout {

    private static final String TAG = "MineTitleView";

    private Context mContext;

    private ImageView titleMsg;
    private ImageView titleSetting;
    private TextView tvUnreadMsg;

    public MineTitleView(Context context) {
        super(context);
        init(context);
    }

    public MineTitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MineTitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public MineTitleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        View view = View.inflate(context, R.layout.mine_title, this);
        titleMsg = (ImageView) view.findViewById(R.id.mine_title_msg);
        titleSetting = (ImageView) view.findViewById(R.id.mine_title_setting);
        tvUnreadMsg = (TextView) view.findViewById(R.id.tv_unread_msg);
    }

    public void setMsgClickListener(OnClickListener listener) {
        titleMsg.setOnClickListener(listener);
    }

    public void setSettingListener(OnClickListener listener) {
        titleSetting.setOnClickListener(listener);
    }

    public void setUnreadMsgVisible(boolean visible) {
        if (visible) {
            tvUnreadMsg.setVisibility(VISIBLE);
        } else {
            tvUnreadMsg.setVisibility(INVISIBLE);
        }
    }

    public void setUnreadMsgCount(int unreadMsgCount) {
        if (unreadMsgCount <= 0) {
            tvUnreadMsg.setText("");
        } else if (unreadMsgCount > 99) {
            tvUnreadMsg.setText(99 + "+");
        } else {
            tvUnreadMsg.setText(String.valueOf(unreadMsgCount));
        }
    }
}
