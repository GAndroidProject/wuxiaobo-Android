package com.xiaoe.shop.wxb.common.releaseversion.ui;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;

import com.xiaoe.common.app.Global;
import com.xiaoe.common.entitys.VersionLog;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.adapter.common.VersionLogAdapter;
import com.xiaoe.shop.wxb.base.XiaoeActivity;
import com.xiaoe.shop.wxb.utils.StatusBarUtil;
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

    private List<VersionLog> voteItemList = new ArrayList<>();
    private VersionLogAdapter optionsAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_release_version);
        ButterKnife.bind(this);
        // 状态栏颜色字体(白底黑字)修改 Android6.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setStatusBarColor(getWindow(), Color.parseColor(Global.g().getGlobalColor()), View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        initView();
        initListener();
        initData();
    }

    private void initView() {

    }

    private void initListener() {

    }

    private void initData() {
        getInitVoteItem();

        optionsAdapter = new VersionLogAdapter(this, new Handler(this), voteItemList);
        lvVersionLog.setAdapter(optionsAdapter);
        lvVersionLog.bindLinearLayoutV();
    }

    private void getInitVoteItem() {
        VersionLog voteItem1 = new VersionLog();
        voteItemList.add(voteItem1);
        VersionLog voteItem2 = new VersionLog();
        voteItemList.add(voteItem2);
    }

    @Override
    public boolean handleMessage(Message message) {
        int index = message.arg1;
        switch (message.what) {
            case 1:
                VersionLog voteItem = new VersionLog();
                voteItemList.add(voteItem);
                optionsAdapter.notifyDataSetChanged();
                lvVersionLog.bindLinearLayoutV();
                break;
            case 2:
                voteItemList.remove(index);
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

}
