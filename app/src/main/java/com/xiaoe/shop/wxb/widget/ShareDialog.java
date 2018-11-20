package com.xiaoe.shop.wxb.widget;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.XiaoeActivity;

public class ShareDialog implements View.OnClickListener {
    private static final String TAG = "ShareDialog";
    public static final int SHARE_DIALOG_TAG = 8100;
    private final View rootView;
    private final AlertDialog dialog;
    private final Context mContext;
    private String mShareTitle;
    private String mImgUrl;
    private String mShareUrl;
    private String mDesc;
    private final XiaoeActivity xiaoeActivity;


    public ShareDialog(Context context, XiaoeActivity activity) {
        xiaoeActivity = activity;
        mContext = context;
        rootView = LayoutInflater.from(context).inflate(R.layout.layout_share_dialog, null, false);
        TextView btnCancelShare = (TextView) rootView.findViewById(R.id.btn_cancel_share);
        btnCancelShare.setOnClickListener(this);
        LinearLayout btnShareFriendEncircle = (LinearLayout) rootView.findViewById(R.id.btn_share_friend_encircle);
        btnShareFriendEncircle.setOnClickListener(this);
        LinearLayout btnShareWechatFriend = (LinearLayout) rootView.findViewById(R.id.btn_share_wechat_friend);
        btnShareWechatFriend.setOnClickListener(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        dialog = builder.create();
    }

    public void showSharePanel(String shareTitle, String imgUrl, String shareUrl, String desc) {
        mShareTitle = shareTitle;
        mImgUrl = imgUrl;
        mShareUrl = shareUrl;
        mDesc = desc;
        dialog.show();
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.getDecorView().setPadding(0, 0, 0, 0);
        window.setGravity(Gravity.BOTTOM);
        dialog.setContentView(rootView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_cancel_share:
                dialog.dismiss();
                break;
            case R.id.btn_share_friend_encircle:
                shareFriendEncircle();
                break;
            case R.id.btn_share_wechat_friend:
                shareWechatFriend();
                break;
            default:
                break;
        }
    }

    private void shareWechatFriend() {
        UMImage thumb = new UMImage(mContext, mImgUrl);
        UMWeb web = new UMWeb(mShareUrl);
        web.setTitle(mShareTitle);//标题
        web.setThumb(thumb);  //缩略图
        web.setDescription(mDesc);//描述
        new ShareAction(xiaoeActivity)
                .withMedia(web)
                .setPlatform(SHARE_MEDIA.WEIXIN)
                .setCallback(xiaoeActivity)
                .share();
        dialog.dismiss();
        xiaoeActivity.getDialog().setDialogTag(SHARE_DIALOG_TAG);
        xiaoeActivity.getDialog().showLoadDialog(false);
    }

    private void shareFriendEncircle() {
        UMImage thumb = new UMImage(mContext, mImgUrl);
        UMWeb web = new UMWeb(mShareUrl);
        web.setTitle(mShareTitle);//标题
        web.setThumb(thumb);  //缩略图
        web.setDescription(mDesc);//描述
        new ShareAction(xiaoeActivity)
                .withMedia(web)
                .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                .setCallback(xiaoeActivity)
                .share();
        dialog.dismiss();
        xiaoeActivity.getDialog().setDialogTag(SHARE_DIALOG_TAG);
        xiaoeActivity.getDialog().showLoadDialog(false);
    }
}
