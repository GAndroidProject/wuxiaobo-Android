package xiaoe.com.shop.business.earning.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import xiaoe.com.common.app.CommonUserInfo;
import xiaoe.com.network.NetworkCodes;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.network.requests.SettingPseronMsgRequest;
import xiaoe.com.network.requests.WithDrawalRequest;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.XiaoeActivity;
import xiaoe.com.shop.business.earning.presenter.EarningPresenter;
import xiaoe.com.shop.business.setting.presenter.SettingPresenter;
import xiaoe.com.shop.common.JumpDetail;
import xiaoe.com.shop.common.login.LoginPresenter;
import xiaoe.com.shop.utils.OSUtils;
import xiaoe.com.shop.utils.StatusBarUtil;

// 提现页面
public class WithdrawalActivity extends XiaoeActivity {

    private static final String TAG = "WithdrawalActivity";

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

    EarningPresenter earningPresenter;
    String realName;
    double resultPrice;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setStatusBar();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdrawal);

        unbinder = ButterKnife.bind(this);

        wrWrap.setPadding(0, StatusBarUtil.getStatusBarHeight(this), 0, 0);
        intent = getIntent();
        balance = intent.getStringExtra("allMoney");
        earningPresenter = new EarningPresenter(this);
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
                double price = Double.parseDouble(wrInput.getText().toString());
                resultPrice = price * 100;
                if (resultPrice > 2000000) { // 需要真实姓名
                    SettingPresenter settingPresenter = new SettingPresenter(WithdrawalActivity.this);
                    settingPresenter.requestPersonData(CommonUserInfo.getApiToken(), true);
                } else {
                    earningPresenter.requestWithdrawal(resultPrice, "", OSUtils.getIP(WithdrawalActivity.this));
                }
            }
        });
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        JSONObject result = (JSONObject) entity;
        if (success) {
            if (iRequest instanceof SettingPseronMsgRequest) {
                int code = result.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) {
                    JSONObject data = (JSONObject) result.get("data");
                    realName = data.getString("wx_name");
                    if (!TextUtils.isEmpty(realName)) {
                        earningPresenter.requestWithdrawal(resultPrice, "", OSUtils.getIP(WithdrawalActivity.this));
                    }
                } else {
                    Log.d(TAG, "onMainThreadResponse: 请求失败...");
                }
            } else if (iRequest instanceof WithDrawalRequest) {
                int code = result.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) {
                    Log.d(TAG, "onMainThreadResponse: 提现提交成功...");
                    JumpDetail.jumpWrResult(WithdrawalActivity.this, String.valueOf(resultPrice / 100));
                } else {
                    Log.d(TAG, "onMainThreadResponse: 提现失败...");
                    String msg = result.getString("msg");
                    if (!TextUtils.isEmpty(msg)) {
                        Toast(msg);
                    }
                }
            }
        } else {
            Log.d(TAG, "onMainThreadResponse: request fail...");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
