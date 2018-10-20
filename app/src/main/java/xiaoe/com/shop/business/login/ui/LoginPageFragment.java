package xiaoe.com.shop.business.login.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
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
import xiaoe.com.common.utils.JudgeUtil;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseFragment;
import xiaoe.com.shop.business.login.presenter.LoginTimeCount;
import xiaoe.com.shop.business.main.ui.MainActivity;
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

    private String phoneNum;

    public static LoginPageFragment newInstance(int layoutId) {
        LoginPageFragment loginPageFragment = new LoginPageFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("layoutId", layoutId);
        loginPageFragment.setArguments(bundle);
        return loginPageFragment;
    }

    public static LoginPageFragment newInstance(int layoutId, String phoneNum) {
        LoginPageFragment loginPageFragment = new LoginPageFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("layoutId", layoutId);
        bundle.putString("phoneNum", phoneNum);
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

    private void toggleSoftKeyboard() {
        if (imm != null && imm.isActive()) {
            View view = getActivity().getCurrentFocus();
            if (view != null) {
                IBinder iBinder = view.getWindowToken();
                imm.hideSoftInputFromWindow(iBinder, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    // 初始化主页面
    private void initLoginMainFragment() {
        final EditText mainPhoneContent = (EditText) viewWrap.findViewById(R.id.login_input_num_content);
        final Button mainPhoneSubmit = (Button) viewWrap.findViewById(R.id.login_main_submit);
        TextView mainPhonePwd = (TextView) viewWrap.findViewById(R.id.login_main_pwd);
        TextView mainPhoneWeChat = (TextView) viewWrap.findViewById(R.id.login_main_we_chat);
        TextView mainPhoneTourist = (TextView) viewWrap.findViewById(R.id.login_main_tourist);
        final TextView mainPhoneError = (TextView) viewWrap.findViewById(R.id.login_pwd_error);
        mainPhoneSubmit.setEnabled(false);
        mainPhoneContent.setCompoundDrawables(null, null, null, null);
        mainPhoneContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO: 需要优化输入逻辑
                if (s.length() == 11) {
                    mainPhoneSubmit.setEnabled(true);
                    mainPhoneSubmit.setBackground(getActivity().getResources().getDrawable(R.drawable.person_submit_bg));
                    mainPhoneSubmit.setAlpha(1);
                } else if (s.length() == 2) {
                    String head = s.toString();
                    JudgeUtil.showErrorViewIfNeed(getActivity(), head, mainPhoneError, mainPhoneSubmit);
                } else if (s.length() == 0) {
                    JudgeUtil.hideErrorViewIfNeed(getActivity(), mainPhoneError, mainPhoneSubmit);
                } else {
                    mainPhoneSubmit.setBackground(getActivity().getResources().getDrawable(R.drawable.submit_disabled_bg));
                    mainPhoneSubmit.setAlpha(0.6f);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mainPhoneSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mainPhoneContent.getText().toString().length() == 11) {
                    toggleSoftKeyboard();
                    // TODO: 是否注册，已经注册就直接走登录逻辑，否则 toast 提示去注册
                    String phoneNum = mainPhoneContent.getText().toString();
                    mainPhoneContent.setText("");
                    loginActivity.setPhoneNum(phoneNum);
                    loginActivity.replaceFragment(LoginActivity.CODE);
                }
            }
        });

        mainPhonePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSoftKeyboard();
                mainPhoneContent.setText("");
                mainPhoneError.setVisibility(View.GONE);
                loginActivity.replaceFragment(LoginActivity.PWD);
            }
        });

        mainPhoneWeChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 跳转到微信登录页面
                toggleSoftKeyboard();
                mainPhoneContent.setText("");
                mainPhoneError.setVisibility(View.GONE);
                loginActivity.replaceFragment(LoginActivity.WE_CHAT);
            }
        });

        mainPhoneTourist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSoftKeyboard();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        });
    }

    // 初始化注册页面
    private void initLoginRegisterFragment() {
        final EditText registerPhoneContent = (EditText) viewWrap.findViewById(R.id.login_input_num_content);
        final Button registerPhoneSubmit = (Button) viewWrap.findViewById(R.id.login_register_submit);
        final TextView registerPhoneError = (TextView) viewWrap.findViewById(R.id.login_pwd_error);
        TextView registerPhoneProvision = (TextView) viewWrap.findViewById(R.id.login_register_provision);
        registerPhoneSubmit.setEnabled(false);
        registerPhoneSubmit.setAlpha(0.6f);
        registerPhoneContent.setCompoundDrawables(null, null, null, null);
        registerPhoneContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 11) {
                    if (registerPhoneError.getVisibility() == View.GONE) {
                        registerPhoneSubmit.setEnabled(true);
                        registerPhoneSubmit.setBackground(getActivity().getResources().getDrawable(R.drawable.person_submit_bg));
                        registerPhoneSubmit.setAlpha(1);
                    }
                } else if (s.length() == 2) {
                    String head = s.toString();
                    JudgeUtil.showErrorViewIfNeed(getActivity(), head, registerPhoneError, registerPhoneSubmit);
                } else if (s.length() == 0) {
                    JudgeUtil.hideErrorViewIfNeed(getActivity(), registerPhoneError, registerPhoneSubmit);
                } else {
                    registerPhoneSubmit.setEnabled(false);
                    registerPhoneSubmit.setBackground(getActivity().getResources().getDrawable(R.drawable.submit_disabled_bg));
                    registerPhoneSubmit.setAlpha(0.6f);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        registerPhoneSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 获取 输入框的内容，进行判断，跳转之前把内容清空
                String phoneNum = registerPhoneContent.getText().toString();
                registerPhoneContent.setText("");
                // 将手机号拿到，并且给到活动页
                loginActivity.setPhoneNum(phoneNum);
                loginActivity.replaceFragment(LoginActivity.CODE);
            }
        });

        registerPhoneProvision.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 跳转到服务协议页面
                Toast.makeText(getActivity(), "服务协议...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 初始化验证码页面
    private void initLoginCodeFragment() {
        final LoginTimeCount loginTimeCount = new LoginTimeCount(getActivity(), 60000, 1000, viewWrap);
        TextView loginCodeDesc = (TextView) viewWrap.findViewById(R.id.login_register_code_desc);
        final CodeVerifyView loginCodeContent = (CodeVerifyView) viewWrap.findViewById(R.id.login_register_code_content);
        final TextView loginCodeObtain = (TextView) viewWrap.findViewById(R.id.login_register_code_obtain);
        final TextView loginCodeSecond = (TextView) viewWrap.findViewById(R.id.login_register_code_second);

        // TODO: 手机号码添加两个空格
        loginCodeDesc.setText(String.format(getActivity().getResources().getString(R.string.login_code_desc), phoneNum));

        loginCodeContent.setOnCodeFinishListener(new CodeVerifyView.OnCodeFinishListener() {
            @Override
            public void onComplete(String content) {
                // 拿到用户输入的验证码，然后跟返回的验证码对比
                // 对了要去设置密码页面
                if (content.equals("1234")) {
                    loginCodeContent.clearAllEditText();
                    loginTimeCount.cancel();
                    toggleSoftKeyboard();
                    loginActivity.replaceFragment(LoginActivity.SET_PWD);
                } else {
                    loginCodeContent.setErrorBg(R.drawable.cv_error_bg);
                }
            }
        });

        loginCodeObtain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginCodeSecond.setVisibility(View.VISIBLE);
                loginTimeCount.start();
            }
        });
    }

    // 初始化密码登录页面
    private void initLoginPwdFragment() {
        final EditText pwdPhone = (EditText) viewWrap.findViewById(R.id.login_input_num_content);
        final EditText pwdPassWord = (EditText) viewWrap.findViewById(R.id.login_pwd_pass_word);
        final Button pwdSubmit = (Button) viewWrap.findViewById(R.id.login_pwd_submit);
        TextView pwdForget = (TextView) viewWrap.findViewById(R.id.login_pwd_forget);
        final TextView pwdError = (TextView) viewWrap.findViewById(R.id.login_pwd_error);

        setEditTextOnTouchListener(pwdPassWord);

        pwdSubmit.setAlpha(0.6f);
        pwdPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 11 && pwdError.getVisibility() != View.VISIBLE) {
                    pwdSubmit.setEnabled(true);
                    pwdSubmit.setBackground(getActivity().getResources().getDrawable(R.drawable.person_submit_bg));
                    pwdSubmit.setAlpha(1);
                } else if (s.length() == 2) {
                    String head = s.toString();
                    JudgeUtil.showErrorViewIfNeed(getActivity(), head, pwdError, pwdPassWord);
                    pwdSubmit.setEnabled(false);
                } else if (s.length() == 0) {
                    JudgeUtil.hideErrorViewIfNeed(getActivity(), pwdError, pwdPassWord);
                } else {
                    pwdSubmit.setAlpha(0.6f);
                    pwdSubmit.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        pwdSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = pwdPassWord.getText().toString();
                int passwordLength = pwdPassWord.getText().toString().length();
                pwdPassWord.setText("");
                if (passwordLength > 6 && password.equals("123456789")) {
                    toggleSoftKeyboard();
                    // 密码正确，登录成功
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    getActivity().startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), "密码错误", Toast.LENGTH_SHORT).show();
                }
            }
        });
        pwdForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pwdPhone.setText("");
                pwdPassWord.setText("");
                pwdError.setVisibility(View.GONE);
                loginActivity.replaceFragment(LoginActivity.FIND_PWD);
            }
        });
    }

    // 初始化找回密码页面
    private void initLoginFindFragment() {
        final EditText findPhone = (EditText) viewWrap.findViewById(R.id.login_input_num_content);
        final TextView findError = (TextView) viewWrap.findViewById(R.id.login_pwd_error);
        final Button findSubmit = (Button) viewWrap.findViewById(R.id.login_find_submit);

        findPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String head = s.toString();
                if (head.length() == 11 && findError.getVisibility() == View.GONE) {
                    findSubmit.setAlpha(1);
                    findSubmit.setEnabled(true);
                } else if (head.length() == 2) {
                    JudgeUtil.showErrorViewIfNeed(getActivity(), head, findError, findSubmit);
                    findSubmit.setAlpha(0.6f);
                    findSubmit.setEnabled(false);
                } else if (head.length() == 0) {
                    JudgeUtil.hideErrorViewIfNeed(getActivity(), findError, findSubmit);
                    findSubmit.setAlpha(0.6f);
                    findSubmit.setEnabled(false);
                } else {
                    findSubmit.setAlpha(0.6f);
                    findSubmit.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        findSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNum = findPhone.getText().toString();
                findPhone.setText("");
                toggleSoftKeyboard();
                loginActivity.setPhoneNum(phoneNum);
                loginActivity.replaceFragment(LoginActivity.CODE);
            }
        });
    }

    // 设置密码页面
    private void initLoginSetPwdFragment() {
        final EditText setPwdContent = (EditText) viewWrap.findViewById(R.id.login_set_pwd_content);
        final Button setPwdSubmit = (Button) viewWrap.findViewById(R.id.login_set_pwd_submit);

        setEditTextOnTouchListener(setPwdContent);
        setPwdSubmit.setEnabled(false);
        setPwdSubmit.setAlpha(0.6f);
        setPwdContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String head = s.toString();
                if (head.length() > 6) {
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
                // 回到登录页面
                if (setPwdContent.getText().toString().length() > 6) {
                    setPwdContent.setText("");
                    toggleSoftKeyboard();
                    loginActivity.replaceFragment(LoginActivity.PWD);
                }
            }
        });
    }

    private void initLoginWeChatFragment() {
        Button weChatSubmit = (Button) viewWrap.findViewById(R.id.login_we_chat_submit);

        weChatSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "调取微信登录页面", Toast.LENGTH_SHORT).show();
                // TODO: 调取微信登录页面回来之后，先判断是否是老用户，是就直接登录，否则跳转绑定手机号，
                // 绑定手机号
                loginActivity.replaceFragment(LoginActivity.BIND_PHONE);
            }
        });
    }

    private void initLoginBindPhoneFragment() {
        final EditText bindPhoneContent = (EditText) viewWrap.findViewById(R.id.login_input_num_content);
        final TextView bindPhoneError = (TextView) viewWrap.findViewById(R.id.login_pwd_error);
        final Button bindPhoneSubmit = (Button) viewWrap.findViewById(R.id.login_bind_phone_submit);

        bindPhoneContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String head = s.toString();
                if (head.length() == 11 && bindPhoneError.getVisibility() == View.GONE) {
                    bindPhoneSubmit.setAlpha(1);
                    bindPhoneSubmit.setEnabled(true);
                } else if (head.length() == 2) {
                    JudgeUtil.showErrorViewIfNeed(getActivity(), head, bindPhoneError, bindPhoneSubmit);
                    bindPhoneSubmit.setAlpha(0.6f);
                    bindPhoneSubmit.setEnabled(false);
                } else if (head.length() == 0) {
                    JudgeUtil.hideErrorViewIfNeed(getActivity(), bindPhoneError, bindPhoneSubmit);
                    bindPhoneSubmit.setAlpha(0.6f);
                    bindPhoneSubmit.setEnabled(false);
                } else {
                    bindPhoneSubmit.setAlpha(0.6f);
                    bindPhoneSubmit.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        bindPhoneSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到获取验证码页面
                String phoneNum = bindPhoneContent.getText().toString();
                toggleSoftKeyboard();
                bindPhoneContent.setText("");
                loginActivity.setPhoneNum(phoneNum);
                loginActivity.replaceFragment(LoginActivity.CODE);
            }
        });
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
}
