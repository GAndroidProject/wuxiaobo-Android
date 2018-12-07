package com.xiaoe.shop.wxb.business.earning.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.XiaoeActivity;
import com.xiaoe.shop.wxb.utils.StatusBarUtil;

public class WithdrawalResultActivity extends XiaoeActivity {

    @BindView(R.id.wr_result_wrap)
    LinearLayout wrResultWrap;

    @BindView(R.id.title_back)
    ImageView wrResultBack;
    @BindView(R.id.title_content)
    TextView wrResultContent;
    @BindView(R.id.title_end)
    TextView wrResultDesc;

    @BindView(R.id.wr_success_money)
    TextView wrResultSuccess;

    Unbinder unbinder;
    Intent intent;
    String showPrice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_withdrawal_result);

        unbinder = ButterKnife.bind(this);

        wrResultWrap.setPadding(0, StatusBarUtil.getStatusBarHeight(this), 0, 0);

        intent = getIntent();
        showPrice = intent.getStringExtra("showPrice");

        initData();
        initListener();
    }

    private void initData() {
        wrResultContent.setText(R.string.show_the_result);
        wrResultDesc.setVisibility(View.GONE);
        wrResultSuccess.setText(showPrice);
    }

    private void initListener() {
        wrResultBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
