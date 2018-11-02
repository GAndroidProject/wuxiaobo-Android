package xiaoe.com.shop.adapter.coupon;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import xiaoe.com.common.entitys.CouponInfo;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;
import xiaoe.com.shop.interfaces.OnSelectCouponListener;
import xiaoe.com.shop.widget.CouponView;

public class CouponListHolder extends BaseViewHolder {
    private static final String TAG = "CouponListHolder";
    private final View rootView;
    private final RelativeLayout couponItem;
    private final CouponView couponView;
    private OnSelectCouponListener selectCouponListener;

    public CouponListHolder(View itemView, int showSeleceIcon) {
        super(itemView);
        rootView = itemView;
        couponItem = (RelativeLayout) rootView.findViewById(R.id.coupon_item);
        couponView = (CouponView) rootView.findViewById(R.id.coupon_info);
        couponView.showSelectIcon(showSeleceIcon);
    }
    public void bindView(final CouponInfo couponInfo, boolean existMarginTop, final int position, int marginTop, int marginBottom){
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) couponItem.getLayoutParams();
        if(position == 0 && existMarginTop){
            layoutParams.setMargins(0, marginTop, 0, marginBottom);
        }else{
            layoutParams.setMargins(0, 0, 0, marginBottom);
        }
        couponView.setCouponInfo(couponInfo);
        couponView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectCouponListener != null){
                    selectCouponListener.onSelect(couponInfo, position);
                }
            }
        });
    }

    public void setOnSelectCouponListener(OnSelectCouponListener listener) {
        selectCouponListener = listener;
    }
}
