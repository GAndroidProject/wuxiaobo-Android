package com.xiaoe.shop.wxb.utils;

import android.content.Context;
import android.view.View;

import com.xiaoe.shop.wxb.common.JumpDetail;
import com.xiaoe.shop.wxb.events.OnClickEvent;
import com.xiaoe.shop.wxb.widget.TouristDialog;

public class LoginDialogUtils {

    public static void showTouristDialog(Context context) {
        final TouristDialog touristDialog = new TouristDialog(context);
        touristDialog.setDialogCloseClickListener(new OnClickEvent(OnClickEvent.DEFAULT_SECOND) {
            @Override
            public void singleClick(View v) {
                touristDialog.dismissDialog();
            }
        });
        touristDialog.setDialogConfirmClickListener(new OnClickEvent(OnClickEvent.DEFAULT_SECOND) {
            @Override
            public void singleClick(View v) {
                touristDialog.dismissDialog();
                JumpDetail.jumpLogin(context, true);
            }
        });
        touristDialog.showDialog();
    }

}
