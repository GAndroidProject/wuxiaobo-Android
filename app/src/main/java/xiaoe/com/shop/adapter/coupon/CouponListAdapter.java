package xiaoe.com.shop.adapter.coupon;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;

public class CouponListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final String TAG = "CouponListAdapter";
    private Context mContext;

    public CouponListAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_item_coupon, parent, false);
        return new CouponListHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 15;
    }
}
