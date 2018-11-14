package xiaoe.com.shop.business.earning.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import xiaoe.com.common.app.Constants;
import xiaoe.com.common.entitys.EarningItem;
import xiaoe.com.common.utils.MeasureUtil;
import xiaoe.com.network.NetworkCodes;
import xiaoe.com.network.requests.EarningRequest;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.XiaoeActivity;
import xiaoe.com.shop.business.earning.presenter.EarningListAdapter;
import xiaoe.com.shop.business.earning.presenter.EarningPresenter;
import xiaoe.com.shop.common.JumpDetail;
import xiaoe.com.shop.utils.StatusBarUtil;

public class ScholarshipActivity extends XiaoeActivity {

    private static final String TAG = "ScholarshipActivity";

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

    EarningPresenter earningPresenter;

    List<EarningItem> dataList;

    String allMoney; // 可提现金额

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_scholarship);

        unbinder = ButterKnife.bind(this);

        scholarWrap.setPadding(0, StatusBarUtil.getStatusBarHeight(this), 0, 0);

        earningPresenter = new EarningPresenter(this);
        earningPresenter.requestLaundryList(Constants.SCHOLARSHIP_ASSET_TYPE, Constants.NEED_FLOW ,Constants.EARNING_FLOW_TYPE, 1, 10);

        initData();
        initListener();
    }

    private void initData() {
        dataList = new ArrayList<>();
        scholarshipTitle.setText("奖学金");
        scholarshipWithdrawal.setText("提现记录");
        scholarshipListTitle.setText("我的奖学金");
        scholarshipTip.setText("暂无奖学金记录，快去做任务领取奖学金吧");
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
                JumpDetail.jumpWr(ScholarshipActivity.this, allMoney);
            }
        });
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        JSONObject result = (JSONObject) entity;
        if (success) {
            if (iRequest instanceof EarningRequest) {
                int code = result.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) {
                    JSONObject data = (JSONObject) result.get("data");
                    initPageData(data);
                } else {
                    Log.d(TAG, "onMainThreadResponse: 请求数据失败...");
                }
            }
        } else {
            Log.d(TAG, "onMainThreadResponse: request fail...");
        }
    }

    // 初始化页面数据
    private void initPageData(JSONObject data) {
        int money = data.getInteger("balance");
        BigDecimal top = new BigDecimal(money);
        BigDecimal bottom = new BigDecimal(100);
        allMoney = String.valueOf(top.divide(bottom, 2, RoundingMode.HALF_UP));
        JSONArray flowList = (JSONArray) data.get("flow_list");
        for (Object item : flowList) {
            JSONObject jsonItem = (JSONObject) item;
            int flowType = jsonItem.getInteger("flow_type");
            String detailId = jsonItem.getString("detail_id");
            int price = jsonItem.getInteger("price");
            String createdAt = jsonItem.getString("created_at");
            String desc = jsonItem.getString("desc");

            EarningItem earningItem = new EarningItem();
            earningItem.setItemTitle(desc);
            earningItem.setItemContent(createdAt.split(" ")[0]);
            BigDecimal topPrice = new BigDecimal(price);
            String realPriceStr = topPrice.divide(bottom, 2, RoundingMode.HALF_UP) + "元";
            earningItem.setItemMoney(realPriceStr);

            dataList.add(earningItem);
        }

        scholarshipMoney.setText(allMoney);
        if (dataList.size() > 0) {
            EarningListAdapter earningListAdapter = new EarningListAdapter(this, dataList);
            scholarshipList.setAdapter(earningListAdapter);
            MeasureUtil.setListViewHeightBasedOnChildren(scholarshipList);
            scholarshipTip.setVisibility(View.GONE);
        } else {
            scholarshipTip.setVisibility(View.VISIBLE);
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
