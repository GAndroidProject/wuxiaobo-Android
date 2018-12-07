package com.xiaoe.shop.wxb.business.download.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.XiaoeActivity;
import com.xiaoe.shop.wxb.common.JumpDetail;

public class DownloadActivity extends XiaoeActivity implements View.OnClickListener{
    private static final String TAG = "DownloadActivity";

    private ImageView btnBack;
    private TextView btnOffLineCache;
    private Intent mIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_download);
        mIntent = getIntent();
        initViews();
        initData();
    }

    private void initViews() {
        DownloadDirectoryFragment currentFragment = new DownloadDirectoryFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.show_select_download_fragment, currentFragment, "show_select_download_fragment").commit();

        btnBack = (ImageView) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);
        btnOffLineCache = (TextView) findViewById(R.id.btn_off_line_cache);
        btnOffLineCache.setOnClickListener(this);
    }

    private void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_back:
                finish();
                break;
            case R.id.btn_off_line_cache:
                JumpDetail.jumpOffLine(mContext);
                break;
            default:
                break;
        }
    }
}
