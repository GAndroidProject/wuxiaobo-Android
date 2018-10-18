package xiaoe.com.shop.business.setting.presenter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import xiaoe.com.common.entitys.SettingItemInfo;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;
import xiaoe.com.shop.business.setting.ui.SettingPersonItemActivity;

public class SettingRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private Context mContext;
    private List<SettingItemInfo> itemList;

    private int currentPos;
    private SettingItemInfo currentItem;
    private OnItemClickListener onItemClickListener;

    // 默认会有底部 margin
    public SettingRecyclerAdapter(Context context, List<SettingItemInfo> itemList) {
        this.mContext = context;
        this.itemList = itemList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.setting_item, null);
        view.setLayoutParams(layoutParams);
        return new SettingItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        SettingItemViewHolder viewHolder = (SettingItemViewHolder) holder;
        final int tempPos = viewHolder.getAdapterPosition();
        if (position == itemList.size()) { // 最后一个空项，什么都不显示
            viewHolder.itemContainer.setVisibility(View.GONE);
            return;
        } else {
            currentItem = itemList.get(currentPos);
        }
        if (currentPos == 0) { // 是头像，约定如果有 title 会优先显示 title
            if (TextUtils.isEmpty(currentItem.getItemTitle())) {
                viewHolder.itemIcon.setVisibility(View.VISIBLE);
                viewHolder.itemContent.setVisibility(View.GONE);
                viewHolder.itemTitle.setText(currentItem.getItemTitle());
                viewHolder.itemIcon.setImageURI(currentItem.getItemIcon());
            } else {
                viewHolder.itemIcon.setVisibility(View.GONE);
                viewHolder.itemContent.setVisibility(View.VISIBLE);
                viewHolder.itemTitle.setText(currentItem.getItemTitle());
                viewHolder.itemContent.setText(currentItem.getItemContent());
            }
        } else {
            itemList.size(); // 其他内容
            viewHolder.itemIcon.setVisibility(View.GONE);
            viewHolder.itemContent.setVisibility(View.VISIBLE);
            viewHolder.itemTitle.setText(currentItem.getItemTitle());
            viewHolder.itemContent.setText(currentItem.getItemContent());
        }
        if (currentPos != itemList.size()) {
            viewHolder.itemContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(v, tempPos);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        currentPos = position;
        return super.getItemViewType(position);
    }

    class SettingItemViewHolder extends BaseViewHolder {

        @BindView(R.id.setting_item_container)
        FrameLayout itemContainer;
        @BindView(R.id.item_title)
        TextView itemTitle;
        @BindView(R.id.item_icon)
        SimpleDraweeView itemIcon;
        @BindView(R.id.item_content)
        TextView itemContent;
        @BindView(R.id.item_go)
        ImageView itemGo;

        private OnItemClickListener itemClickListener;

        SettingItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
