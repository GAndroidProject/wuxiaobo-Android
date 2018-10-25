package xiaoe.com.shop.adapter.decorate.graphic_navigation;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import xiaoe.com.common.entitys.GraphicNavItem;
import xiaoe.com.common.interfaces.OnItemClickWithNavItemListener;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;

public class GraphicNavRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private Context mContext;
    private List<GraphicNavItem> mItemList;

    private OnItemClickWithNavItemListener onItemClickWithNavItemListener;

    public void setOnItemClickWithNavItemListener (OnItemClickWithNavItemListener onItemClickWithNavItemListener) {
        this.onItemClickWithNavItemListener = onItemClickWithNavItemListener;
    }

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
        GraphicNavItemViewHolder graphicNavItemViewHolder = (GraphicNavItemViewHolder) holder;
        final int innerPos = graphicNavItemViewHolder.getAdapterPosition();
        graphicNavItemViewHolder.itemIcon.setImageURI(mItemList.get(innerPos).getNavIcon());
        graphicNavItemViewHolder.itemContent.setText(mItemList.get(innerPos).getNavContent());
        graphicNavItemViewHolder.itemWrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickWithNavItemListener != null) {
                    onItemClickWithNavItemListener.onNavItemClick(v, mItemList.get(innerPos));
                }
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
