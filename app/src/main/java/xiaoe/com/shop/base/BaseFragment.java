package xiaoe.com.shop.base;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import xiaoe.com.common.app.Global;
import xiaoe.com.network.network_interface.INetworkResponse;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.shop.R;


/**
 * Created by Administrator on 2017/7/17.
 * <br>des：fragment 基类
 */

public class BaseFragment extends Fragment implements INetworkResponse {
    private static PopupWindow popupWindow;
    private TextView mToastText;
    private boolean isFragmentDestroy = false;
//    private Handler mHandler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            if(popupWindow != null){
//                popupWindow.dismiss();
//            }
//        }
//    };

    private Handler mHandler = new BfHandler(this);

    static class BfHandler extends Handler {

        WeakReference<BaseFragment> wrf;

        BfHandler (BaseFragment baseFragment) {
            this.wrf = new WeakReference<>(baseFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (popupWindow != null) {
                popupWindow.dismiss();
            }
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        popupWindow = new PopupWindow(getContext());
        popupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        View pView = LayoutInflater.from(getContext()).inflate(R.layout.layout_custom_toast, null,false);
        mToastText = (TextView) pView.findViewById(R.id.id_toast_text);
        popupWindow.setContentView(pView);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);
        isFragmentDestroy = false;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onResponse(final IRequest iRequest, final boolean success, final Object entity) {
        if(!success && entity instanceof String){
            if(((String)entity).equals("user login time out")){
                return;
            }
        }
        if(!isFragmentDestroy){
            Global.g().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onMainThreadResponse(iRequest,success,entity);
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isFragmentDestroy = true;
        if(popupWindow!= null){
            popupWindow.dismiss();
            popupWindow = null;
        }
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {

    }
    public void ToastCustom(String msg){
        mHandler.removeMessages(0);
        mToastText.setText(msg);
        popupWindow.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.CENTER, 0, 0);
        mHandler.sendEmptyMessageDelayed(0,1500);
    }
    /**
     * 在弹出对话框时，显示popupWindow，则popupWindow会在对话框下面（被对话框盖住）
     * 是方法是让popupWindow显示在对话上面
     * view 是对话框中的view
     */
    public void ToastCustomDialog(View view,String msg){
        mHandler.removeMessages(0);
        mToastText.setText(msg);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        mHandler.sendEmptyMessageDelayed(0,1500);
    }
}
