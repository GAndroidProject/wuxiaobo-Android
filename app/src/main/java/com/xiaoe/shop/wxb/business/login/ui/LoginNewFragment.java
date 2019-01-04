package com.xiaoe.shop.wxb.business.login.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.common.utils.SharedPreferencesUtil;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseFragment;
import com.xiaoe.shop.wxb.business.login.presenter.LoginTimeCount;
import com.xiaoe.shop.wxb.utils.JudgeUtil;
import com.xiaoe.shop.wxb.widget.CodeVerifyView;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;

public class LoginNewFragment extends BaseFragment {

    private static final String TAG = "LoginNewFragment";

    protected View viewWrap;
    private Context mContext;
    private int layoutId = -1;
    private LoginNewActivity loginNewActivity;

    public static LoginNewFragment newInstance(int layoutId) {
        LoginNewFragment loginNewFragment = new LoginNewFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("layoutId", layoutId);
        loginNewFragment.setArguments(bundle);
        return loginNewFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            layoutId = bundle.getInt("layoutId");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewWrap = inflater.inflate(layoutId, null, false);
        mContext = getContext();
        loginNewActivity = (LoginNewActivity) getActivity();
        if (layoutId != -1) {
            initView();
            initListener();
        }
        return viewWrap;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    private void initView() {
        switch (layoutId) {
            case R.layout.fragment_login_main_new:
                initLoginMainFragment();
                break;
            case R.layout.fragment_login_code_new:
                initLoginCodeFragment();
                break;
            case R.layout.fragment_login_we_chat_new:
                initLoginWxFragment();
                break;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            if (loginTimeCount != null) {
                loginTimeCount.cancel();
                loginTimeCount = null;
            }
            if (codeContent != null) {
                codeContent.clearAllEditText();
            }
        } else {
            if (getDialog().isShowing()) {
                getDialog().dismissDialog();
            }
            if (layoutId != 1) {
                updateView();
            }
        }
    }

    private void updateView() {
        if (codeTip != null) {
            codeTip.setVisibility(View.GONE);
            codeTip.setText("");
        }
        switch (layoutId) {
            case R.layout.fragment_login_main_new:
                break;
            case R.layout.fragment_login_code_new:
                if (codeHeader != null) {
                    codeHeader.setText(String.format(getActivity().getResources().getString(R.string.login_code_desc), loginNewActivity.phoneNum));
                }
                loginTimeCount = new LoginTimeCount(getActivity(), 60000, 1000, viewWrap);
                loginTimeCount.start();
                break;
            case R.layout.fragment_login_we_chat_new:
                break;
        }
    }

    private void initListener() {
        viewWrap.setOnClickListener(new DebouncingOnClickListener() {
            @Override
            public void doClick(View v) {
                loginNewActivity.toggleSoftKeyboard();
            }
        });
    }

    private void initLoginWxFragment() {
        FrameLayout bindWx = (FrameLayout) viewWrap.findViewById(R.id.login_bind_we_chat);

        bindWx.setOnClickListener(new DebouncingOnClickListener() {
            @Override
            public void doClick(View v) {
                getDialog().showLoadDialog(false);
                loginNewActivity.loginPresenter.reqWXLogin();
            }
        });
    }

    protected LoginTimeCount loginTimeCount;
    protected CodeVerifyView codeContent;
    protected TextView codeTip;
    protected LinearLayout codeObtainWrap;
    protected TextView codeHeader;

    private void initLoginCodeFragment() {
        codeContent = (CodeVerifyView) viewWrap.findViewById(R.id.login_code_content_new);
        codeTip = (TextView) viewWrap.findViewById(R.id.login_code_tip_new);
        codeHeader = (TextView) viewWrap.findViewById(R.id.login_code_desc_new);
        codeObtainWrap = (LinearLayout) viewWrap.findViewById(R.id.login_obtain_wrap);

        TextView codeObtain = (TextView) viewWrap.findViewById(R.id.login_register_code_obtain);
        TextView codeTime = (TextView) viewWrap.findViewById(R.id.login_register_code_second);

        // 手机号码添加两个空格
        codeHeader.setText(String.format(getActivity().getResources().getString(R.string.login_code_desc), loginNewActivity.phoneNum));

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(Dp2Px2SpUtil.dp2px(mContext, 20), Dp2Px2SpUtil.dp2px(mContext, 48), Dp2Px2SpUtil.dp2px(mContext, 20), 0);
        codeObtainWrap.setLayoutParams(layoutParams);

        codeHeader.setText(String.format(getString(R.string.login_code_desc), loginNewActivity.phoneNum));
        codeContent.setOnCodeFinishListener(new CodeVerifyView.OnCodeFinishListener() {
            @Override
            public void onComplete(String content) {
                getDialog().showLoadDialog(false);
                loginNewActivity.smsCode = content;
                loginNewActivity.loginPresenter.verifyRegisterCode(loginNewActivity.phoneNum, content);
            }
        });

        codeObtain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginNewActivity.loginPresenter.obtainPhoneCode(loginNewActivity.phoneNum);
                codeTime.setVisibility(View.VISIBLE);
                if (codeObtain == null) {
                    loginTimeCount = new LoginTimeCount(getActivity(), 60000, 1000, viewWrap);
                }
                loginTimeCount.start();
            }
        });

        loginTimeCount = new LoginTimeCount(getActivity(), 60000, 1000, viewWrap);
        loginTimeCount.start();
    }

    private void initLoginMainFragment() {
        EditText loginInput = (EditText) viewWrap.findViewById(R.id.login_input);
        TextView loginErrorTip = (TextView) viewWrap.findViewById(R.id.login_error_tip_new);
        Button loginSubmit = (Button) viewWrap.findViewById(R.id.login_submit_new);
        TextView loginTip = (TextView) viewWrap.findViewById(R.id.login_tip_new);

        loginErrorTip.setVisibility(View.GONE);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(Dp2Px2SpUtil.dp2px(mContext, 15), Dp2Px2SpUtil.dp2px(mContext, 40), Dp2Px2SpUtil.dp2px(mContext, 15), 0);
        loginSubmit.setLayoutParams(layoutParams);

        if (!TextUtils.isEmpty(loginNewActivity.mainFragmentTitle)) {
            if (loginNewActivity.mainFragmentTitle.equals(getString(R.string.login_main_phone))) { // 输入手机号页面
                loginTip.setVisibility(View.VISIBLE);
            } else if (loginNewActivity.mainFragmentTitle.equals(getString(R.string.login_bind_phone_title))) { // 绑定手机号页面
                loginTip.setVisibility(View.GONE);
            }
        } else {
            Log.d(TAG, "initLoginMainFragment: 主页没有标题，有问题...");
            return;
        }

        loginInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String head = s.toString();
                if (head.length() == 11 && loginErrorTip.getVisibility() == View.GONE) {
                    loginSubmit.setEnabled(true);
                    loginSubmit.setAlpha(1f);
                } else {
                    JudgeUtil.newShowErrorViewIfNeed(getActivity(), head, loginErrorTip, loginSubmit);
                    loginSubmit.setEnabled(false);
                    loginInput.setCursorVisible(true);
                }
                if (head.length() > 0) {
                    loginInput.setTextColor(getResources().getColor(R.color.main_title_color));
                } else {
                    loginInput.setTextColor(getResources().getColor(R.color.secondary_title_color));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        loginSubmit.setOnClickListener(new DebouncingOnClickListener() {
            @Override
            public void doClick(View v) {
                loginNewActivity.phoneNum = loginInput.getText().toString();
                // 老接口
                loginNewActivity.loginPresenter.checkRegister(loginNewActivity.phoneNum);
            }
        });
    }
}
