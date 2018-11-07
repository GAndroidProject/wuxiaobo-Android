package xiaoe.com.shop.business.bought_list.presenter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import xiaoe.com.common.entitys.BoughtListItem;
import xiaoe.com.common.interfaces.OnItemClickWithBoughtItemListener;
import xiaoe.com.shop.R;

public class BoughtListAdapter extends BaseAdapter {

    private Context mContext;
    private List<BoughtListItem> mItemList;
    private LayoutInflater mLayoutInflater;
    private OnItemClickWithBoughtItemListener onItemClickWithBoughtItemListener;

    public void setOnItemClickWithBoughtItemListener(OnItemClickWithBoughtItemListener listener) {
        onItemClickWithBoughtItemListener = listener;
    }

    public BoughtListAdapter(Context context, List<BoughtListItem> itemList) {
        this.mContext = context;
        this.mItemList = itemList;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mItemList == null ? 0 : mItemList.size();
    }

    @Override
    public BoughtListItem getItem(int position) {
        return mItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        BoughtListViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.bought_list_item, parent, false);
            viewHolder = new BoughtListViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (BoughtListViewHolder) convertView.getTag();
        }

        viewHolder.itemIcon.setImageURI(mItemList.get(position).getItemIcon());
        viewHolder.itemContent.setText(mItemList.get(position).getItemTitle());
        viewHolder.itemShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickWithBoughtItemListener.onBoughtListItemClick(v, mItemList.get(position));
            }
        });
        return convertView;
    }
}
