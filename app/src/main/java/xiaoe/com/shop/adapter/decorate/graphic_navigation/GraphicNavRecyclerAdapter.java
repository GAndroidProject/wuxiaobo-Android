package xiaoe.com.shop.adapter.decorate.graphic_navigation;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import xiaoe.com.common.entitys.GraphicNavItem;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;

public class GraphicNavRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private Context mContext;
    private Activity mActivity;
    private List<GraphicNavItem> mItemList;

    public GraphicNavRecyclerAdapter(Context context, List<GraphicNavItem> itemList) {
        this.mContext = context;
        this.mItemList = itemList;
    }

    public GraphicNavRecyclerAdapter(Activity activity, List<GraphicNavItem> itemList) {
        this.mActivity = activity;
        this.mItemList = itemList;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.graphic_navigatioin_item, null);
        return new GraphicNavItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        GraphicNavItemViewHolder graphicNavItemViewHolder = (GraphicNavItemViewHolder) holder;
        graphicNavItemViewHolder.itemIcon.setImageURI(mItemList.get(position).getNavIcon());
        graphicNavItemViewHolder.itemContent.setText(mItemList.get(position).getNavContent());
    }

    @Override
    public int getItemCount() {
        return mItemList == null ? 0 : mItemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}
