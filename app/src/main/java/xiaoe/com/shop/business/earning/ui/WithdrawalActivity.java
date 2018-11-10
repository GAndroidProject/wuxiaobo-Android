package xiaoe.com.shop.business.earning.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import xiaoe.com.common.app.CommonUserInfo;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.XiaoeActivity;
import xiaoe.com.shop.common.JumpDetail;
import xiaoe.com.shop.utils.StatusBarUtil;

// 提现页面
public class WithdrawalActivity extends XiaoeActivity {

    @BindView(R.id.wr_wrap)
    LinearLayout wrWrap;

    @BindView(R.id.title_back)
    ImageView wrBack;
    @BindView(R.id.title_content)
    TextView wrTitle;
    @BindView(R.id.title_end)
    TextView wrDesc;

    @BindView(R.id.wr_page_money)
    EditText wrInput;
    @BindView(R.id.wr_page_error_tip)
    TextView wrErrorTip;
    @BindView(R.id.wr_page_balance)
    TextView wrBalance; // 余额
    @BindView(R.id.wr_page_all_take)
    TextView wrAll;
    @BindView(R.id.wr_page_now)
    TextView wrNow;
    @BindView(R.id.wr_page_limit_phone)
    TextView wrLimitPhone;

    Unbinder unbinder;

//    float balance; // 余额
    String balance; // 余额
    Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setStatusBar();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdrawal);

        unbinder = ButterKnife.bind(this);

        wrWrap.setPadding(0, StatusBarUtil.getStatusBarHeight(this), 0, 0);
        intent = getIntent();
        balance = intent.getStringExtra("allMoney");
        initData();
        initListener();
    }

    private void initData() {
        wrTitle.setText("提现");
        wrDesc.setVisibility(View.GONE);
        wrBalance.setText(String.format(getResources().getString(R.string.withdrawal_left), balance));
        String localPhone = CommonUserInfo.getPhone();
        char[] phoneArr = localPhone.toCharArray();
        for (int i = 0; i < phoneArr.length; i++) {
            if (i >= 3 && i <=8) {
                phoneArr[i] = '*';
            }
        }
        String limitPhone = String.valueOf(phoneArr);
        wrLimitPhone.setText(limitPhone);
    }

    private void initListener() {
        wrBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        wrAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wrInput.setText(String.valueOf(balance));
                wrInput.setSelection(wrInput.getText().toString().length());
            }
        });
        wrNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JumpDetail.jumpWrResult(WithdrawalActivity.this, "160.73");
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
