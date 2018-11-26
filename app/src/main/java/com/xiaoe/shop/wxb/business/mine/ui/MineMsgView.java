package com.xiaoe.shop.wxb.business.mine.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.utils.SetImageUriUtil;

public class MineMsgView extends RelativeLayout {

    private static final String TAG = "MineMsgView";

    private Context mContext;
    private TextView title_nickname;
    private SimpleDraweeView title_avatar;
    private Button title_buy_vip;

    public MineMsgView(Context context) {
        super(context);
        init(context);
    }

    public MineMsgView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MineMsgView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public MineMsgView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        View view = View.inflate(context, R.layout.mine_msg, this);
        title_nickname = (TextView) view.findViewById(R.id.title_nickname);
        title_avatar = (SimpleDraweeView) view.findViewById(R.id.title_avatar);
        title_buy_vip = (Button) view.findViewById(R.id.title_buy_vip);
    }

    public void setAvatar(String imgUrl) {
        if (title_avatar != null) {
            SetImageUriUtil.setImgURI(title_avatar, imgUrl, Dp2Px2SpUtil.dp2px(mContext, 72), Dp2Px2SpUtil.dp2px(mContext, 72));
        }
    }

    public void setAvatarClickListener(OnClickListener listener) {
        title_avatar.setOnClickListener(listener);
    }

    public void setNickName(String nickName) {
        if (title_nickname != null) {
            title_nickname.setText(nickName);
        }
    }

    public void setBuyVipVisibility(int visibility) {
        title_buy_vip.setVisibility(visibility);
    }

    public void setBuyVipClickListener(OnClickListener listener) {
        title_buy_vip.setOnClickListener(listener);
    }

    public void setNicknameOnClickListener(OnClickListener listener) {
        title_nickname.setOnClickListener(listener);
    }

    public void setBuyVipTag() {
        title_buy_vip.setText("超级会员");
        title_buy_vip.setTextColor(mContext.getResources().getColor(R.color.white));
        title_buy_vip.setPadding(Dp2Px2SpUtil.dp2px(mContext, 16), Dp2Px2SpUtil.dp2px(mContext, 4), Dp2Px2SpUtil.dp2px(mContext, 16), Dp2Px2SpUtil.dp2px(mContext, 4));
        title_buy_vip.setBackground(mContext.getResources().getDrawable(R.drawable.recent_update_btn_pressed));
    }

    public void setBuyVipCommon() {
        title_buy_vip.setText(mContext.getString(R.string.super_vip));
        title_buy_vip.setTextColor(mContext.getResources().getColor(R.color.main_title_color));
        title_buy_vip.setPadding(Dp2Px2SpUtil.dp2px(mContext, 32), Dp2Px2SpUtil.dp2px(mContext, 4), Dp2Px2SpUtil.dp2px(mContext, 32), Dp2Px2SpUtil.dp2px(mContext, 4));
        title_buy_vip.setBackground(mContext.getResources().getDrawable(R.drawable.super_vip_bg));
    }
}
