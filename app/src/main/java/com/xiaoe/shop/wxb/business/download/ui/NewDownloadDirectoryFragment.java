package com.xiaoe.shop.wxb.business.download.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author: zak
 * @date: 2018/12/20
 */
public class NewDownloadDirectoryFragment extends BaseFragment {

    private static final String TAG = "NewDownloadFragment";

    View mRootView;
    Unbinder unbinder;

    @BindView(R.id.download_recycler_view_new)
    RecyclerView downloadRecyclerViewNew;
    @BindView(R.id.all_select_txt_new)
    TextView allSelectTxtNew;
    @BindView(R.id.select_size_new)
    TextView selectSizeNew;
    @BindView(R.id.btn_download_new)
    TextView btnDownloadNew;
    @BindView(R.id.bottom_select_download_wrap_new)
    LinearLayout bottomSelectDownloadWrap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_download_directory_new, container, false);
        unbinder = ButterKnife.bind(this, mRootView);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        downloadRecyclerViewNew = (RecyclerView) mRootView.findViewById(R.id.download_recycler_view_new);
        allSelectTxtNew = (TextView) mRootView.findViewById(R.id.all_select_txt_new);
        selectSizeNew = (TextView) mRootView.findViewById(R.id.select_size_new);
        btnDownloadNew = (TextView) mRootView.findViewById(R.id.btn_download_new);

    }

    private void initData() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.all_select_txt_new, R.id.btn_download_new})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.all_select_txt_new:
                break;
            case R.id.btn_download_new:
                break;
        }
    }
}
