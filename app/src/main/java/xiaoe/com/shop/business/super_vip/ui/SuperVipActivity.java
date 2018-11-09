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
                // 获取资源类型和资源 id
//                JumpDetail.jumpPay(SuperVipActivity.this, "", "", "", 0);
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
