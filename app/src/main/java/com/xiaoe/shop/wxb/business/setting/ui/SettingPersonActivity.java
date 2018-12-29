package com.xiaoe.shop.wxb.business.setting.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.xiaoe.common.app.Global;
import com.xiaoe.common.app.XiaoeApplication;
import com.xiaoe.common.db.LoginSQLiteCallback;
import com.xiaoe.common.db.SQLiteUtil;
import com.xiaoe.common.entitys.LoginUser;
import com.xiaoe.common.entitys.SettingItemInfo;
import com.xiaoe.common.interfaces.OnItemClickWithSettingItemInfoListener;
import com.xiaoe.common.utils.DateFormat;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.common.utils.NetUtils;
import com.xiaoe.network.NetworkCodes;
import com.xiaoe.common.entitys.NetworkStateResult;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.network.requests.SettingPersonItemRequest;
import com.xiaoe.network.requests.SettingPseronMsgRequest;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.XiaoeActivity;
import com.xiaoe.shop.wxb.business.setting.presenter.LinearDividerDecoration;
import com.xiaoe.shop.wxb.business.setting.presenter.SettingPresenter;
import com.xiaoe.shop.wxb.business.setting.presenter.SettingRecyclerAdapter;
import com.xiaoe.shop.wxb.utils.StatusBarUtil;
import com.xiaoe.shop.wxb.utils.ToastUtils;
import com.xiaoe.shop.wxb.widget.StatusPagerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.internal.DebouncingOnClickListener;

public class SettingPersonActivity extends XiaoeActivity implements OnItemClickWithSettingItemInfoListener {

    private static final String TAG = "SettingPersonActivity";

    @BindView(R.id.setting_person_tool_bar)
    Toolbar settingToolbar;
    @BindView(R.id.setting_back)
    ImageView settingBack;
    @BindView(R.id.setting_person_content)
    RecyclerView settingContentView;
    @BindView(R.id.setting_person_loading)
    StatusPagerView settingLoading;

    SettingRecyclerAdapter settingRecyclerAdapter;
    // 从我的页面获取图片路径
    Intent intent;
    List<SettingItemInfo> dataList;
    private static final int REQUEST_CODE = 101;

    View actionSheet;
    // 性别选择器的内容
    TextView actionSheetMan;
    TextView actionSheetWoman;
    TextView actionSheetCancel;
    Dialog dialog;

    // 时间选择器
    TimePickerView timePickerView;

    String apiToken;
    SettingPresenter settingPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_setting_person);
        ButterKnife.bind(this);

        intent = getIntent();

        settingLoading.setLoadingState(View.VISIBLE);

        List<LoginUser> loginMsg = SQLiteUtil.init(this, new LoginSQLiteCallback()).query(LoginSQLiteCallback.TABLE_NAME_USER, "select * from " + LoginSQLiteCallback.TABLE_NAME_USER, null);
        if (loginMsg.size() == 1) {
            apiToken = loginMsg.get(0).getApi_token();

            // 网络请求
            settingPresenter = new SettingPresenter(this);
            settingPresenter.requestPersonData(apiToken, true);
        }

        //状态栏颜色字体(白底黑字)修改 Android6.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setStatusBarColor(getWindow(), Color.parseColor(Global.g().getGlobalColor()), View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        settingToolbar.setPadding(0, StatusBarUtil.getStatusBarHeight(this), 0, 0);

        initData();
        initListener();
    }

    private void initData() {
        dataList = new ArrayList<>();
    }

    private void initListener() {
        settingBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        JSONObject result = (JSONObject) entity;
        if (success) {
            if (iRequest instanceof SettingPseronMsgRequest) {
                int code = result.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) {
                    JSONObject data = (JSONObject) result.get("data");
                    initPersonData(data);
                } else if (code == NetworkCodes.CODE_PERSON_PARAM_LOSE) {
                    Log.d(TAG, "onMainThreadResponse: 必选字段缺失");
                } else if (code == NetworkCodes.CODE_PERSON_PARAM_UNUSEFUL) {
                    Log.d(TAG, "onMainThreadResponse: 字段格式无效");
                } else if (code == NetworkCodes.CODE_PERSON_NOT_FOUND) {
                    Log.d(TAG, "onMainThreadResponse: 当前用户不存在");
                }
            } else if (iRequest instanceof SettingPersonItemRequest) {

            }
        } else {
            Log.d(TAG, "onMainThreadResponse: request fail");
            if (result != null) {
                int code = result.getInteger("code");
                if (NetworkStateResult.ERROR_NETWORK == code) {
                    if (dataList != null && dataList.size() == 0) {
                        settingLoading.setPagerState(StatusPagerView.FAIL, StatusPagerView.FAIL_CONTENT, R.mipmap.error_page);
                    }
                }
            }
        }
    }

    // 初始化用户信息界面
    private void initPersonData(JSONObject data) {
        String wxNickname = data.getString("wx_nickname");
        String wxAvatar = data.getString("wx_avatar");
        String wxAvatarWx = data.getString("wx_avatar_wx");
        String wxName = data.getString("wx_name");
        String wxGender = data.getString("wx_gender");
        String birth = data.getString("birth");
        String address = data.getString("address");
        String job = data.getString("job");
        String company = data.getString("company");
        String phone = data.getString("phone");

        String imgUrl = intent.getStringExtra("avatar");
        if (TextUtils.isEmpty(imgUrl)) {
            // 默认图片
            imgUrl = "res:///" + R.mipmap.default_avatar;
        } else {
            imgUrl = intent.getStringExtra("avatar");
        }
        String gender = null;
        switch (wxGender) {
            case "0":
                gender = "";
                break;
            case "1":
                gender = getString(R.string.action_sheet_man);
                break;
            case "2":
                gender = getString(R.string.action_sheet_woman);
                break;
            default:
                break;
        }
        SettingItemInfo itemAvatar = new SettingItemInfo(getString(R.string.avatar_text), imgUrl, "");
        SettingItemInfo itemNickname = new SettingItemInfo(getString(R.string.nickname), "", wxNickname);
        SettingItemInfo itemName = new SettingItemInfo(getString(R.string.real_name), "", wxName);
        SettingItemInfo itemGender = new SettingItemInfo(getString(R.string.gender), "", gender);
        SettingItemInfo itemBirthday = new SettingItemInfo(getString(R.string.birthday), "", birth);
        SettingItemInfo itemPhone = new SettingItemInfo(getString(R.string.phone_number), "", phone);
        SettingItemInfo itemAddress = new SettingItemInfo(getString(R.string.address), "", address);
        SettingItemInfo itemCompany = new SettingItemInfo(getString(R.string.company), "", company);
        SettingItemInfo itemJob = new SettingItemInfo(getString(R.string.post), "", job);

        dataList.add(itemAvatar);
        dataList.add(itemNickname);
        dataList.add(itemName);
        dataList.add(itemGender);
        dataList.add(itemBirthday);
        dataList.add(itemPhone);
        dataList.add(itemAddress);
        dataList.add(itemCompany);
        dataList.add(itemJob);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        settingContentView.setLayoutManager(llm);
        settingRecyclerAdapter = new SettingRecyclerAdapter(this, dataList);
        settingRecyclerAdapter.setOnItemClickWithSettingItemInfoListener(this);
        settingContentView.setAdapter(settingRecyclerAdapter);
        settingContentView.addItemDecoration(new LinearDividerDecoration(this, LinearLayout.VERTICAL, R.drawable.recycler_divider_line, Dp2Px2SpUtil.dp2px(this, 20)));

        settingLoading.setVisibility(View.GONE);
    }

    @Override
    public void onItemClick(View view, SettingItemInfo itemInfo) {
        if (getString(R.string.avatar_text).equals(itemInfo.getItemTitle())
                || getString(R.string.phone_number).equals(itemInfo.getItemTitle())) {
            return;
        }
        if (getString(R.string.gender).equals(itemInfo.getItemTitle())) {
            initActionSheetGender(dataList.indexOf(itemInfo));
        } else if (getString(R.string.birthday).equals(itemInfo.getItemTitle())) {
            initActionSheetDate(dataList.indexOf(itemInfo));
        } else {
            Intent intent = new Intent(this, SettingPersonItemActivity.class);
            intent.putExtra("title", itemInfo.getItemTitle());
            intent.putExtra("content", itemInfo.getItemContent());
            intent.putExtra("position", dataList.indexOf(itemInfo));
            startActivityForResult(intent, REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                int position = data.getIntExtra("position", -1);
                String content = data.getStringExtra("content");
                if (position != -1) {
                    dataList.get(position).setItemContent(content);
                    settingRecyclerAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    // 初始化底部性别选择器
    private void initActionSheetGender(int position) {
        dialog = new Dialog(this, R.style.ActionSheetDialogStyle);
        actionSheet = LayoutInflater.from(this).inflate(R.layout.action_sheet_gender, null);
        actionSheetMan = (TextView) actionSheet.findViewById(R.id.action_sheet_man);
        actionSheetWoman = (TextView) actionSheet.findViewById(R.id.action_sheet_woman);
        actionSheetCancel = (TextView) actionSheet.findViewById(R.id.action_sheet_cancel);
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            Display display = windowManager.getDefaultDisplay();
            // 设置Dialog最小宽度为屏幕宽度,实现占满全屏
            actionSheet.setMinimumWidth(display.getWidth());
        }
        // 将布局设置给 dialog
        dialog.setContentView(actionSheet);
        // 获取当前 activity 所在的窗体
        Window dialogWindow = dialog.getWindow();
        // 设置 dialog 从窗体底部弹出
        if (dialogWindow != null) {
            dialogWindow.setGravity(Gravity.BOTTOM);
            // 可以给 dialog 所在的设置属性
            // 获得窗体的属性
//            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            // lp.y = 20; // 设置Dialog距离底部的距离
            //    将属性设置给窗体
//            dialogWindow.setAttributes(lp);
            // 显示对话框
//            dialogWindow.setBackgroundDrawableResource(R.drawable.action_sheet_bg);
            initActionSheetGenderListener(position);
            dialog.show();
        }
    }

    // 初始化性别底部选择器监听
    private void initActionSheetGenderListener(final int position) {
        actionSheetMan.setOnClickListener(new DebouncingOnClickListener() {
            @Override
            public void doClick(View v) {
                if (NetUtils.hasNetwork(XiaoeApplication.applicationContext) && NetUtils.hasDataConnection(XiaoeApplication.applicationContext)) {
                    settingPresenter.updateGender(apiToken, getString(R.string.action_sheet_man));
                    dataList.get(position).setItemContent(getString(R.string.action_sheet_man));
                    settingRecyclerAdapter.notifyDataSetChanged();
                } else {
                    ToastUtils.show(mContext, getString(R.string.network_error_text));
                }
                dialog.dismiss();
            }
        });

        actionSheetWoman.setOnClickListener(new DebouncingOnClickListener() {
            @Override
            public void doClick(View v) {
                if (NetUtils.hasNetwork(XiaoeApplication.applicationContext) && NetUtils.hasDataConnection(XiaoeApplication.applicationContext)) {
                    settingPresenter.updateGender(apiToken, getString(R.string.action_sheet_woman));
                    dataList.get(position).setItemContent(getString(R.string.action_sheet_woman));
                    settingRecyclerAdapter.notifyDataSetChanged();
                } else {
                    ToastUtils.show(mContext, getString(R.string.network_error_text));
                }
                dialog.dismiss();
            }
        });

        actionSheetCancel.setOnClickListener(new DebouncingOnClickListener() {
            @Override
            public void doClick(View v) {
                dialog.dismiss();
            }
        });
    }

    // 初始化日期底部选择器
    private void initActionSheetDate(final int position) {
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();

        // 设置选择时间的开始和结束日期
        startDate.set(1950, 0, 1);
        int year = endDate.get(Calendar.YEAR);
        int month = endDate.get(Calendar.MONTH);
        int day = endDate.get(Calendar.DAY_OF_MONTH);
        endDate.set(year, month, day);

        timePickerView = new TimePickerBuilder(SettingPersonActivity.this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                if (NetUtils.hasNetwork(XiaoeApplication.applicationContext) && NetUtils.hasDataConnection(XiaoeApplication.applicationContext)) {
                    String dateStr = DateFormat.date2String(date);
                    settingPresenter.updateBirth(apiToken, dateStr);
                    dataList.get(position).setItemContent(dateStr);
                    settingRecyclerAdapter.notifyDataSetChanged();
                } else {
                    ToastUtils.show(mContext, getString(R.string.network_error_text));
                }
            }
        }).setContentTextSize(18)
                .setTitleColor(getResources().getColor(R.color.main_title_color))
                .setTitleSize(16)
                .setOutSideCancelable(true)
                .setSubmitColor(getResources().getColor(R.color.main_title_color))
                .setCancelColor(getResources().getColor(R.color.main_title_color))
                .setTitleBgColor(getResources().getColor(R.color.white))
                .isCyclic(false)
                .setDate(endDate) // 可以设置选择的时间
                .setRangDate(startDate, endDate)
                .build();
        timePickerView.show();
    }
}
