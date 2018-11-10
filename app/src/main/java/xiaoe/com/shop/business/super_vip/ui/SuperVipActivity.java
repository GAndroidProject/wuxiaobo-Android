package xiaoe.com.shop.business.super_vip.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.XiaoeActivity;
import xiaoe.com.shop.common.JumpDetail;

public class SuperVipActivity extends XiaoeActivity {

    @BindView(R.id.super_vip_bg)
    SimpleDraweeView superVipBg;
    @BindView(R.id.super_vip_submit)
    TextView superVipSubmit;

    Unbinder unbinder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_vip);
        unbinder = ButterKnife.bind(this);
        superVipBg.setImageURI("res:///" + R.mipmap.super_vip);
        superVipSubmit.setText(getResources().getString(R.string.vip_btn_money));
        superVipSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 默认超级会员的 resourceType 为 -10，resourceId 为 super_vip
                JumpDetail.jumpPay(SuperVipActivity.this, "super_vip", -10, "res:///" + R.mipmap.pay_vip_bg, "超级会员", 0);
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
