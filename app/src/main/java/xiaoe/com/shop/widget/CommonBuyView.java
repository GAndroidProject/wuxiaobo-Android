package xiaoe.com.shop.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import xiaoe.com.shop.R;

public class CommonBuyView extends LinearLayout {

    private TextView vipBtn;
    private TextView buyBtn;

    public CommonBuyView(Context context) {
        super(context);
        init(context);
    }

    public CommonBuyView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CommonBuyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public CommonBuyView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        View view = View.inflate(context, R.layout.common_buy, this);
        vipBtn = (TextView) view.findViewById(R.id.buy_vip);
        buyBtn = (TextView) view.findViewById(R.id.buy_course);
    }

    public void setOnVipBtnClickListener(OnClickListener listener) {
        vipBtn.setOnClickListener(listener);
    }

    public void setOnBuyBtnClickListener(OnClickListener listener) {
        buyBtn.setOnClickListener(listener);
    }
}
