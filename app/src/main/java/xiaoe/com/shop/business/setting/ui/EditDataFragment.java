package xiaoe.com.shop.business.setting.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import com.facebook.drawee.view.SimpleDraweeView;
import com.suke.widget.SwitchButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import xiaoe.com.common.entitys.SettingItemInfo;
import xiaoe.com.common.interfaces.OnItemClickWithPosListener;
import xiaoe.com.common.interfaces.OnItemClickWithSettingItemInfoListener;
import xiaoe.com.common.utils.Dp2Px2SpUtil;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseFragment;
import xiaoe.com.shop.business.setting.presenter.LinearDividerDecoration;
import xiaoe.com.shop.business.setting.presenter.SettingRecyclerAdapter;
import xiaoe.com.shop.business.setting.presenter.SettingTimeCount;
import xiaoe.com.shop.widget.CodeVerifyView;

public class EditDataFragment extends BaseFragment implements OnItemClickWithSettingItemInfoListener {

    private static final String TAG = "EditDataFragment";

    private Unbinder unbinder;
    private Context mContext;
    private boolean destroyView = false;
    protected View viewWrap;

    private int layoutId = -1;
    private List<SettingItemInfo> itemList;

    private SettingAccountActivity settingAccountActivity;

    RecyclerView accountRecycler; // 账号设置 recycler
    RecyclerView aboutContent; // 关于 recycler

    // 软键盘
    InputMethodManager imm;

    public static EditDataFragment newInstance(int layoutId) {
        EditDataFragment editDataFragment = new EditDataFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("layoutId", layoutId);
        editDataFragment.setArguments(bundle);
        return editDataFragment;
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
        unbinder = ButterKnife.bind(this, viewWrap);
        mContext = getContext();
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        settingAccountActivity = (SettingAccountActivity) getActivity();
        return viewWrap;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (layoutId != -1) {
            initView();
        }
    }

    private void initView() {
        switch (layoutId) {
            case R.layout.fragment_account:
                initAccountFragment();
                break;
            case R.layout.fragment_message:
                initMessageFragment();
                break;
            case R.layout.fragment_suggestion:
                initSuggestionFragment();
                break;
            case R.layout.fragment_about:
                initAboutFragment();
                break;
            case R.layout.fragment_phone:
                initPhoneFragment();
                break;
            case R.layout.fragment_pwd_now:
                initPwdNowFragment();
                break;
            case R.layout.fragment_pwd_new:
                initPwdNewFragment();
                break;
            case R.layout.fragment_phone_num:
                initPhoneNumFragment();
                break;
        }
    }

    // 账号设置布局
    private void initAccountFragment() {
        // 账号设置页面假数据
        itemList = new ArrayList<>();
        accountRecycler = (RecyclerView) viewWrap.findViewById(R.id.account_content);
        SettingItemInfo changePwd = new SettingItemInfo("修改密码", "", "");
        SettingItemInfo changePhone = new SettingItemInfo("更换手机号", "" ,"188*****1234");
        SettingItemInfo bindWeChat = new SettingItemInfo("绑定微信", "", "未绑定");
        itemList.add(changePwd);
        itemList.add(changePhone);
        itemList.add(bindWeChat);
        SettingRecyclerAdapter settingRecyclerAdapter = new SettingRecyclerAdapter(getActivity(), itemList);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        LinearDividerDecoration linearDividerDecoration = new LinearDividerDecoration(getActivity(), R.drawable.recycler_divider_line, Dp2Px2SpUtil.dp2px(getActivity(), 20));
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        settingRecyclerAdapter.setOnItemClickWithSettingItemInfoListener(this);
        accountRecycler.setLayoutManager(llm);
        accountRecycler.addItemDecoration(linearDividerDecoration);
        accountRecycler.setAdapter(settingRecyclerAdapter);
    }

    // 推送消息布局
    private void initMessageFragment() {
        SwitchButton messageSwitch = (SwitchButton) viewWrap.findViewById(R.id.message_switch);
        // TODO: 获取推送消息开关状态
//        messageSwitch.setChecked(true);
        messageSwitch.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(getActivity(), "打开消息推送", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "关闭消息推送", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 建议布局
    private void initSuggestionFragment() {
        EditText editText = (EditText) viewWrap.findViewById(R.id.suggest_content);
        final TextView numText = (TextView) viewWrap.findViewById(R.id.suggest_num);
        Button btnSubmit = (Button) viewWrap.findViewById(R.id.suggest_submit);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int tempLength = s.length();
                numText.setText(String.format(getResources().getString(R.string.setting_suggestion_num), tempLength));
            }
        });
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 网络请求，保存意见反馈
                settingAccountActivity.replaceFragment(SettingAccountActivity.MAIN);
            }
        });
    }

    // 关于布局
    private void initAboutFragment() {
        SimpleDraweeView aboutLogo = (SimpleDraweeView) viewWrap.findViewById(R.id.about_logo);
        TextView aboutDesc = (TextView) viewWrap.findViewById(R.id.about_desc);
        aboutDesc.setText("吴晓波 版权所有");
        aboutContent = (RecyclerView) viewWrap.findViewById(R.id.about_content);
        // 关于布局假数据
        aboutLogo.setImageURI("http://pic24.nipic.com/20121008/6298817_164144357169_2.jpg");
        itemList = new ArrayList<>();
        SettingItemInfo connectUs = new SettingItemInfo("联系我们", "", "462436501@gmail.com");
        SettingItemInfo service = new SettingItemInfo("服务协议", "" ,"");
        itemList.add(connectUs);
        itemList.add(service);
        SettingRecyclerAdapter settingRecyclerAdapter = new SettingRecyclerAdapter(getActivity(), itemList);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        LinearDividerDecoration linearDividerDecoration = new LinearDividerDecoration(getActivity(), R.drawable.recycler_divider_line, Dp2Px2SpUtil.dp2px(getActivity(), 20));
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        settingRecyclerAdapter.setOnItemClickWithSettingItemInfoListener(this);
        aboutContent.setLayoutManager(llm);
        aboutContent.addItemDecoration(linearDividerDecoration);
        aboutContent.setAdapter(settingRecyclerAdapter);
    }

    private void initPhoneFragment() {
        TextView phoneNum = (TextView) viewWrap.findViewById(R.id.phone_num);
        Button phoneSubmit = (Button) viewWrap.findViewById(R.id.phone_submit);
        // 初始化数据
        String num = "18814182578";
        phoneNum.setText(num);
        phoneSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingAccountActivity.accountTitle.setText("修改密码");
                settingAccountActivity.replaceFragment(SettingAccountActivity.PHONE_NUM);
            }
        });
    }

    private void initPwdNowFragment() {
        final EditText nowPwd = (EditText) viewWrap.findViewById(R.id.pwd_now_content);
        Button nowBtn = (Button) viewWrap.findViewById(R.id.pwd_now_next);
        final TextView nowPhone = (TextView) viewWrap.findViewById(R.id.pwd_now_phone);

        nowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 判断密码是否正确
                String tempPwd = "123";
                if (tempPwd.equals(nowPwd.getText().toString())) { // 密码正确
                    // 跳转到修改页面
                    if (imm != null && imm.isActive()) {
                        imm.hideSoftInputFromWindow(nowPwd.getWindowToken(), 0);
                    }
                    nowPwd.setText("");
                    settingAccountActivity.replaceFragment(SettingAccountActivity.PWD_NEW);
                } else {
                    Toast.makeText(getActivity(), "密码错误", Toast.LENGTH_SHORT).show();
                }
            }
        });

        nowPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nowPwd.setText("");
                settingAccountActivity.replaceFragment(SettingAccountActivity.PHONE_NUM);
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initPwdNewFragment() {
        final EditText newPwd = (EditText) viewWrap.findViewById(R.id.pwd_new_content);
        Button newBtn = (Button) viewWrap.findViewById(R.id.pwd_new_submit);

        newPwd.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 拿到画在尾部的 drawable
                Drawable drawable = newPwd.getCompoundDrawables()[2];
                // 如果没有不处理
                if (drawable == null) {
                    return false;
                }
                // 如果不是抬起事件，不处理
                if (event.getAction() != MotionEvent.ACTION_UP) {
                    return false;
                }
                if (event.getX() > newPwd.getWidth() - newPwd.getPaddingEnd() - drawable.getIntrinsicWidth()) {
                    if (newPwd.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD + InputType.TYPE_CLASS_TEXT) {
                        // 设置隐藏按钮
                        Drawable right = getActivity().getResources().getDrawable(R.mipmap.icon_hide);
                        right.setBounds(0, 0, right.getMinimumWidth(), drawable.getMinimumHeight());
                        newPwd.setCompoundDrawables(null, null, right, null);
                        newPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    } else {
                        // 设置显示按钮
                        Drawable right = getActivity().getResources().getDrawable(R.mipmap.icon_show);
                        right.setBounds(0, 0, right.getMinimumWidth(), drawable.getMinimumHeight());
                        newPwd.setCompoundDrawables(null, null, right, null);
                        newPwd.setInputType(InputType.TYPE_CLASS_TEXT |InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    }
                    return true;
                }
                return false;
            }
        });

        newBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (newPwd.getText().toString().length() < 6) {
                    Toast.makeText(getActivity(), "密码长度不能少于6", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (imm != null && imm.isActive()) {
                    imm.hideSoftInputFromWindow(newPwd.getWindowToken(), 0);
                }
                // TODO: 请求完成修改接口，然后回到设置主页
                newPwd.setText("");
                settingAccountActivity.accountTitle.setText("设置");
                settingAccountActivity.replaceFragment(SettingAccountActivity.MAIN);
            }
        });
    }

    protected CodeVerifyView phoneNumContent;

    private void initPhoneNumFragment() {
        final SettingTimeCount settingTimeCount = new SettingTimeCount(getActivity(), 60000, 1000, viewWrap);

        TextView phoneNumTitle = (TextView) viewWrap.findViewById(R.id.phone_num_title);
        phoneNumContent = (CodeVerifyView) viewWrap.findViewById(R.id.phone_num_content);
        final TextView phoneNumDesc = (TextView) viewWrap.findViewById(R.id.phone_num_desc);
        phoneNumTitle.setText(String.format(getActivity().getResources().getString(R.string.setting_phone_num_title), "18814182578"));
        phoneNumDesc.setText("获取验证码");
        phoneNumContent.setOnCodeFinishListener(new CodeVerifyView.OnCodeFinishListener() {
            @Override
            public void onComplete(String content) {
                // content 为用户输入的验证码
                // TODO: 获取后台返回的验证码与用户输入的作比较，默认 1234
                if (content.equals("1234")) {
                    phoneNumContent.clearAllEditText();
                    settingAccountActivity.accountTitle.setText("账号设置");
                    // 关掉 SettingTimeCount
                    settingTimeCount.cancel();
                    settingAccountActivity.replaceFragment(SettingAccountActivity.ACCOUNT);
                    if (imm != null && imm.isActive()) {
                        View view = getActivity().getCurrentFocus();
                        if (view != null) {
                            IBinder iBinder = view.getWindowToken();
                            imm.hideSoftInputFromWindow(iBinder, InputMethodManager.HIDE_NOT_ALWAYS);
                        }
                    }
                } else {
                    phoneNumContent.setErrorBg(R.drawable.cv_error_bg);
                }
            }
        });
        phoneNumDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingTimeCount.start();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        destroyView = true;
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public void onItemClick(View view, SettingItemInfo itemInfo) {
        // 账号设置的点击事件
        if (accountRecycler == view.getParent()) {
            switch (itemList.indexOf(itemInfo)) {
                case 0:
                    // 修改密码
                    settingAccountActivity.replaceFragment(SettingAccountActivity.PWD_NOW);
                    break;
                case 1:
                    // 更换手机号
                    settingAccountActivity.replaceFragment(SettingAccountActivity.PHONE);
                    break;
                case 2:
                    // 绑定微信
                    Toast.makeText(getActivity(), "绑定微信", Toast.LENGTH_SHORT).show();
                    break;
            }
        } else if (aboutContent == view.getParent()) { // 关于的点击事件
            switch (itemList.indexOf(itemInfo)) {
                case 0:
                    // 联系我们
                    Toast.makeText(getActivity(), "联系我们", Toast.LENGTH_SHORT).show();
                    break;
                case 1:
                    // 服务协议
                    Toast.makeText(getActivity(), "服务协议", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
