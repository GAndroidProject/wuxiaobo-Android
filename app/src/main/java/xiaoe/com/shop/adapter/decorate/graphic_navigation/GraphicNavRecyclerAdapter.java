package xiaoe.com.shop.adapter.decorate.graphic_navigation;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import xiaoe.com.common.entitys.GraphicNavItem;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;
import xiaoe.com.shop.business.navigate_detail.ui.NavigateDetailActivity;

public class GraphicNavRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private Context mContext;
    private List<GraphicNavItem> mItemList;

    public GraphicNavRecyclerAdapter(Context context, List<GraphicNavItem> itemList) {
        this.mContext = context;
        this.mItemList = itemList;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.graphic_navigatioin_item, null);
        return new GraphicNavItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        final int innerPos = position;
        GraphicNavItemViewHolder graphicNavItemViewHolder = (GraphicNavItemViewHolder) holder;
        graphicNavItemViewHolder.itemIcon.setImageURI(mItemList.get(position).getNavIcon());
        graphicNavItemViewHolder.itemContent.setText(mItemList.get(position).getNavContent());
        graphicNavItemViewHolder.itemWrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pageTitle = mItemList.get(innerPos).getNavContent();
                Intent intent = new Intent(mContext, NavigateDetailActivity.class);
                intent.putExtra("pageTitle", pageTitle);
                mContext.startActivity(intent);
            }
        });
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
