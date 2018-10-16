package xiaoe.com.shop.business.setting.presenter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xiaoe.com.common.entitys.SettingItemInfo;
import xiaoe.com.shop.R;

public class SettingListAdapter extends BaseAdapter {

    private Context mContext;
    private List<SettingItemInfo> mItemList;
    private LayoutInflater mInflater;

    public SettingListAdapter(Context context, List<SettingItemInfo> itemList) {
        this.mContext = context;
        this.mItemList = itemList;
        this.mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mItemList == null ? 0 : mItemList.size();
    }

    @Override
    public SettingItemInfo getItem(int position) {
        return mItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SettingItemViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.setting_item, parent, false);
            viewHolder = new SettingItemViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (SettingItemViewHolder) convertView.getTag();
        }
        if (position == 0) { // 是头像
            viewHolder.itemContent.setVisibility(View.GONE);
            viewHolder.itemIcon.setVisibility(View.VISIBLE);
            viewHolder.itemTitle.setText(mItemList.get(position).getItemTitle());
            viewHolder.itemIcon.setImageURI(mItemList.get(position).getItemIcon());
        } else { // 不是头像
            viewHolder.itemContent.setVisibility(View.VISIBLE);
            viewHolder.itemIcon.setVisibility(View.GONE);
            viewHolder.itemTitle.setText(mItemList.get(position).getItemTitle());
            viewHolder.itemContent.setText(mItemList.get(position).getItemContent());
        }
        return convertView;
    }

    class SettingItemViewHolder {

        @BindView(R.id.item_title)
        TextView itemTitle;
        @BindView(R.id.item_icon)
        SimpleDraweeView itemIcon;
        @BindView(R.id.item_content)
        TextView itemContent;

        SettingItemViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }
}
