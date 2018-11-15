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

import com.alibaba.fastjson.JSONObject;

import java.lang.ref.WeakReference;
import java.util.List;

import xiaoe.com.common.app.CommonUserInfo;
import xiaoe.com.common.app.Global;
import xiaoe.com.common.entitys.LoginUser;
import xiaoe.com.common.db.SQLiteUtil;
import xiaoe.com.network.NetworkCodes;
import xiaoe.com.network.network_interface.INetworkResponse;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.shop.R;
import xiaoe.com.common.db.LoginSQLiteCallback;
import xiaoe.com.shop.common.JumpDetail;
import xiaoe.com.shop.interfaces.OnCancelListener;
import xiaoe.com.shop.interfaces.OnConfirmListener;
import xiaoe.com.shop.widget.CustomDialog;


/**
 * Created by Administrator on 2017/7/17.
 * <br>des：fragment 基类
 */

public class BaseFragment extends Fragment implements INetworkResponse, OnCancelListener, OnConfirmListener {
    private static final String TAG = "BaseFragment";
    private static PopupWindow popupWindow;
    private TextView mToastText;
    protected boolean isFragmentDestroy = false;
    private static final int DIALOG_TAG_LOADING = 5110;//登录弹窗类型
    // Fragment 懒加载字段
    private boolean isFragmentVisible;
    private boolean isReuseView;
    private boolean isFirstVisible;
    private View rootView;
    private CustomDialog dialog;
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

    // 登录的信息
    List<LoginUser> userList;

    // 用户登录信息
    LoginUser user = null;

    // 这个方法除了 Fragment 的可见状态发生变化时会被回调外，创建 Fragment 实例时候也会调用
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        // 若在生命周期之外调用就 return
        if (rootView == null) {
            return;
        }
        if (isFirstVisible && isVisibleToUser) {
            onFragmentFirstVisible();
            isFirstVisible = false;
        }
        if (isVisibleToUser) {
            onFragmentVisibleChange(true);
        }
        if (isFragmentVisible) {
            isFragmentVisible = false;
            onFragmentVisibleChange(false);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 初始化数据库
        SQLiteUtil.init(getActivity().getApplicationContext(), new LoginSQLiteCallback());
        // 如果表不存在，就去创建
        if (!SQLiteUtil.tabIsExist(LoginSQLiteCallback.TABLE_NAME_USER)) {
            SQLiteUtil.execSQL(LoginSQLiteCallback.TABLE_SCHEMA_USER);
        }

        userList = SQLiteUtil.query(LoginSQLiteCallback.TABLE_NAME_USER, "select * from " + LoginSQLiteCallback.TABLE_NAME_USER, null);

        if (userList.size() == 1) {
            user = userList.get(0);
            CommonUserInfo.setApiToken(user.getApi_token());
            CommonUserInfo.setWxAvatar(user.getWxAvatar());
            CommonUserInfo.setWxNickname(user.getWxNickname());
            CommonUserInfo.setPhone(user.getPhone());
            CommonUserInfo.setUserId(user.getUserId());
            CommonUserInfo.setShopId(user.getShopId());
        }
        dialog = new CustomDialog(getContext());
        initVariable();
    }

    // 获取登录用户登录信息集合
    protected List<LoginUser> getLoginUserList() {
        return userList;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // 懒加载的初始化
        if (rootView == null) {
            rootView = view;
            if (getUserVisibleHint()) {
                if (isFirstVisible) {
                    onFragmentFirstVisible();
                    isFirstVisible = false;
                }
                onFragmentVisibleChange(true);
                isFragmentVisible = true;
            }
        }
        super.onViewCreated(isReuseView ? rootView : view, savedInstanceState);
        // toast 的初始化
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
        if(isFragmentDestroy){
            return;
        }

        Global.g().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (success && entity != null) {
                    JSONObject jsonObject = (JSONObject) entity;
                    if(jsonObject.getIntValue("code") == NetworkCodes.CODE_NOT_LOAING){
                        if(!dialog.isShowing() && !((XiaoeActivity) getActivity()).getDialog().isShowing()){
                            dialog.setCancelable(false);
                            dialog.setHintMessage(getString(R.string.login_invalid));
                            dialog.setConfirmText(getString(R.string.btn_again_login));
                            dialog.setCancelListener(BaseFragment.this);
                            dialog.setConfirmListener(BaseFragment.this);
                            dialog.showDialog(DIALOG_TAG_LOADING);
                        }

                    }else{
                        onMainThreadResponse(iRequest, true, entity);
                    }
                } else {
                    onMainThreadResponse(iRequest, false, entity);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isFragmentDestroy = true;
        if(popupWindow!= null){
            popupWindow.dismiss();
            popupWindow = null;
        }
        if(mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
        }
        mHandler = null;
        initVariable();
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {

    }

    public void toastCustom(String msg){
        mHandler.removeMessages(0);
        mToastText.setText(msg);
        popupWindow.showAtLocation(getActivity().getWindow().getDecorView(), Gravity.CENTER, 0, 0);
        mHandler.sendEmptyMessageDelayed(0,1500);
    }

    /**
     * 在弹出对话框时，显示popupWindow，则popupWindow会在对话框下面（被对话框盖住）
     * 从方法是让popupWindow显示在对话上面
     * view 是对话框中的view
     */
    public void toastCustomDialog(View view,String msg){
        mHandler.removeMessages(0);
        mToastText.setText(msg);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        mHandler.sendEmptyMessageDelayed(0,1500);
    }

    // fragment 首次可见时回调，可进行网络加载
    protected void onFragmentFirstVisible () {

    }

    // 去除 setUserVisibleHint 多余的回调，只有当 fragment 可见状态发生变化才回调
    // 可以进行 ui 的操作
    protected void onFragmentVisibleChange (boolean isVisible) {

    }

    protected boolean isFragmentVisible () {
        return isFragmentVisible;
    }

    // 设置是否使用 view 的复用，默认开启，view 的复用可以防止重复创建 view
    protected void reuseView (boolean isReuse) {
        isReuseView = isReuse;
    }

    private void initVariable () {
        isFirstVisible = true;
        isFragmentVisible = false;
        rootView = null;
        isReuseView = true;
    }

    @Override
    public void onClickCancel(View view, int tag) {

    }

    @Override
    public void onClickConfirm(View view, int tag) {
        if(tag == DIALOG_TAG_LOADING){
            SQLiteUtil.init(getActivity(), new LoginSQLiteCallback());
            SQLiteUtil.deleteFrom(LoginSQLiteCallback.TABLE_NAME_USER);
            CommonUserInfo.getInstance().clearUserInfo();
            CommonUserInfo.setApiToken("");
            CommonUserInfo.setIsSuperVip(false);
            CommonUserInfo.setIsSuperVipAvailable(false);

            JumpDetail.jumpLogin(getContext());
            getActivity().finish();
        }
    }

    public CustomDialog getDialog(){
        return dialog;
    }
}
