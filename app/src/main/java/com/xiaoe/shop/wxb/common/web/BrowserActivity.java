package com.xiaoe.shop.wxb.common.web;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.xiaoe.common.app.Global;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.XiaoeActivity;
import com.xiaoe.shop.wxb.utils.StatusBarUtil;

import static com.xiaoe.shop.wxb.common.web.BridgeWebView.CUSTOM_ERROR_PAGE;

/**
 * @author lancelot
 * @date 2018/5/15
 */
public class BrowserActivity extends XiaoeActivity {

    @BindView(R.id.setting_account_edit_back)
    ImageView settingAccountEditBack;
    @BindView(R.id.setting_account_edit_title)
    TextView settingAccountEditTitle;
    @BindView(R.id.account_toolbar)
    Toolbar accountToolbar;
    @BindView(R.id.progress)
    ProgressBar mProgressBar;
    @BindView(R.id.web_browser)
    BridgeWebView mWebView;

    public static final String WEB_URL = "url";
    public static final String WEB_TITLE = "title";

    private String url;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_browser);
        ButterKnife.bind(this);

        getIntentValue();
        initView();
        init();
    }

    private void initActionBar() {
        // 状态栏颜色字体(白底黑字)修改 Android6.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setStatusBarColor(getWindow(), Color.parseColor(Global.g().getGlobalColor()), View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        accountToolbar.setPadding(0, StatusBarUtil.getStatusBarHeight(this), 0, 0);
    }

    @Override
    public void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    private void getIntentValue() {
        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }
        url = bundle.getString(WEB_URL, "");
        title = bundle.getString(WEB_TITLE, "");
    }

    private void initView() {
        initActionBar();
        settingAccountEditTitle.setText(title);
    }

    private void init() {
        mWebView.setWebViewClient(new BridgeWebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url == null) {
                    return;
                }
                if (url.contains("m.jinse.com")) {
                    String js = "document.getElementsByClassName(\"appfixed\")[0].remove()";
                    mWebView.loadUrl("javascript:" + js);
                }
            }
        });
        mWebView.setWebChromeClient(new BridgeWebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                mProgressBar.setProgress(newProgress);
                mProgressBar.setVisibility(newProgress >= 100 ? View.GONE : View.VISIBLE);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                settingAccountEditTitle.setText(title);
            }
        });
        mWebView.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) -> {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });
        mWebView.loadUrl(url);
    }

    public boolean loadUrl(String url) {
        if (mWebView == null || TextUtils.isEmpty(url)) {
            return false;
        }
        this.url = url;
        mWebView.loadUrl(url);
        return true;
    }

    public static void openUrl(Context context, Bundle bundle) {
        Intent intent = new Intent(context, BrowserActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    public static void openUrl(Context context, String url, String title) {
        Bundle args = new Bundle();
        args.putString(WEB_URL, url);
        if (!TextUtils.isEmpty(title)) {
            args.putString(WEB_TITLE, title);
        }
        Intent intent = new Intent(context, BrowserActivity.class);
        intent.putExtras(args);
        context.startActivity(intent);
    }

    @Override
    public void onHeadLeftButtonClick(View v) {
        if (isWebGoBack()) {
            return;
        }
        super.onHeadLeftButtonClick(v);
    }

    @Override
    public void onBackPressed() {
        if (isWebGoBack()) {
            return;
        }
        super.onBackPressed();
    }

    private boolean isWebGoBack() {
        if (mWebView.canGoBack() && !mWebView.getUrl().equals(CUSTOM_ERROR_PAGE)) {
            mWebView.goBack();
            return true;
        }
        return false;
    }

}
