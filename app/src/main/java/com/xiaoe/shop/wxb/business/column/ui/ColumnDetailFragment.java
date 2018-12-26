package com.xiaoe.shop.wxb.business.column.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.xiaoe.common.utils.NetworkState;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseFragment;
import com.xiaoe.shop.wxb.common.JumpDetail;

public class ColumnDetailFragment extends BaseFragment {
    private static final String TAG = "ColumnDetailFragment";
    private View rootView;
    private WebView webView;
    private String contentDetail = "";
    private boolean isSetDetail = false;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_column_detail, null, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        webView = (WebView) rootView.findViewById(R.id.column_detail_web_view);
        initWebView(webView);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String s) {
                JumpDetail.jumpAppBrowser(ColumnDetailFragment.this.getContext(), s, "");
                return true;
            }
        });
        if(!isSetDetail){
            webView.loadDataWithBaseURL(null, NetworkState.getNewContent(contentDetail), "text/html", "UFT-8", null);
        }
    }

    public void setContentDetail(String detail){
        if(webView != null){
            isSetDetail = true;
            webView.loadDataWithBaseURL(null, NetworkState.getNewContent(detail), "text/html", "UFT-8", null);
        }else {
            contentDetail = detail;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (webView != null) {
            webView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (webView != null) {
            webView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.destroy();
        }
    }
}
