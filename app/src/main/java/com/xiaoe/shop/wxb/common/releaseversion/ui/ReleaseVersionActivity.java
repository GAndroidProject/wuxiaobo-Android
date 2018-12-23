package com.xiaoe.shop.wxb.common.releaseversion.ui;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.xiaoe.common.app.Global;
import com.xiaoe.common.entitys.ReleaseVersionReq;
import com.xiaoe.common.entitys.VersionLog;
import com.xiaoe.network.NetworkCodes;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.network.requests.ReleaseVersionRequest;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.adapter.common.VersionLogAdapter;
import com.xiaoe.shop.wxb.base.XiaoeActivity;
import com.xiaoe.shop.wxb.common.releaseversion.presenter.ReleaseVersionPresenter;
import com.xiaoe.shop.wxb.utils.StatusBarUtil;
import com.xiaoe.shop.wxb.utils.ToastUtils;
import com.xiaoe.shop.wxb.widget.LinearLayoutForListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author flynnWang
 * @date 2018/12/21
 * <p>
 * 描述：
 */
public class ReleaseVersionActivity extends XiaoeActivity implements Handler.Callback {

    @BindView(R.id.sp_release_environment)
    Spinner spReleaseEnvironment;
    @BindView(R.id.sp_release_type)
    Spinner spReleaseType;
    @BindView(R.id.sp_release_update_mode)
    Spinner spReleaseUpdateMode;
    @BindView(R.id.et_shop_id)
    EditText etShopId;
    @BindView(R.id.et_version)
    EditText etVersion;
    @BindView(R.id.et_download_url)
    EditText etDownloadUrl;
    @BindView(R.id.et_audit_version)
    EditText etAuditVersion;
    @BindView(R.id.lv_version_log)
    LinearLayoutForListView lvVersionLog;
    @BindView(R.id.tv_request_url)
    TextView tvRequestUrl;
    @BindView(R.id.btn_release)
    Button btnRelease;

    private static final String TAG = "ReleaseVersionActivity";

    /**
     * 请求地址
     */
    private String[] requestUrls = new String[]{"http://app-server.inside.xiaoeknow.com/api/",
            "https://app-server.xiaoeknow.com/api/",
            "https://app-server.xiaoeknow.com/api/"};

    /**
     * 店铺 id
     */
    private String[] shopIds = new String[]{"appiOW1KfWe9943", "app38itOR341547", "appe0MEs6qX8480"};

    private String url;

    private ReleaseVersionReq releaseVersionReq = new ReleaseVersionReq();

    private List<VersionLog> msgItemList = new ArrayList<>();
    private VersionLogAdapter optionsAdapter;

    private ReleaseVersionPresenter releaseVersionPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_release_version);
        ButterKnife.bind(this);

        initView();
        initData();
        initListener();
    }

    private void initView() {
        // 状态栏颜色字体(白底黑字)修改 Android6.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setStatusBarColor(getWindow(), Color.parseColor(Global.g().getGlobalColor()), View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    private void initData() {
        getInitVoteItem();

        optionsAdapter = new VersionLogAdapter(this, new Handler(this), msgItemList);
        lvVersionLog.setAdapter(optionsAdapter);
        lvVersionLog.bindLinearLayoutV();

        releaseVersionPresenter = new ReleaseVersionPresenter(this);
    }

    private void initListener() {
        spReleaseEnvironment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                    case 1:
                    case 2:
                        url = requestUrls[i];
                        tvRequestUrl.setText(url);
                        etShopId.setText(shopIds[i]);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d(TAG, "onNothingSelected: url " + url);
            }
        });
        spReleaseType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                    case 1:
                        releaseVersionReq.setType(String.valueOf(i + 1));
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d(TAG, "onNothingSelected: type " + releaseVersionReq.getType());
            }
        });
        spReleaseUpdateMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                    case 1:
                    case 2:
                        releaseVersionReq.setUpdate_mode(i);
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Log.d(TAG, "onNothingSelected: update_mode " + releaseVersionReq.getUpdate_mode());
            }
        });

        btnRelease.setOnClickListener(view -> {
            if (checkNull()) {
                return;
            }

            showAlertDialog("你确定要 发布 版本更新提示吗？", true);
        });
    }

    private boolean checkNull() {
        if (TextUtils.isEmpty(etShopId.getText().toString().trim())) {
            ToastUtils.show(mContext, "请输入店铺 id");
            etShopId.requestFocus();
            return true;
        }
        if (TextUtils.isEmpty(etVersion.getText().toString().trim())) {
            ToastUtils.show(mContext, "请输入版本名称");
            etVersion.requestFocus();
            return true;
        }
        if (TextUtils.isEmpty(etDownloadUrl.getText().toString().trim())) {
            ToastUtils.show(mContext, "请输入下载地址");
            etDownloadUrl.requestFocus();
            return true;
        }

        List<String> messages = new ArrayList<>();
        for (VersionLog log : msgItemList) {
            if (!TextUtils.isEmpty(log.getContent())) {
                messages.add(log.getContent());
            }
        }
        if (messages.size() <= 0) {
            ToastUtils.show(mContext, "请输入版本更新说明");
            return true;
        }

        return false;
    }

    private void getInitVoteItem() {
        VersionLog voteItem1 = new VersionLog();
        msgItemList.add(voteItem1);
        VersionLog voteItem2 = new VersionLog();
        msgItemList.add(voteItem2);
    }

    @Override
    public boolean handleMessage(Message message) {
        int index = message.arg1;
        switch (message.what) {
            case 1:
                VersionLog voteItem = new VersionLog();
                msgItemList.add(voteItem);
                optionsAdapter.notifyDataSetChanged();
                lvVersionLog.bindLinearLayoutV();
                break;
            case 2:
                msgItemList.remove(index);
                optionsAdapter.notifyDataSetChanged();
                lvVersionLog.bindLinearLayoutV();
                break;
            case 3:
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        JSONObject result = (JSONObject) entity;
        if (success) {
            if (iRequest instanceof ReleaseVersionRequest) {
                int code = result.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) {
                    ToastUtils.show(mContext, result.get("msg") + " 发布成功");
                    btnRelease.postDelayed(this::finish, 500);
                } else {
                    ToastUtils.show(mContext, result.get("msg") + "");
                }
            }
        } else {
            Log.d(TAG, "onMainThreadResponse: request fail...");
            ToastUtils.show(mContext, result.getString("msg"));
        }
    }

    @Override
    public void onBackPressed() {
        if (checkNotNull()) {
            showAlertDialog("你正在编辑中，确定要关闭页面吗？", false);
        } else {
            super.onBackPressed();
        }
    }

    private boolean checkNotNull() {
        if (!TextUtils.isEmpty(etShopId.getText().toString().trim())) {
            return true;
        }
        if (!TextUtils.isEmpty(etVersion.getText().toString().trim())) {
            return true;
        }
        if (!TextUtils.isEmpty(etDownloadUrl.getText().toString().trim())) {
            return true;
        }
        if (!TextUtils.isEmpty(etAuditVersion.getText().toString().trim())) {
            return true;
        }

        List<String> messages = new ArrayList<>();
        for (VersionLog log : msgItemList) {
            if (!TextUtils.isEmpty(log.getContent())) {
                messages.add(log.getContent());
            }
        }

        return messages.size() > 0;
    }

    private void showAlertDialog(String message, boolean isRelease) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("温馨提示");
        builder.setMessage(message);
        builder.setIcon(R.mipmap.ic_launcher_round);
        // 点击对话框以外的区域是否让对话框消失
        builder.setCancelable(true);
        // 设置正面按钮
        builder.setPositiveButton("确定", (dialog, which) -> {
            if (isRelease) {
                releaseVersion();
            } else {
                finish();
            }
            dialog.dismiss();
        });
        // 设置反面按钮
        builder.setNegativeButton("取消", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        // 对话框显示的监听事件
        dialog.setOnShowListener(dialog1 -> Log.d(TAG, "对话框显示了"));
        // 对话框消失的监听事件
        dialog.setOnCancelListener(dialog12 -> Log.d(TAG, "对话框消失了"));
        // 显示对话框
        dialog.show();
    }

    private void releaseVersion() {

        String shopId = etShopId.getText().toString().trim();
        releaseVersionReq.setApp_id(shopId);
        releaseVersionReq.setShop_id(shopId);
        releaseVersionReq.setVersion(etVersion.getText().toString().trim());
        releaseVersionReq.setDownload_url(etDownloadUrl.getText().toString().trim());
        releaseVersionReq.setAudit_version(etAuditVersion.getText().toString().trim());
        List<String> messages = new ArrayList<>();
        for (VersionLog log : msgItemList) {
            messages.add(log.getContent());
        }
        releaseVersionReq.setMsg(messages);

        releaseVersionPresenter.requestReleaseVersion(url, releaseVersionReq);
    }
}
