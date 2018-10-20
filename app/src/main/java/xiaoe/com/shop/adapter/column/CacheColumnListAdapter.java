package xiaoe.com.shop.adapter.column;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xiaoe.com.common.utils.Dp2Px2SpUtil;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;

public class CacheColumnListAdapter extends RecyclerView.Adapter<BaseViewHolder> {
    private static final String TAG = "CacheColumnListAdapter";
    private final Context mContext;
    public static int marginLeft = 0;
    public static int childMarginLeft = 0;
    public static int childMarginRight = 0;
    public static int marginBottom = 0;
    private int childPaddingLeft = 0;
    private int childPaddingRight = 0;

    public CacheColumnListAdapter(Context context) {
        mContext = context;
        marginLeft = Dp2Px2SpUtil.dp2px(context, 20);
        childMarginLeft = Dp2Px2SpUtil.dp2px(context, 16);
        childMarginRight = Dp2Px2SpUtil.dp2px(context, 16);
        marginBottom = Dp2Px2SpUtil.dp2px(context, 10);
        childPaddingLeft = Dp2Px2SpUtil.dp2px(context, 16);
        childPaddingRight = Dp2Px2SpUtil.dp2px(context, 16);
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.layout_item_cache_column, parent, false);
        return new CacheColumnListHolder(mContext, view, childPaddingLeft, childPaddingRight);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        CacheColumnListHolder columnListHolder = (CacheColumnListHolder) holder;
        columnListHolder.bindView();
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public void setMarginLeft(int left) {
       marginLeft = left;
    }

    public void setChildMarginLeft(int childLeft) {
        childMarginLeft = childLeft;
    }

    public void setMarginBottom(int bottom) {
        marginBottom = bottom;
    }

    public void setChildPaddingLeft(int childPaddingLeft) {
        this.childPaddingLeft = childPaddingLeft;
    }

    public void setChildPaddingRight(int childPaddingRight) {
        this.childPaddingRight = childPaddingRight;
    }
}
