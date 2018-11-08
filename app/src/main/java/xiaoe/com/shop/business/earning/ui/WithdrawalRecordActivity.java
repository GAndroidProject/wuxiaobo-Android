package xiaoe.com.shop.business.earning.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import xiaoe.com.common.entitys.WrItem;
import xiaoe.com.common.utils.MeasureUtil;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.XiaoeActivity;
import xiaoe.com.shop.business.earning.presenter.WrListAdapter;
import xiaoe.com.shop.utils.StatusBarUtil;

// 提现记录页面
public class WithdrawalRecordActivity extends XiaoeActivity {

    @BindView(R.id.withdrawal_record_wrap)
    LinearLayout wrWrap;

    @BindView(R.id.title_back)
    ImageView wrBack;
    @BindView(R.id.title_content)
    TextView wrTitle;
    @BindView(R.id.title_end)
    TextView wrEnd;

    @BindView(R.id.withdrawal_record_list)
    ListView wrList;

    List<WrItem> dataList;

    Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_withdrawal_record);

        unbinder = ButterKnife.bind(this);

        wrWrap.setPadding(0, StatusBarUtil.getStatusBarHeight(this), 0, 0);

        initData();
        initListener();
    }

    private void initData() {
        wrTitle.setText("提现记录");
        wrEnd.setVisibility(View.GONE);
        dataList = new ArrayList<>();

        // 提现记录假数据
        WrItem wrItem = new WrItem();
        wrItem.setWrTitle("微信提现");
        wrItem.setWrMoney(5000);
        wrItem.setWrTime("2018/11/08 20:41:40");
        wrItem.setWrState("提现成功");

        dataList.add(wrItem);
        WrListAdapter wrListAdapter = new WrListAdapter(this, dataList);
        wrList.setAdapter(wrListAdapter);
        MeasureUtil.setListViewHeightBasedOnChildren(wrList);
    }

    private void initListener() {
        wrBack.setOnClickListener(new View.OnClickListener() {
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
