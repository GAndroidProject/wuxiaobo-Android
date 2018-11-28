package com.xiaoe.shop.wxb.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoe.common.entitys.CouponInfo;
import com.xiaoe.shop.wxb.R;

import java.text.DecimalFormat;

public class CouponView extends FrameLayout {
    public static final String TAG = "CouponView";
    private View rootView;
    private TextView couponMoney;
    private TextView couponType;
    private TextView couponTitle;
    private TextView startDate;
    private TextView endDate;
    private ImageView selectIcon;
    private Context mContext;
    private TextView useConponText;
    private RelativeLayout couponTag;


    public CouponView(@NonNull Context context) {
        this(context,null);
    }

    public CouponView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        rootView = LayoutInflater.from(context).inflate(R.layout.view_coupon,this, true);
        initView();
    }

    private void initView() {
        couponTag = (RelativeLayout) rootView.findViewById(R.id.parent_coupon_money_wrap);
        couponMoney = (TextView) rootView.findViewById(R.id.coupon_money);
        couponType = (TextView) rootView.findViewById(R.id.coupon_type);
        couponTitle = (TextView) rootView.findViewById(R.id.coupon_title);
        startDate = (TextView) rootView.findViewById(R.id.coupon_valid_start_date);
        endDate = (TextView) rootView.findViewById(R.id.coupon_valid_end_date);
        selectIcon = (ImageView) rootView.findViewById(R.id.select_icon);
        selectIcon.setVisibility(GONE);
        useConponText = (TextView) rootView.findViewById(R.id.use_coupon);
    }

    public void setCouponInfo(CouponInfo couponInfo){
        setCouponPrice(couponInfo.getPrice());
        setCouponTitle(couponInfo.getTitle(), couponInfo.getType());
        setStartDate(String.format(mContext.getString(R.string.to_text), couponInfo.getValid_at()));
        setEndDate(couponInfo.getInvalid_at());
        setCouponType(couponInfo.getRequire_price());
        setValid(couponInfo.isValid());
        setSelect(couponInfo.isSelect());
    }

    public void showSelectIcon(int visibility){
        selectIcon.setVisibility(visibility);
        useConponText.setVisibility(visibility == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    public void showRightBthOnlyText(String text) {
        useConponText.setText(text);
        useConponText.setTextColor(mContext.getResources().getColor(R.color.secondary_title_color));
        useConponText.setBackgroundColor(mContext.getResources().getColor(R.color.self_transparent));
    }

    /**
     * @param price
     */
    public void setCouponPrice(int price) {
        float priceF = (float) (price * 0.01);
        couponMoney.setText(format((double) priceF));
//        Log.d(TAG, "setCouponPrice: price " + price + " float " + priceF);
    }

    /**
     * double小数点保留规范（两位）
     *
     * @param d
     * @return
     */
    private String format(Double d) {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return df.format(d);
    }

    /**
     *
     * @param title
     * @param couponType  0-指定商品可用，1-无门槛使用
     */
    public void setCouponTitle(String title, int couponType){
        if(couponType == 0){
            couponTitle.setText(title+"("+mContext.getResources().getString(R.string.appoint_goods_coupon)+")");
        }else{
            couponTitle.setText(title);
        }
    }

    public void setStartDate(String date){
        startDate.setText(date);
    }

    public void setEndDate(String date){
        endDate.setText(date);
    }

    /**
     *
     * @param limit 0-无门槛使用，> 0 满多少可用
     */
    public void setCouponType(int limit){
        if(limit <= 0){
            couponType.setText(mContext.getResources().getString(R.string.no_threshold_coupon));
        }else{
            float limitF = limit / 100f;
            couponType.setText("满"+limitF+"元使用");
        }
    }

    private void setValid(boolean valid){
        if(valid){
            couponTag.setBackgroundResource(R.drawable.coupon_valid_bg);
            couponTitle.setTextColor(mContext.getResources().getColor(R.color.main_title_color));
            startDate.setTextColor(mContext.getResources().getColor(R.color.secondary_title_color));
            endDate.setTextColor(mContext.getResources().getColor(R.color.secondary_title_color));
            useConponText.setTextColor(mContext.getResources().getColor(R.color.recent_update_btn_pressed));
            useConponText.setBackgroundResource(R.drawable.use_coupon_border);
            useConponText.setText(mContext.getResources().getString(R.string.use_coupon));
        }else{
            couponTag.setBackgroundResource(R.drawable.coupon_invalid_bg);
            couponTitle.setTextColor(mContext.getResources().getColor(R.color.knowledge_item_desc_color));
            startDate.setTextColor(mContext.getResources().getColor(R.color.knowledge_item_desc_color));
            endDate.setTextColor(mContext.getResources().getColor(R.color.knowledge_item_desc_color));
            useConponText.setTextColor(mContext.getResources().getColor(R.color.knowledge_item_desc_color));
            useConponText.setBackgroundResource(R.color.self_transparent);
            useConponText.setText(mContext.getResources().getString(R.string.out_time));
        }
    }

    public void setSelect(boolean select){
        if(select){
            selectIcon.setImageResource(R.mipmap.download_checking);
        }else{
            selectIcon.setImageResource(R.mipmap.download_tocheck);
        }
    }
}
