package xiaoe.com.shop.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import xiaoe.com.common.app.Global;
import xiaoe.com.common.utils.Dp2Px2SpUtil;
import xiaoe.com.shop.R;
import xiaoe.com.shop.interfaces.OnCancelListener;
import xiaoe.com.shop.interfaces.OnConfirmListener;


/**
 * Created by Administrator on 2017/7/18.
 */

public class CustomDialog {
    private static final String TAG = "HintDialog";

    private Context mContext;
    private View rootView;
    private final TextView btnCancel;
    private TextView btnConfirm;
    private AlertDialog dialog;
    private final TextView message;
    private OnCancelListener cancelListener;
    private OnConfirmListener confirmListener;
    private final View verticalLine;
    private boolean mCancelable = true;
    //    private int mType = -1;
    private final TextView title;
    private final TextView messageLoad;
    private DialogInterface.OnDismissListener mDismissListener;
    private boolean mCanceledOnTouchOutside = true;
    private final RelativeLayout hintTypeLayout;
    private final LinearLayout loadTypeLayout;
    private boolean hideCancel = false;

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
    }

    public void setCancelListener(OnCancelListener cancelListener) {
        this.cancelListener = cancelListener;
    }

    public void setConfirmListener(OnConfirmListener confirmListener) {
        this.confirmListener = confirmListener;
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

    public TextView getMessage(){
        return message;
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
        loadTypeLayout.setVisibility(View.GONE);
        hintTypeLayout.setVisibility(View.VISIBLE);
        if (cancelListener == null && hideCancel) {
            btnCancel.setVisibility(View.GONE);
            verticalLine.setVisibility(View.GONE);
            btnConfirm.setBackgroundResource(cancelListener == null && hideCancel ?
                    R.drawable.btn_bottom_radio_bg : R.drawable.btn_right_bottom_radio_bg);
        }
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialog != null) {
                    dialog.dismiss();
                    dialog = null;
                }
                if (cancelListener != null) {
                    cancelListener.onClickCancel(v, tag);
                }
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                dialog = null;
                if (confirmListener != null) {
                    confirmListener.onClickConfirm(v, tag);
                }
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        dialog = builder.create();
        if(mDismissListener != null){
            dialog.setOnDismissListener(mDismissListener);
        }
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
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_dialog);
            dialog.getWindow().setLayout(Dp2Px2SpUtil.dp2px(mContext,250),LinearLayout.LayoutParams.WRAP_CONTENT);
        }
        dialog.setContentView(rootView);
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener dismissListener){
        mDismissListener = dismissListener;
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
        dialog.setCanceledOnTouchOutside(mCanceledOnTouchOutside);
        dialog.setCancelable(false);


        dialog.show();
        if(dialog.getWindow() != null){
            dialog.getWindow().setBackgroundDrawableResource(R.drawable.bg_dialog);
        }
        int w = 0;
        int h = 0;
        if(showMessage){
            w = point.x * 55 / 72;
            h = point.y * 15 / 128;
        }else{
            w = point.x * 5 / 12;
            h = point.y * 15 / 128;
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
}
