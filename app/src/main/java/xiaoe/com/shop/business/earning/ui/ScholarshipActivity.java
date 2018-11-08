package xiaoe.com.shop.business.earning.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.XiaoeActivity;
import xiaoe.com.shop.common.JumpDetail;
import xiaoe.com.shop.utils.StatusBarUtil;

public class ScholarshipActivity extends XiaoeActivity {

    @BindView(R.id.scholarship_page_wrap)
    LinearLayout scholarWrap;

    @BindView(R.id.title_back)
    ImageView scholarshipBack;
    @BindView(R.id.title_content)
    TextView scholarshipTitle;
    @BindView(R.id.title_end)
    TextView scholarshipWithdrawal;

    @BindView(R.id.scholarship_page_content)
    TextView scholarshipMoney;
    @BindView(R.id.scholarship_page_submit)
    TextView scholarshipSubmit;
    @BindView(R.id.scholarship_page_be_super_vip)
    TextView scholarshipBeSuperVip;

    @BindView(R.id.earning_list_title)
    TextView scholarshipListTitle;
    @BindView(R.id.earning_list)
    ListView scholarshipList;
    @BindView(R.id.earning_empty_tip)
    TextView scholarshipTip;

    Unbinder unbinder;
    boolean isSuperVip;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_scholarship);

        unbinder = ButterKnife.bind(this);

        scholarWrap.setPadding(0, StatusBarUtil.getStatusBarHeight(this), 0, 0);

        initData();
        initListener();
    }

    private void initData() {
        scholarshipTitle.setText("奖学金");
        scholarshipWithdrawal.setText("提现记录");
        scholarshipListTitle.setText("我的奖学金");
        scholarshipTip.setVisibility(View.VISIBLE);
        if (isSuperVip) { // 超级会员
            scholarshipBeSuperVip.setVisibility(View.VISIBLE);
        } else {
            scholarshipBeSuperVip.setVisibility(View.GONE);
        }
    }

    private void initListener() {
        scholarshipBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        scholarshipWithdrawal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JumpDetail.jumpWrRecord(ScholarshipActivity.this);
            }
        });
        scholarshipSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JumpDetail.jumpWr(ScholarshipActivity.this);
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
