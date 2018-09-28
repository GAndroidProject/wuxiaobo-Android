package xiaoe.com.shop.base;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;


import java.lang.ref.WeakReference;

import xiaoe.com.common.app.Global;
import xiaoe.com.network.network_interface.INetworkResponse;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.shop.R;

/**
 * Created by Administrator on 2017/7/17.
 */

public class XiaoeActivity extends AppCompatActivity implements INetworkResponse{
    private static final String TAG = "XiaoeActivity";
    private static final int DISMISS_POPUP_WINDOW = 1;
    private static final int DISMISS_TOAST = 2;
    private static PopupWindow popupWindow;
    private static Toast toast;
    private TextView mToastText;
//    private Handler mHandler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if(msg.what == DISMISS_POPUP_WINDOW){
//                if(popupWindow != null){
//                    popupWindow.dismiss();
//                }
//            }else if(msg.what == DISMISS_TOAST){
//                if(toast != null){
//                    toast.cancel();
//                }
//            }
//
//        }
//    };
    private Handler mHandler = new XeHandler(this);
    private boolean mHasFocus = false;
    private boolean isActivityDestroy = false;

    static class XeHandler extends Handler {

        WeakReference<XiaoeActivity> wrf;

        XeHandler(XiaoeActivity activity) {
            wrf = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == DISMISS_POPUP_WINDOW){
                if(popupWindow != null){
                    popupWindow.dismiss();
                }
            }else if(msg.what == DISMISS_TOAST){
                if(toast != null){
                    toast.cancel();
                }
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        popupWindow = new PopupWindow(this);
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        View view = LayoutInflater.from(this).inflate(R.layout.layout_custom_toast, null,false);
        mToastText = (TextView) view.findViewById(R.id.id_toast_text);
        popupWindow.setContentView(view);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);
        isActivityDestroy = false;
    }

    public void Toast(String msg){
        toast = Toast.makeText(this,msg,Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onResponse(final IRequest iRequest, final boolean success, final Object entity) {
        if(!success && entity instanceof String){
            if(((String)entity).equals("user login time out")){
            }
        }
        if(!isActivityDestroy){
            Global.g().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onMainThreadResponse(iRequest,success,entity);
                }
            });
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mHasFocus = hasFocus;
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {

    }
    public void ToastCustom(String msg){
        if(mHasFocus){
            mHandler.removeMessages(DISMISS_POPUP_WINDOW);
            mToastText.setText(msg);
            popupWindow.showAtLocation(getWindow().getDecorView(), Gravity.CENTER, 0, 0);
            mHandler.sendEmptyMessageDelayed(DISMISS_POPUP_WINDOW,1500);
        }
    }

    /**
     * 在弹出对话框时，显示popupWindow，则popupWindow会在对话框下面（被对话框盖住）
     * 是方法是让popupWindow显示在对话上面
     * view 是对话框中的view
     */
    public void ToastCustomDialog(View view,String msg){
        mHandler.removeMessages(DISMISS_POPUP_WINDOW);
        mToastText.setText(msg);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        mHandler.sendEmptyMessageDelayed(DISMISS_POPUP_WINDOW,1500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isActivityDestroy = true;
        if(toast != null){
            toast.cancel();
        }
        if(popupWindow != null){
            popupWindow.dismiss();
            popupWindow = null;
        }
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }
}
