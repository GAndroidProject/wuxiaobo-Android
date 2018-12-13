package com.xiaoe.shop.wxb.business.login.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.XiaoeActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ProtocolActivity extends XiaoeActivity {

    Unbinder unbinder;

    @BindView(R.id.protocol_content)
    WebView protocolContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_protocol);
        unbinder = ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        WebSettings webSettings = protocolContent.getSettings();
        webSettings.setJavaScriptEnabled(false);
        webSettings.setAllowFileAccess(true);
        protocolContent.loadUrl(getResources().getString(R.string.service_link));
        protocolContent.setWebViewClient(new WebViewClient());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @OnClick(R.id.protocol_back)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.protocol_back:
                onBackPressed();
                break;
        }
    }
}