package com.xiaoe.shop.wxb.business.column.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.WebView;

import com.xiaoe.common.utils.NetworkState;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseFragment;

public class ColumnDetailFragment extends BaseFragment {
    private static final String TAG = "ColumnDetailFragment";
    private View rootView;
    private WebView webView;

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
    }

    public void setContentDetail(String detail){
        webView.loadDataWithBaseURL(null, NetworkState.getNewContent(detail), "text/html", "UFT-8", null);
        CookieSyncManager.createInstance(getContext());
        CookieSyncManager.getInstance().sync();
    }
}
