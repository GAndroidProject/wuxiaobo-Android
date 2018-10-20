package xiaoe.com.shop.business.coupon.ui;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import xiaoe.com.common.app.Global;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.XiaoeActivity;
import xiaoe.com.shop.business.coupon.presenter.CouponPresenter;
import xiaoe.com.shop.utils.StatusBarUtil;

public class CouponActivity extends XiaoeActivity {

    private static final String TAG = "CouponActivity";

    protected static final String WITH_CONTENT = "with_content";
    protected static final String EMPTY_CONTENT = "empty_content";

    @BindView(R.id.coupon_back)
    ImageView couponBack;
    @BindView(R.id.coupon_title)
    TextView couponTitle;
    @BindView(R.id.coupon_content_wrap)
    LinearLayout contentWrap;

    Fragment currentFragment;

    boolean hasCoupon;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon);
        ButterKnife.bind(this);

        //状态栏颜色字体(白底黑字)修改 Android6.0+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarUtil.setStatusBarColor(getWindow(), Color.parseColor(Global.g().getGlobalColor()), View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            Global.g().setGlobalColor("#000000");
            StatusBarUtil.setStatusBarColor(getWindow(), Color.parseColor(Global.g().getGlobalColor()), View.SYSTEM_UI_FLAG_VISIBLE);
        }

        // TODO: 请求接口判断是否有可用的优惠券，如果有就显示，否则现在空页面
//        CouponPresenter couponPresenter = new CouponPresenter(this);
//        couponPresenter.requestData();

        initView();
        initListener();
    }

    private void initView() {
        if (hasCoupon) {
            Log.d(TAG, "onCreate: 有优惠券的情况...");
        } else {
            currentFragment = CouponFragment.newInstance(R.layout.fragment_coupone_empty);
            getSupportFragmentManager().beginTransaction().add(R.id.coupon_content_wrap, currentFragment, EMPTY_CONTENT).commit();
        }
    }

    private void initListener() {
        couponBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        Log.d(TAG, "onMainThreadResponse: coupon --- " + success);
    }
}
