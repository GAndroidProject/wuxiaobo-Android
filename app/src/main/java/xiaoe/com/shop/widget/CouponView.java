package xiaoe.com.shop.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import xiaoe.com.shop.R;

public class CouponView extends FrameLayout {
    private static final String TAG = "CouponView";
    private View rootView;
    private TextView couponMoney;
    private TextView couponType;
    private TextView couponTitle;
    private TextView startDate;
    private TextView endDate;
    private ImageView selectIcon;


    public CouponView(@NonNull Context context) {
        this(context,null);
    }

    public CouponView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        rootView = LayoutInflater.from(context).inflate(R.layout.view_coupon,this, true);
        initView();
    }

    private void initView() {
        couponMoney = (TextView) rootView.findViewById(R.id.coupon_money);
        couponType = (TextView) rootView.findViewById(R.id.coupon_type);
        couponTitle = (TextView) rootView.findViewById(R.id.coupon_title);
        startDate = (TextView) rootView.findViewById(R.id.coupon_valid_start_date);
        endDate = (TextView) rootView.findViewById(R.id.coupon_valid_end_date);
        selectIcon = (ImageView) rootView.findViewById(R.id.select_icon);
        selectIcon.setVisibility(GONE);
    }


}
