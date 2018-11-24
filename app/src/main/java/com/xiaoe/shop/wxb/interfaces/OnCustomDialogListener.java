package com.xiaoe.shop.wxb.interfaces;

import android.content.DialogInterface;
import android.view.View;

/**
 * Created by Administrator on 2017/6/12.
 */

public interface OnCustomDialogListener {
    void onClickCancel(View view, int tag);
    void onClickConfirm(View view, int tag);
    void onDialogDismiss(DialogInterface dialog, int tag, boolean backKey);
}
