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

// 积分页面
public class IntegralActivity extends XiaoeActivity {

    @BindView(R.id.integral_wrap)
    LinearLayout integralWrap;

    @BindView(R.id.title_back)
    ImageView integralBack;
    @BindView(R.id.title_content)
    TextView integralTitle;
    @BindView(R.id.title_end)
    TextView integralDesc;

    @BindView(R.id.integral_content)
    TextView integralContent;
    @BindView(R.id.integral_be_super_vip)
    TextView integralBuildTip;
    @BindView(R.id.integral_be_super_vip)
    TextView integralBeSuperVip;

    @BindView(R.id.earning_list_title)
    TextView integralListTitle;
    @BindView(R.id.earning_list)
    ListView integralList;

    Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBar();
        setContentView(R.layout.activity_integral);

        unbinder = ButterKnife.bind(this);

        initData();
        initListener();
    }

    private void initData() {

    }

    private void initListener() {
        integralBack.setOnClickListener(new View.OnClickListener() {
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
