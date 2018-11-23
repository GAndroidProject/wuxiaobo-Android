package com.xiaoe.shop.wxb.business.setting.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.xiaoe.common.app.CommonUserInfo;
import com.xiaoe.common.app.Global;
import com.xiaoe.common.db.LoginSQLiteCallback;
import com.xiaoe.common.db.SQLiteUtil;
import com.xiaoe.common.entitys.LoginUser;
import com.xiaoe.common.entitys.LoginUserEntity;
import com.xiaoe.common.entitys.UpdateMineMsgEvent;
import com.xiaoe.network.NetworkCodes;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.network.requests.SettingPersonItemRequest;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.XiaoeActivity;
import com.xiaoe.shop.wxb.business.setting.presenter.SettingPresenter;
import com.xiaoe.shop.wxb.utils.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;

import java.sql.RowId;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingPersonItemActivity extends XiaoeActivity {

    private static final String TAG = "PersonItemActivity";

    @BindView(R.id.person_edit_toolbar)
    Toolbar personEditToolbar;
    @BindView(R.id.person_edit_back)
    ImageView personEditBack;
    @BindView(R.id.person_edit_title)
    TextView personEditTitle;
    @BindView(R.id.person_edit_content)
    EditText personEditContent;
    @BindView(R.id.person_edit_submit)
    Button personEditSubmit;

    Intent intent;
    int position;

    String apiToken;
    SettingPresenter settingPresenter;
    SQLiteUtil loginSQLiteUtil;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_setting_person_item);
        ButterKnife.bind(this);


        List<LoginUser> loginMsg = SQLiteUtil.init(this, new LoginSQLiteCallback()).query(LoginSQLiteCallback.TABLE_NAME_USER, "select * from " + LoginSQLiteCallback.TABLE_NAME_USER, null);
        apiToken = loginMsg.get(0).getApi_token();

        intent = getIntent();

        // 网络请求
        settingPresenter = new SettingPresenter(this);

        //状态栏颜色字体(白底黑字)修改 Android6.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setStatusBarColor(getWindow(), Color.parseColor(Global.g().getGlobalColor()), View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        personEditToolbar.setPadding(0, StatusBarUtil.getStatusBarHeight(this), 0, 0);
        loginSQLiteUtil = SQLiteUtil.init(this, new LoginSQLiteCallback());

        initData();
        initListener();
    }

    private void initData() {
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");
        position = intent.getIntExtra("position", 0);
        Log.d(TAG, "initData: position ----- " + position);
        personEditTitle.setText(title);
        if (TextUtils.isEmpty(content)) {
            personEditContent.setHint("请输入" + title);
        } else {
            personEditContent.setText(content);
        }
        personEditContent.setSelection(personEditContent.getText().length());
    }

    String inputContent;
    private void initListener() {
        personEditBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        personEditContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 0) {
                    personEditSubmit.setClickable(true);
                    personEditSubmit.setAlpha(1);
                } else {
                    personEditSubmit.setClickable(false);
                    personEditSubmit.setAlpha(0.8f);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        personEditSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 请求保存接口
                String title = personEditTitle.getText().toString();
                inputContent = personEditContent.getText().toString();
                switch (title) {
                    case "昵称":
                        settingPresenter.updateWxNickname(apiToken, inputContent);
                        break;
                    case "真实姓名":
                        settingPresenter.updateName(apiToken, inputContent);
                        break;
                    case "性别":
                        settingPresenter.updateGender(apiToken, inputContent);
                        break;
                    case "生日":
                        settingPresenter.updateBirth(apiToken, inputContent);
                        break;
                    case "手机":
                        // TODO: 更新手机号
                        Toast("没做");
//                        settingPresenter.updatePhone(apiToken, content);
                        break;
                    case "地址":
                        settingPresenter.updateAddress(apiToken, inputContent);
                        break;
                    case "公司":
                        settingPresenter.updateCompany(apiToken, inputContent);
                        break;
                    case "职位":
                        settingPresenter.updateJob(apiToken, inputContent);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        JSONObject result = (JSONObject) entity;
        if (success) {
            if (iRequest instanceof SettingPersonItemRequest) {
                int code = result.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) {
                    Toast("修改成功");
                    List<LoginUser> resultList = loginSQLiteUtil.query(LoginSQLiteCallback.TABLE_NAME_USER, "select * from " + LoginSQLiteCallback.TABLE_NAME_USER, null);
                    String rowId = resultList.get(0).getRowId();
                    String sql = "update " + LoginSQLiteCallback.TABLE_NAME_USER +
                            " set " + LoginUserEntity.COLUMN_NAME_WX_NICKNAME +
                            " = '" + inputContent +
                            "' where " + LoginUserEntity.COLUMN_NAME_ROW_ID +
                            " = '" + rowId + "'";
                    loginSQLiteUtil.execSQL(sql);
                    UpdateMineMsgEvent updateMineMsgEvent = new UpdateMineMsgEvent();
                    updateMineMsgEvent.setWxNickName(inputContent);
                    EventBus.getDefault().post(updateMineMsgEvent);
                    // 更新数据库
                    Intent intent = new Intent();
                    intent.putExtra("content", inputContent);
                    intent.putExtra("position", position);
                    SettingPersonItemActivity.this.setResult(RESULT_OK, intent);
                    SettingPersonItemActivity.this.finish();
                } else if (code == NetworkCodes.CODE_PERSON_PARAM_LOSE) {
                    Log.d(TAG, "onMainThreadResponse: 必选字段缺失");
                } else if (code == NetworkCodes.CODE_PERSON_PARAM_UNUSEFUL) {
                    Log.d(TAG, "onMainThreadResponse: 字段格式无效");
                } else if (code == NetworkCodes.CODE_PERSON_PHONE_REPEAT) {
                    Log.d(TAG, "onMainThreadResponse: 手机号重复了");
                } else if (code == NetworkCodes.CODE_PERSON_NOT_FOUND) {
                    Log.d(TAG, "onMainThreadResponse: 当前用户不存在");
                }
            }
        } else {
            Log.d(TAG, "onMainThreadResponse: request fail...");
        }
    }
}
