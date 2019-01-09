package com.xiaoe.shop.wxb.business.setting.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.xiaoe.common.app.CommonUserInfo;
import com.xiaoe.common.app.Global;
import com.xiaoe.common.db.LoginSQLiteCallback;
import com.xiaoe.common.db.LrSQLiteCallback;
import com.xiaoe.common.db.SQLiteUtil;
import com.xiaoe.common.entitys.SettingItemInfo;
import com.xiaoe.common.interfaces.OnItemClickWithSettingItemInfoListener;
import com.xiaoe.common.utils.CacheManagerUtil;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.network.downloadUtil.DownloadManager;
import com.xiaoe.network.requests.IRequest;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseFragment;
import com.xiaoe.shop.wxb.business.audio.presenter.AudioMediaPlayer;
import com.xiaoe.shop.wxb.business.audio.presenter.AudioPlayUtil;
import com.xiaoe.shop.wxb.business.setting.presenter.LinearDividerDecoration;
import com.xiaoe.shop.wxb.business.setting.presenter.SettingRecyclerAdapter;
import com.xiaoe.shop.wxb.business.upgrade.AppUpgradeHelper;
import com.xiaoe.shop.wxb.common.JumpDetail;
import com.xiaoe.shop.wxb.interfaces.OnCustomDialogListener;
import com.xiaoe.shop.wxb.utils.ActivityCollector;
import com.xiaoe.shop.wxb.utils.UploadLearnProgressManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;

public class MainAccountFragment extends BaseFragment implements OnItemClickWithSettingItemInfoListener {

    private static final String TAG = "MainAccountFragment";

    private Unbinder unbinder;
    private Context mContext;
    private boolean destroyView = false;

    @BindView(R.id.setting_account_content)
    RecyclerView accountContent;
    @BindView(R.id.setting_account_btn)
    Button accountBtn;

    List<SettingItemInfo> itemInfoList;
    SettingRecyclerAdapter settingRecyclerAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_main, null, false);
        unbinder = ButterKnife.bind(this, view);
        mContext = getContext();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initListener();
    }

    private void initData() {
        // 账号设置主页面假数据
        itemInfoList = new ArrayList<>();
        // TODO: 获取最新版本信息和缓存信息
        // SettingItemInfo account = new SettingItemInfo(getString(R.string.account_settings), "", "");
        SettingItemInfo message = new SettingItemInfo(getString(R.string.message_settings), "", "");
        // 获取缓存信息
        String cacheSize = "0K";
        try {
            cacheSize = CacheManagerUtil.getTotalCacheSize(getContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SettingItemInfo memory = new SettingItemInfo(getString(R.string.remove_cache), "", cacheSize);
        SettingItemInfo version = new SettingItemInfo(getString(R.string.find_a_new_version), "",
                AppUpgradeHelper.getInstance().isHasUpgradeCurrentApp() ? Global.g().getVersionName() : getString(R.string.is_latest_version));
        SettingItemInfo us = new SettingItemInfo(getString(R.string.setting_about_us), "", "");
        // itemInfoList.add(account);
        itemInfoList.add(message);
        itemInfoList.add(memory);
        itemInfoList.add(version);
        itemInfoList.add(us);

        settingRecyclerAdapter = new SettingRecyclerAdapter(getActivity(), itemInfoList);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        LinearDividerDecoration linearDividerDecoration = new LinearDividerDecoration(getActivity(), R.drawable.recycler_divider_line, Dp2Px2SpUtil.dp2px(getActivity(), 20));
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        accountContent.setLayoutManager(llm);
        settingRecyclerAdapter.setOnItemClickWithSettingItemInfoListener(this);
        accountContent.addItemDecoration(linearDividerDecoration);
        accountContent.setAdapter(settingRecyclerAdapter);
    }

    private void initListener() {
        accountBtn.setOnClickListener(new DebouncingOnClickListener() {
            @Override
            public void doClick(View v) {
                getDialog().setTitleVisibility(View.VISIBLE);
                getDialog().setMessageVisibility(View.GONE);
                getDialog().setTitle(getString(R.string.confirm_exit));
                getDialog().setConfirmText(getResources().getString(R.string.confirm_title));
                getDialog().setConfirmTextColor(getResources().getColor(R.color.login_error));
                getDialog().setCancelText(getResources().getString(R.string.cancel_title));
                getDialog().setOnCustomDialogListener(new OnCustomDialogListener() {
                    @Override
                    public void onClickCancel(View view, int tag) {
                        getDialog().dismissDialog();
                    }

                    @Override
                    public void onClickConfirm(View view, int tag) {
                        getDialog().dismissDialog();
                        AudioMediaPlayer.stop();
                        UploadLearnProgressManager.INSTANCE.clearData();
                        AudioPlayUtil.getInstance().setCloseMiniPlayer(true);
                        SQLiteUtil loginUtil = SQLiteUtil.init(getActivity(), new LoginSQLiteCallback());
                        loginUtil.execSQL("delete from " + LoginSQLiteCallback.TABLE_NAME_USER);
                        SQLiteUtil lrUtil = SQLiteUtil.init(getActivity(), new LrSQLiteCallback());
                        lrUtil.execSQL("delete from " + LrSQLiteCallback.TABLE_NAME_LR);
                        CommonUserInfo.getInstance().clearUserInfo();
                        CommonUserInfo.getInstance().clearLoginUserInfo();
                        ActivityCollector.finishAll();
                        CommonUserInfo.setApiToken("");
                        CommonUserInfo.setIsSuperVip(false);
                        CommonUserInfo.setIsSuperVipAvailable(false);
                        JumpDetail.jumpLoginSplash(getActivity());
                        DownloadManager.getInstance().allPaushDownload();
                    }

                    @Override
                    public void onDialogDismiss(DialogInterface dialog, int tag, boolean backKey) {

                    }
                });
                getDialog().showDialog(1);
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: 发送网络请求
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        Log.d(TAG, "onMainThreadResponse: isSuccess ---- " + success);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        destroyView = true;
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    public void onItemClick(View view, SettingItemInfo itemInfo) {
        SettingAccountActivity settingAccountActivity = (SettingAccountActivity) getActivity();
        String tag = settingAccountActivity.int2Str(itemInfoList.indexOf(itemInfo));
        settingAccountActivity.replaceFragment(tag);
    }
}
