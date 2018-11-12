package xiaoe.com.shop.business.super_vip.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import xiaoe.com.network.NetworkCodes;
import xiaoe.com.network.requests.IRequest;
import xiaoe.com.network.requests.SuperVipBuyInfoRequest;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.XiaoeActivity;
import xiaoe.com.shop.business.super_vip.presenter.SuperVipPresenter;
import xiaoe.com.shop.common.JumpDetail;

public class SuperVipActivity extends XiaoeActivity {

    private static final String TAG = "SuperVipActivity";

    @BindView(R.id.super_vip_back)
    ImageView superVipBack;
    @BindView(R.id.super_vip_bg)
    SimpleDraweeView superVipBg;
    @BindView(R.id.super_vip_submit)
    TextView superVipSubmit;

    Unbinder unbinder;

    String productId; // 购买所需要的 produceId
    String resourceId; // 购买所需要的 资源 Id

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_vip);
        unbinder = ButterKnife.bind(this);

        SuperVipPresenter superVipPresenter = new SuperVipPresenter(this);
        superVipPresenter.requestSuperVipBuyInfo();
        superVipBg.setImageURI("res:///" + R.mipmap.super_vip);
        superVipSubmit.setText(getResources().getString(R.string.vip_btn_money));
        superVipSubmit.setClickable(false);
        superVipSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 默认超级会员的 resourceType 为 23，resourceId 为 resourceId
                JumpDetail.jumpPay(SuperVipActivity.this, resourceId, productId, 23, "res:///" + R.mipmap.pay_vip_bg, "超级会员", 180000);
            }
        });
        superVipBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onMainThreadResponse(IRequest iRequest, boolean success, Object entity) {
        super.onMainThreadResponse(iRequest, success, entity);
        JSONObject result = (JSONObject) entity;
        if (success) {
            if (iRequest instanceof SuperVipBuyInfoRequest) {
                int code = result.getInteger("code");
                if (code == NetworkCodes.CODE_SUCCEED) {
                    JSONObject data = (JSONObject) result.get("data");
                    productId = data.getString("svip_id");
                    resourceId = data.getString("resource_id");
                    superVipSubmit.setClickable(true);
                } else {
                    Log.d(TAG, "onMainThreadResponse: 请求超级会员购买信息失败...");
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
