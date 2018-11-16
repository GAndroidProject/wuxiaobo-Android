package com.xiaoe.shop.wxb.widget;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.shop.wxb.R;

// 通过弹框
public class TouristDialog extends View {

    private Context mContext;

    private ImageView dialogClose;
    private ImageView dialogIcon;
    private Button dialogConfirm;

    private AlertDialog dialog;

    View view;

    public TouristDialog(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        view = LayoutInflater.from(context).inflate(R.layout.tourists_remind_dialog,null, false);
        dialogClose = (ImageView) view.findViewById(R.id.btn_dialog_close);
        dialogIcon = (ImageView) view.findViewById(R.id.dialog_icon);
        dialogConfirm = (Button) view.findViewById(R.id.btn_dialog_confirm);
    }

    // 设置对话框 icon
    public void setDialogIcon(int iconSrc) {
        dialogIcon.setImageDrawable(mContext.getResources().getDrawable(iconSrc));
    }

    public void setDialogCloseClickListener(OnClickListener listener) {
        dialogClose.setOnClickListener(listener);
    }

    public void setDialogConfirmClickListener(OnClickListener listener) {
        dialogConfirm.setOnClickListener(listener);
    }

    public void showDialog() {
        if (dialog == null){
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            dialog = builder.create();
        }
        if (!dialog.isShowing()) {
            dialog.show();
        }
        dialog.getWindow().setLayout(Dp2Px2SpUtil.dp2px(mContext,240),
                Dp2Px2SpUtil.dp2px(mContext,230));
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_layout_dialog2);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(view);
    }

    public void dismissDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
