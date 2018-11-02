package xiaoe.com.shop.business.login.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import xiaoe.com.common.utils.SharedPreferencesUtil;
import xiaoe.com.shop.utils.JudgeUtil;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseFragment;
import xiaoe.com.shop.business.login.presenter.LoginTimeCount;
import xiaoe.com.shop.common.JumpDetail;
import xiaoe.com.shop.widget.CodeVerifyView;

public class LoginPageFragment extends BaseFragment {

    private static final String TAG = "LoginPageFragment";

    private Unbinder unbinder;
    private Context mContext;
    private boolean destroyView = false;
    protected View viewWrap;

    private int layoutId = -1;
    private LoginActivity loginActivity;

    // 软键盘
    InputMethodManager imm;

    private String phoneNum; // 手机号
    private String smsCode;  // 验证码

    public static LoginPageFragment newInstance(int layoutId) {
        LoginPageFragment loginPageFragment = new LoginPageFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("layoutId", layoutId);
        loginPageFragment.setArguments(bundle);
        return loginPageFragment;
    }

    // 验证码 fragment 需要知道手机号
    public static LoginPageFragment newInstance(int layoutId, String phoneNum) {
        LoginPageFragment loginPageFragment = new LoginPageFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("layoutId", layoutId);
        bundle.putString("phoneNum", phoneNum);
        loginPageFragment.setArguments(bundle);
        return loginPageFragment;
    }

    // 设置密码 fragment 需要知道手机号和输入的验证码
    public static LoginPageFragment newInstance(int layoutId, String phoneNum, String smsCode) {
        LoginPageFragment loginPageFragment = new LoginPageFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("layoutId", layoutId);
        bundle.putString("phoneNum", phoneNum);
        bundle.putString("smsCode", smsCode);
        loginPageFragment.setArguments(bundle);
        return loginPageFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            layoutId = bundle.getInt("layoutId");
            if (bundle.getString("phoneNum") != null) {
                phoneNum = bundle.getString("phoneNum");
            }
            if (bundle.getString("smsCode") != null) {
                smsCode = bundle.getString("smsCode");
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewWrap = inflater.inflate(layoutId, null, false);
        unbinder = ButterKnife.bind(this, viewWrap);
        mContext = getContext();
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        loginActivity = (LoginActivity) getActivity();
        return viewWrap;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (layoutId != -1) {
            initView();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        destroyView = true;
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    private void initView() {
        switch (layoutId) {
            case R.layout.fragment_login_main:
                initLoginMainFragment();
                break;
            case R.layout.fragment_login_register:
                initLoginRegisterFragment();
                break;
            case R.layout.fragment_login_code:
                initLoginCodeFragment();
                break;
            case R.layout.fragment_login_pwd:
                initLoginPwdFragment();
                break;
            case R.layout.fragment_login_find:
                initLoginFindFragment();
                break;
            case R.layout.fragment_login_set_pwd:
                initLoginSetPwdFragment();
                break;
            case R.layout.fragment_login_we_chat:
                initLoginWeChatFragment();
                break;
            case R.layout.fragment_login_bind_phone:
                initLoginBindPhoneFragment();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            if (phoneContent != null && !TextUtils.isEmpty(phoneContent.getText().toString())) {
                phoneContent.setText("");
            }
            if (loginTimeCount != null) {
                loginTimeCount.cancel();
            }
            if (passwordContent != null && !TextUtils.isEmpty(passwordContent.getText().toString())) {
                passwordContent.setText("");
            }
        }
    }

    // 弹出或关闭软键盘
    private void toggleSoftKeyboard() {
        if (imm != null && imm.isActive()) {
            View view = getActivity().getCurrentFocus();
            if (view != null) {
                IBinder iBinder = view.getWindowToken();
                imm.hideSoftInputFromWindow(iBinder, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    // 手机号输入
    protected EditText phoneContent;
    protected Button phoneObtainCode;
    protected TextView phoneErrorTip;

    protected EditText passwordContent;

    // 初始化主页面
    private void initLoginMainFragment() {

        phoneContent = (EditText) viewWrap.findViewById(R.id.login_input_num_content);
        phoneErrorTip = (TextView) viewWrap.findViewById(R.id.login_error_tip);
        phoneObtainCode = (Button) viewWrap.findViewById(R.id.login_submit_btn);
        initPhoneInputListener(LoginActivity.MAIN);

        TextView mainPhonePwd = (TextView) viewWrap.findViewById(R.id.login_main_pwd);
        TextView mainPhoneWeChat = (TextView) viewWrap.findViewById(R.id.login_main_we_chat);
        TextView mainPhoneTourist = (TextView) viewWrap.findViewById(R.id.login_main_tourist);

        // 账号密码登录
        mainPhonePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSoftKeyboard();
                phoneContent.setText("");
                phoneErrorTip.setVisibility(View.GONE);
                loginActivity.replaceFragment(LoginActivity.PWD);
            }
        });

        mainPhoneWeChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 判断是否已经绑定了微信
                toggleSoftKeyboard();
                phoneContent.setText("");
                phoneErrorTip.setVisibility(View.GONE);
                ((LoginActivity) getActivity()).loginPresenter.reqWXLogin();
//                loginActivity.replaceFragment(LoginActivity.BIND_WE_CHAT);
            }
        });

        mainPhoneTourist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSoftKeyboard();
                // 游客登录
                JumpDetail.jumpMain(getActivity(), false);
            }
        });
    }

    // 初始化注册页面
    private void initLoginRegisterFragment() {
        loginActivity.isRegister = true;
        phoneContent = (EditText) viewWrap.findViewById(R.id.login_input_num_content);
        phoneErrorTip = (TextView) viewWrap.findViewById(R.id.login_error_tip);
        phoneObtainCode = (Button) viewWrap.findViewById(R.id.login_submit_btn);

        TextView registerPhoneProvision = (TextView) viewWrap.findViewById(R.id.login_register_provision);

        initPhoneInputListener(LoginActivity.REGISTER);

        registerPhoneProvision.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 跳转到服务协议页面
                Toast.makeText(getActivity(), "服务协议...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected LoginTimeCount loginTimeCount;
    protected CodeVerifyView loginCodeContent;
    // 初始化验证码页面
    private void initLoginCodeFragment() {
        loginTimeCount = new LoginTimeCount(getActivity(), 60000, 1000, viewWrap);
        loginTimeCount.start();
        TextView loginCodeDesc = (TextView) viewWrap.findViewById(R.id.login_register_code_desc);
        loginCodeContent = (CodeVerifyView) viewWrap.findViewById(R.id.login_register_code_content);
        final TextView loginCodeObtain = (TextView) viewWrap.findViewById(R.id.login_register_code_obtain);
        final TextView loginCodeSecond = (TextView) viewWrap.findViewById(R.id.login_register_code_second);

        // TODO: 手机号码添加两个空格

        loginCodeDesc.setText(String.format(getActivity().getResources().getString(R.string.login_code_desc), phoneNum));

        loginCodeContent.setOnCodeFinishListener(new CodeVerifyView.OnCodeFinishListener() {
            @Override
            public void onComplete(String content) {
                // 拿到用户输入的验证码，然后跟返回的验证码对比
                switch (loginActivity.preTag) {
                    case LoginActivity.MAIN: // 通过验证码登录
                        loginActivity.loginPresenter.loginBySmsCode(phoneNum, content);
                        break;
                    case LoginActivity.BIND_PHONE: // 绑定手机
//                        loginActivity.loginPresenter.verifyLoginCode(phoneNum, content);
                        String accessToken = SharedPreferencesUtil.getData("accessToken", "").toString();
                        if (accessToken != null && !accessToken.equals("")) {
                            loginActivity.loginPresenter.bindPhone(accessToken, phoneNum, content);
                            SharedPreferencesUtil.putData("accessToken", ""); // 用完就清空
                            toggleSoftKeyboard();
                        }
                        break;
                    case LoginActivity.REGISTER: // 确认注册验证码
                        loginActivity.loginPresenter.verifyRegisterCode(phoneNum, content);
                        break;
                    case LoginActivity.FIND_PWD: // 确认找回密码验证码
                        loginActivity.loginPresenter.verifyFindPwdCode(phoneNum, content);
                        break;
                }
                loginActivity.setPhoneNum(phoneNum);
                loginActivity.setSmsCode(content);
            }
        });

        loginCodeObtain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginActivity.loginPresenter.obtainPhoneCode(phoneNum);
                loginCodeSecond.setVisibility(View.VISIBLE);
                loginTimeCount.start();
            }
        });
    }

    // 初始化密码登录页面
    private void initLoginPwdFragment() {

        phoneContent = (EditText) viewWrap.findViewById(R.id.login_input_num_content);
        phoneErrorTip = (TextView) viewWrap.findViewById(R.id.login_error_tip);
        phoneObtainCode = (Button) viewWrap.findViewById(R.id.login_submit_btn);
        phoneObtainCode.setVisibility(View.GONE);
        initPhoneInputListener(LoginActivity.PWD);

        phoneErrorTip.setVisibility(View.GONE);
        passwordContent = (EditText) viewWrap.findViewById(R.id.login_pwd_pass_word);
        final Button pwdSubmit = (Button) viewWrap.findViewById(R.id.login_pwd_submit);
        TextView pwdForget = (TextView) viewWrap.findViewById(R.id.login_pwd_forget);
        final TextView pwdError = (TextView) viewWrap.findViewById(R.id.login_pwd_error);
        setEditTextOnTouchListener(passwordContent);
        passwordContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = s.toString();
                if (password.length() >= 6 && phoneContent.getText().toString().length() == 11 && (phoneErrorTip.getVisibility() != View.VISIBLE)) {
                    pwdSubmit.setAlpha(1);
                    pwdSubmit.setEnabled(true);
                } else {
                    pwdSubmit.setAlpha(0.6f);
                    pwdSubmit.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        pwdSubmit.setAlpha(0.6f);
        pwdSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSoftKeyboard();
                String phoneNum = phoneContent.getText().toString();
                String password = passwordContent.getText().toString();
                phoneContent.setText("");
                passwordContent.setText("");
                loginActivity.loginPresenter.loginByPassword(phoneNum, password);
            }
        });
        pwdForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneContent.setText("");
                passwordContent.setText("");
                pwdError.setVisibility(View.GONE);
                loginActivity.replaceFragment(LoginActivity.FIND_PWD);
            }
        });
    }

    // 初始化找回密码页面
    private void initLoginFindFragment() {

        phoneContent = (EditText) viewWrap.findViewById(R.id.login_input_num_content);
        phoneErrorTip = (TextView) viewWrap.findViewById(R.id.login_error_tip);
        phoneObtainCode = (Button) viewWrap.findViewById(R.id.login_submit_btn);

        initPhoneInputListener(LoginActivity.FIND_PWD);
    }

    // 设置密码页面
    private void initLoginSetPwdFragment() {
        passwordContent = (EditText) viewWrap.findViewById(R.id.login_set_pwd_content);
        final Button setPwdSubmit = (Button) viewWrap.findViewById(R.id.login_set_pwd_submit);

        setEditTextOnTouchListener(passwordContent);
        setPwdSubmit.setEnabled(false);
        setPwdSubmit.setAlpha(0.6f);
        passwordContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String head = s.toString();
                if (head.length() >= 6) {
                    setPwdSubmit.setEnabled(true);
                    setPwdSubmit.setAlpha(1);
                } else {
                    setPwdSubmit.setEnabled(false);
                    setPwdSubmit.setAlpha(0.6f);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        setPwdSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 进行注册操作
                String password = passwordContent.getText().toString();
                if (password.length() >= 6) {
                    // 判断是注册流程还是修改密码流程
                    if (loginActivity.isRegister) { // 注册流程
                        loginActivity.loginPresenter.doRegister(phoneNum, password, smsCode);
                    } else { // 修改密码流程
                        loginActivity.loginPresenter.resetPasswordBySms(phoneNum, smsCode, password);
                    }
                    passwordContent.setText("");
                    toggleSoftKeyboard();
                }
            }
        });
    }

    private void initLoginWeChatFragment() {
        Button weChatSubmit = (Button) viewWrap.findViewById(R.id.login_we_chat_submit);

        weChatSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LoginActivity) getActivity()).loginPresenter.reqWXLogin();
                // TODO: 调取微信登录页面回来之后，先判断是否是老用户，是就直接登录，否则跳转绑定手机号，
                // 绑定手机号
//                loginActivity.replaceFragment(LoginActivity.BIND_PHONE);
            }
        });
    }

    // 初始化绑定手机 fragment
    private void initLoginBindPhoneFragment() {

        phoneContent = (EditText) viewWrap.findViewById(R.id.login_input_num_content);
        phoneErrorTip = (TextView) viewWrap.findViewById(R.id.login_error_tip);
        phoneObtainCode = (Button) viewWrap.findViewById(R.id.login_submit_btn);

        initPhoneInputListener(LoginActivity.BIND_PHONE);
    }


    @SuppressLint("ClickableViewAccessibility")
    private void setEditTextOnTouchListener(final EditText et) {
        et.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 拿到画在尾部的 drawable
                Drawable drawable = et.getCompoundDrawables()[2];
                // 如果没有不处理
                if (drawable == null) {
                    return false;
                }
                // 如果不是抬起事件，不处理
                if (event.getAction() != MotionEvent.ACTION_UP) {
                    return false;
                }
                // 点中 drawable
                if (event.getX() > et.getWidth() - et.getPaddingEnd() - drawable.getIntrinsicWidth()) {
                    if (et.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD + InputType.TYPE_CLASS_TEXT) {
                        // 设置隐藏按钮
                        Drawable right = getActivity().getResources().getDrawable(R.mipmap.icon_hide);
                        right.setBounds(0, 0, right.getMinimumWidth(), drawable.getMinimumHeight());
                        et.setCompoundDrawables(null, null, right, null);
                        et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    } else {
                        // 设置显示按钮
                        Drawable right = getActivity().getResources().getDrawable(R.mipmap.icon_show);
                        right.setBounds(0, 0, right.getMinimumWidth(), drawable.getMinimumHeight());
                        et.setCompoundDrawables(null, null, right, null);
                        et.setInputType(InputType.TYPE_CLASS_TEXT |InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    // 初始化手机号输入、按钮监听
    private void initPhoneInputListener(String tag) {
        phoneObtainCode.setEnabled(false);
        phoneObtainCode.setAlpha(0.6f);
        // 监听文字变化
        phoneContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String head = s.toString();
                if (head.length() == 11 && phoneErrorTip.getVisibility() == View.GONE) {
                    phoneObtainCode.setAlpha(1);
                    phoneObtainCode.setEnabled(true);
                } else {
                    JudgeUtil.showErrorViewIfNeed(getActivity(), head, phoneErrorTip, phoneObtainCode);
                    phoneObtainCode.setAlpha(0.6f);
                    phoneObtainCode.setEnabled(false);
                    phoneContent.setCursorVisible(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // 设置布局点击
        viewWrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phoneContent.isCursorVisible()) {
                    phoneContent.setCursorVisible(false);
                    toggleSoftKeyboard(); // 关闭软键盘
                }
            }
        });

        // 设置焦点监听
//        phoneContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (tag.equals(LoginActivity.REGISTER)) {
//                    return;
//                }
//                if (!hasFocus) { // 失去焦点的时候，如果手机号长度为 11 就可以检测这个手机号是否已经注册
//                    String phoneNumber = phoneContent.getText().toString();
//                    if (phoneNumber.length() == 11) {
//                        loginActivity.loginPresenter.checkRegister(phoneNumber);
//                    }
//                }
//            }
//        });

        // 设置点击事件
        phoneContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneContent.setCursorVisible(true);
            }
        });

        // 设置确认键点击事件
        phoneContent.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (phoneContent.isCursorVisible()) {
                        phoneContent.setCursorVisible(false);
                    }
                    toggleSoftKeyboard(); // 关闭软键盘
                }
                // 如果消费了事件的话那焦点失去的事件就不会被触发
                return false;
            }
        });

        switch (tag) {
            case LoginActivity.MAIN:
                // 首页获取验证码按钮
                phoneObtainCode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (phoneContent.getText().toString().length() == 11) {
                            if (phoneContent.isCursorVisible()) {
                                phoneContent.setCursorVisible(false);
                            }
                            toggleSoftKeyboard();
                            // TODO: 已经注册就直接走登录逻辑，否则 toast 提示去注册
                            String phoneNumber = phoneContent.getText().toString();
                            if (phoneNumber.length() == 11) {
                                loginActivity.loginPresenter.checkRegister(phoneNumber);
                            }
                            loginActivity.setPhoneNum(phoneNumber);
                        }
                    }
                });
                break;
            case LoginActivity.PWD:
                // do nothing, 密码登录不需要验证码，隐藏该按钮
                break;
            case LoginActivity.REGISTER:
            case LoginActivity.BIND_PHONE:
                phoneObtainCode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String phoneNum = phoneContent.getText().toString();
                        phoneContent.setText("");
                        toggleSoftKeyboard();
                        loginActivity.setPhoneNum(phoneNum);
                        loginActivity.loginPresenter.obtainPhoneCode(phoneNum);
                        loginActivity.replaceFragment(LoginActivity.CODE);
                    }
                });
                break;
            case LoginActivity.FIND_PWD:
                loginActivity.isRegister = false;
                phoneObtainCode.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String phoneNum = phoneContent.getText().toString();
                        phoneContent.setText("");
                        toggleSoftKeyboard();
                        loginActivity.setPhoneNum(phoneNum);
                        loginActivity.loginPresenter.obtainPhoneCode(phoneNum);
                        loginActivity.replaceFragment(LoginActivity.CODE);
                    }
                });
                break;
        }
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        Log.d(TAG, "onMainThreadResponse: loginPageFragment request result");
    }
}
