package com.xiaoe.shop.wxb.business.earning.presenter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import com.xiaoe.common.entitys.EarningItem;
import com.xiaoe.shop.wxb.R;

public class EarningListAdapter extends BaseAdapter {

    private Context mContext;
    private List<EarningItem> mItemList;
    private LayoutInflater mInflater;

    public EarningListAdapter(Context context, List<EarningItem> list) {
        this.mContext = context;
        this.mItemList = list;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mItemList == null ? 0 : mItemList.size();
    }

    @Override
    public EarningItem getItem(int position) {
        return mItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EarningListViewHolder earningListViewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.earning_list_item, parent, false);
            earningListViewHolder = new EarningListViewHolder(convertView);
            convertView.setTag(earningListViewHolder);
        } else {
            earningListViewHolder = (EarningListViewHolder) convertView.getTag();
        }

        earningListViewHolder.listItemTitle.setText(mItemList.get(position).getItemTitle());
        earningListViewHolder.listItemContent.setText(mItemList.get(position).getItemContent());
        earningListViewHolder.listItemMoney.setText(mItemList.get(position).getItemMoney());

        return convertView;
    }
}
