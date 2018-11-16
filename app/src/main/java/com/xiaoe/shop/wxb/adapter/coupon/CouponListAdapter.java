package com.xiaoe.shop.wxb.adapter.coupon;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import com.xiaoe.common.entitys.CouponInfo;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseViewHolder;
import com.xiaoe.shop.wxb.interfaces.OnSelectCouponListener;

public class CouponListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final String TAG = "CouponListAdapter";
    private Context mContext;
    private int marginTop = 0;
    private int marginBottom = 0;
    private boolean existMarginTop = true;
    private List<CouponInfo> couponList;
    private int showSeleceIcon ;
    private OnSelectCouponListener selectCouponListener;

    public CouponListAdapter(Context context, int showSeleceIcon, int top) {
        this.mContext = context;
        marginTop = top;
        marginBottom = Dp2Px2SpUtil.dp2px(context, 12);
        couponList = new ArrayList<CouponInfo>();
        this.showSeleceIcon = showSeleceIcon;
    }


    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_item_coupon, parent, false);
        CouponListHolder holder = new CouponListHolder(view, showSeleceIcon);
        holder.setOnSelectCouponListener(selectCouponListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        CouponListHolder couponHolder = (CouponListHolder) holder;
        couponHolder.bindView(couponList.get(position), existMarginTop, position, marginTop, marginBottom);
    }

    @Override
    public int getItemCount() {
        return couponList.size();
    }

    public void addAll(List<CouponInfo> list){
        couponList.addAll(list);
        notifyDataSetChanged();
    }
    public List<CouponInfo> getCouponList(){
        return couponList;
    }

    public void setOnSelectCouponListener(OnSelectCouponListener listener) {
        selectCouponListener = listener;
    }


}
