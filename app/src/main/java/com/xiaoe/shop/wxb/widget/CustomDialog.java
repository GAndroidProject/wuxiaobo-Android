package com.xiaoe.shop.wxb.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.xiaoe.common.app.Global;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.interfaces.OnCustomDialogListener;


/**
 * Created by Administrator on 2017/7/18.
 */

public class CustomDialog implements DialogInterface.OnDismissListener, DialogInterface.OnKeyListener {
    private static final String TAG = "HintDialog";

    public static final int PAGER_LOAD_TAG = 7000;//页面加载状态

    public static final int NOT_WIFI_PLAY_TAG = 7010;//非WiFi 环境播放

    public static final int REQUEST_PERMISSIONS_TAG = 7011;

    private Context mContext;
    private View rootView;
    private final TextView btnCancel;
    private TextView btnConfirm;
    private AlertDialog dialog;
    private final TextView message;
//    private OnCancelListener cancelListener;
//    private OnConfirmListener confirmListener;
    private final View verticalLine;
    private boolean mCancelable = true;
    //    private int mType = -1;
    private final TextView title;
    private final TextView messageLoad;
    private boolean mCanceledOnTouchOutside = true;
    private final RelativeLayout hintTypeLayout;
    private final LinearLayout loadTypeLayout;
    private boolean hideCancel = false;
    private int dialogTag = -1;
    private OnCustomDialogListener dialogListener;

    public CustomDialog(Context context) {
        this.mContext = context;
        rootView = LayoutInflater.from(context).inflate(R.layout.layout_custom_dialog, null, false);
        messageLoad = (TextView) rootView.findViewById(R.id.id_message_load);
        messageLoad.setVisibility(View.GONE);
        btnCancel = (TextView) rootView.findViewById(R.id.btn_cancel);
        btnConfirm = (TextView) rootView.findViewById(R.id.btn_confirm);
        message = (TextView) rootView.findViewById(R.id.id_message);
        title = (TextView) rootView.findViewById(R.id.id_title);
        verticalLine = rootView.findViewById(R.id.vertical_line);

        hintTypeLayout = (RelativeLayout) rootView.findViewById(R.id.hint_type);
        hintTypeLayout.setVisibility(View.GONE);
        loadTypeLayout = (LinearLayout) rootView.findViewById(R.id.load_type);
        loadTypeLayout.setVisibility(View.VISIBLE);

        SimpleDraweeView simpleDraweeView = (SimpleDraweeView) rootView.findViewById(R.id.bold_gif_preloader);
        Uri uri = Uri.parse("res:///" + R.drawable.bold_gif_preloader);
        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setOldController(simpleDraweeView.getController())
                // 设置加载图片完成后是否直接进行播放
                .setAutoPlayAnimations(true)
                .build();
        simpleDraweeView.setController(draweeController);
    }

    public void setOnCustomDialogListener(OnCustomDialogListener listener){
        dialogListener = listener;
    }


    public void setCancelText(String text) {
        btnCancel.setText(text);
    }

    public void setCancelTextColor(int color) {
        btnCancel.setTextColor(color);
    }

    public void setConfirmText(String text) {
        btnConfirm.setText(text);
    }

    public void setConfirmTextColor(int color) {
        btnConfirm.setTextColor(color);
    }

    public boolean isShowing(){
        if(dialog != null){
            return dialog.isShowing();
        }
        return false;
    }
    public void setLoadMessage(String text) {
        messageLoad.setText(text);
        messageLoad.setVisibility(View.VISIBLE);
    }

    public void setHintMessage(String text) {
        message.setText(text);
    }

    public void setHintMessage(String text, String textSubContent) {
        message.setText(String.format(text, textSubContent));
    }

    public TextView getMessage(){
        return message;
    }

    public TextView getTitleView() {
        return title;
    }

    public void setMessageTextColor(int color) {
        message.setTextColor(color);
    }

    public void setTitle(String text) {
        title.setText(text);
    }

    public void setTitleVisibility(int visibility){
        title.setVisibility(visibility);
    }

    public void setMessageVisibility(int visibility){
        message.setVisibility(visibility);
    }

    public void setHideCancelButton(boolean hide){
        hideCancel = hide;
    }

    public void showDialog(final int tag) {
        dialogTag = tag;
        loadTypeLayout.setVisibility(View.GONE);
        hintTypeLayout.setVisibility(View.VISIBLE);
        if (hideCancel) {
            btnCancel.setVisibility(View.GONE);
            verticalLine.setVisibility(View.GONE);
            btnConfirm.setBackgroundResource(hideCancel ?
                    R.drawable.btn_bottom_radio_bg : R.drawable.btn_right_bottom_radio_bg);
        }
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.dismiss();
                    dialog = null;
                }
                if (dialogListener != null) {
                    dialogListener.onClickCancel(v, tag);
                }
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                dialog = null;
                if (dialogListener != null) {
                    dialogListener.onClickConfirm(v, tag);
                }
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        dialog = builder.create();
        dialog.setOnKeyListener(this);
        dialog.setOnDismissListener(this);
        dialog.setCanceledOnTouchOutside(mCanceledOnTouchOutside);
        dialog.setCancelable(mCancelable);

        dialog.show();
        Point point = Global.g().getDisplayPixel();
        dialog.getWindow().setLayout((int) (point.x * 0.8),WindowManager.LayoutParams.WRAP_CONTENT);
        ViewGroup viewGroup = (ViewGroup) rootView.getParent();
        if (viewGroup != null) {
            viewGroup.removeAllViewsInLayout();
        }
        if(dialog.getWindow() != null){
            dialog.getWindow().setDimAmount(0.5f);
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_dialog);
            dialog.getWindow().setLayout(Dp2Px2SpUtil.dp2px(mContext,250),LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        dialog.setContentView(rootView);
    }

    
    public void setCancelable(boolean cancelable) {
        mCancelable = cancelable;
    }

    public void setCanceledOnTouchOutside(boolean coto){
        mCanceledOnTouchOutside = coto;
    }

    public void showLoadDialog(boolean showMessage) {
        loadTypeLayout.setVisibility(View.VISIBLE);
        hintTypeLayout.setVisibility(View.GONE);
        Point point = Global.g().getDisplayPixel();
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        dialog = builder.create();
        dialog.setOnKeyListener(this);
        dialog.setCanceledOnTouchOutside(mCanceledOnTouchOutside);
        dialog.setCancelable(mCancelable);
        dialog.setOnDismissListener(this);


        dialog.show();
        if(dialog.getWindow() != null){
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_dialog);
            dialog.getWindow().setDimAmount(0f);
        }
        int w = 0;
        int h = 0;
        if(showMessage){
            w = point.x * 55 / 72;
            h = Dp2Px2SpUtil.dp2px(mContext, 100);
        }else{
            w = Dp2Px2SpUtil.dp2px(mContext, 100);
            h = Dp2Px2SpUtil.dp2px(mContext, 100);
        }
        dialog.getWindow().setLayout(w,h);
        ViewGroup viewGroup = (ViewGroup) rootView.getParent();
        if (viewGroup != null) {
            viewGroup.removeAllViewsInLayout();
        }
        dialog.setContentView(rootView);
    }

    public void dismissDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public int getDialogTag() {
        return dialogTag;
    }

    public void setDialogTag(int dialogTag) {
        this.dialogTag = dialogTag;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if(dialogListener != null){
            dialogListener.onDialogDismiss(dialog, dialogTag, false);
        }
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && (event.getAction() == KeyEvent.ACTION_UP) && dialogListener != null){
            dialogListener.onDialogDismiss(dialog, dialogTag, true);
        }
        return false;
    }
}
