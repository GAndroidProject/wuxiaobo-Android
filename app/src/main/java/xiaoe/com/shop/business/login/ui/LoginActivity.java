package xiaoe.com.shop.business.login.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import xiaoe.com.common.app.Global;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.XiaoeActivity;
import xiaoe.com.shop.business.login.presenter.LoginTimeCount;
import xiaoe.com.shop.utils.StatusBarUtil;
import xiaoe.com.shop.widget.CodeVerifyView;

public class LoginActivity extends XiaoeActivity {

    private static final String TAG = "LoginActivity";

    // 需要切换的 fragment tag
    protected static final String MAIN = "login_main"; // 登录主页
    protected static final String CODE = "login_code"; // 验证码页
    protected static final String BIND_PHONE = "login_bind"; // 绑定手机号页
    protected static final String PWD = "login_pwd"; // 密码登录页
    protected static final String FIND_PWD = "login_find_pwd"; // 找回密码页
    protected static final String SET_PWD = "login_set_pwd"; // 设置密码页
    protected static final String REGISTER = "login_register"; // 注页面
    protected static final String WE_CHAT = "login_we_chat"; // 绑定微信页

    @BindView(R.id.login_back)
    ImageView loginBack;
    @BindView(R.id.login_register)
    TextView loginRegister;

    private Fragment currentFragment;
    // 软键盘
    InputMethodManager imm;

    // 手机号
    protected String phoneNum;

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //状态栏颜色字体(白底黑字)修改 Android6.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setStatusBarColor(getWindow(), Color.parseColor(Global.g().getGlobalColor()), View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            Global.g().setGlobalColor("#000000");
            StatusBarUtil.setStatusBarColor(getWindow(), Color.parseColor(Global.g().getGlobalColor()), View.SYSTEM_UI_FLAG_VISIBLE);
        }
        // TODO: 找本地缓存的登录信息，如果没有就显示登录主页，如果有就直接跳到主页，先默认没有登录

        currentFragment = LoginPageFragment.newInstance(R.layout.fragment_login_main);
        getSupportFragmentManager().beginTransaction().add(R.id.login_container, currentFragment, MAIN).commit();

        // 网络请求
//        LoginPresenter loginPresenter = new LoginPresenter(this, "");
//        loginPresenter.requestSearchResult();

        initView();
        initListener();
    }

    private void initView() {
        if (currentFragment.getTag().equals(MAIN)) {
            loginRegister.setVisibility(View.VISIBLE);
            loginBack.setVisibility(View.GONE);
        } else {
            loginRegister.setVisibility(View.GONE);
            loginBack.setVisibility(View.VISIBLE);
        }
    }

    private void initListener() {

        loginBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        loginRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 换页之前将 editText 的内容清掉
                ((EditText)((LoginPageFragment)currentFragment).viewWrap.findViewById(R.id.login_input_num_content)).setText("");
                // 如果软件盘在的话，就把软键盘隐藏
                toggleSoftKeyboard();
                replaceFragment(REGISTER);
            }
        });
    }

    @Override
    public void onBackPressed() {
        toggleSoftKeyboard();
        if (currentFragment != null) {
            switch (currentFragment.getTag()) {
                case REGISTER:
                    ((EditText) ((LoginPageFragment) currentFragment).viewWrap.findViewById(R.id.login_input_num_content)).setText("");
                    ((LoginPageFragment) currentFragment).viewWrap.findViewById(R.id.login_pwd_error).setVisibility(View.GONE);
                    loginBack.setVisibility(View.GONE);
                    loginRegister.setVisibility(View.VISIBLE);
                    replaceFragment(MAIN);
                    break;
                case CODE:
                    ((CodeVerifyView) ((LoginPageFragment) currentFragment).viewWrap.findViewById(R.id.login_register_code_content)).clearAllEditText();
                    // TODO: 停掉 time count
                    loginBack.setVisibility(View.GONE);
                    loginRegister.setVisibility(View.VISIBLE);
                    replaceFragment(MAIN);
                    break;
                case PWD:
                    ((EditText) ((LoginPageFragment) currentFragment).viewWrap.findViewById(R.id.login_input_num_content)).setText("");
                    ((EditText) ((LoginPageFragment) currentFragment).viewWrap.findViewById(R.id.login_pwd_pass_word)).setText("");
                    ((LoginPageFragment) currentFragment).viewWrap.findViewById(R.id.login_pwd_error).setVisibility(View.GONE);
                    loginBack.setVisibility(View.GONE);
                    loginRegister.setVisibility(View.VISIBLE);
                    replaceFragment(MAIN);
                    break;
                case FIND_PWD:
                    ((EditText) ((LoginPageFragment) currentFragment).viewWrap.findViewById(R.id.login_input_num_content)).setText("");
                    replaceFragment(PWD);
                    break;
                case SET_PWD:
                    ((EditText) ((LoginPageFragment) currentFragment).viewWrap.findViewById(R.id.login_set_pwd_content)).setText("");
                    loginBack.setVisibility(View.GONE);
                    loginRegister.setVisibility(View.VISIBLE);
                    replaceFragment(MAIN);
                    break;
                case WE_CHAT:
                    loginBack.setVisibility(View.GONE);
                    loginRegister.setVisibility(View.VISIBLE);
                    replaceFragment(MAIN);
                    break;
                case BIND_PHONE:
                    ((EditText) ((LoginPageFragment) currentFragment).viewWrap.findViewById(R.id.login_input_num_content)).setText("");
                    loginBack.setVisibility(View.GONE);
                    loginRegister.setVisibility(View.VISIBLE);
                    replaceFragment(MAIN);
                    break;
            }
        }
    }

    protected void replaceFragment(String tag) {
        if (currentFragment != null) {
            getSupportFragmentManager().beginTransaction().hide(currentFragment).commit();
        }
        currentFragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (currentFragment == null) {
            switch (tag) {
                case MAIN: // 登录主页
                    loginBack.setVisibility(View.GONE);
                    loginRegister.setVisibility(View.VISIBLE);
                    currentFragment = LoginPageFragment.newInstance(R.layout.fragment_login_main);
                    break;
                case CODE: // 验证码页
                    loginBack.setVisibility(View.VISIBLE);
                    loginRegister.setVisibility(View.GONE);
                    currentFragment = LoginPageFragment.newInstance(R.layout.fragment_login_code, phoneNum);
                    break;
                case BIND_PHONE: // 绑定手机页
                    loginBack.setVisibility(View.VISIBLE);
                    loginRegister.setVisibility(View.GONE);
                    currentFragment = LoginPageFragment.newInstance(R.layout.fragment_login_bind_phone);
                    break;
                case PWD: // 密码登录页
                    loginBack.setVisibility(View.VISIBLE);
                    loginRegister.setVisibility(View.GONE);
                    currentFragment = LoginPageFragment.newInstance(R.layout.fragment_login_pwd);
                    break;
                case FIND_PWD: // 找回密码页
                    loginBack.setVisibility(View.VISIBLE);
                    loginRegister.setVisibility(View.GONE);
                    currentFragment = LoginPageFragment.newInstance(R.layout.fragment_login_find);
                    break;
                case SET_PWD: // 设置密码页
                    loginBack.setVisibility(View.VISIBLE);
                    loginRegister.setVisibility(View.GONE);
                    currentFragment = LoginPageFragment.newInstance(R.layout.fragment_login_set_pwd);
                    break;
                case REGISTER: // 注册页
                    loginBack.setVisibility(View.VISIBLE);
                    loginRegister.setVisibility(View.GONE);
                    currentFragment = LoginPageFragment.newInstance(R.layout.fragment_login_register);
                    break;
                case WE_CHAT: // 微信登录页
                    loginBack.setVisibility(View.VISIBLE);
                    loginRegister.setVisibility(View.GONE);
                    currentFragment = LoginPageFragment.newInstance(R.layout.fragment_login_we_chat);
                    break;
            }
            if (currentFragment != null) {
                getSupportFragmentManager().beginTransaction().add(R.id.login_container, currentFragment, tag).commit();
            }
        } else {
            switch (tag) {
                case MAIN:
                    loginBack.setVisibility(View.GONE);
                    loginRegister.setVisibility(View.VISIBLE);
                    break;
                case CODE:
                case REGISTER:
                case PWD:
                case FIND_PWD:
                case SET_PWD:
                case WE_CHAT:
                case BIND_PHONE:
                    loginBack.setVisibility(View.VISIBLE);
                    loginRegister.setVisibility(View.GONE);
                    break;
            }
            getSupportFragmentManager().beginTransaction().show(currentFragment).commit();
        }
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        Log.d(TAG, "onMainThreadResponse: success --- " + success);
    }

    /**
     * 如果软键盘弹出，就关闭软键盘
     */
    private void toggleSoftKeyboard() {
        if (imm != null && imm.isActive()) {
            View view = getCurrentFocus();
            if (view != null) {
                IBinder iBinder = view.getWindowToken();
                imm.hideSoftInputFromWindow(iBinder, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
}
