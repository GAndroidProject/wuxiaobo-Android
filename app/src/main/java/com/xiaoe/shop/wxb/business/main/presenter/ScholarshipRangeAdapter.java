package com.xiaoe.shop.wxb.business.main.presenter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import com.xiaoe.common.entitys.ScholarshipRangeItem;
import com.xiaoe.shop.wxb.R;

public class ScholarshipRangeAdapter extends BaseAdapter {

    private Context mContext;
    private List<ScholarshipRangeItem> mItemList;
    private LayoutInflater layoutInflater;

    public ScholarshipRangeAdapter(Context context, List<ScholarshipRangeItem> itemList) {
        this.mContext = context;
        this.mItemList = itemList;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mItemList == null ? 0 : mItemList.size();
    }

    @Override
    public ScholarshipRangeItem getItem(int position) {
        return mItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ScholarshipViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.range_list_item, parent, false);
            viewHolder = new ScholarshipViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ScholarshipViewHolder) convertView.getTag();
        }

        viewHolder.itemRange.setText(mItemList.get(position).getItemRange());
        viewHolder.itemAvatar.setImageURI(mItemList.get(position).getItemAvatar());
        viewHolder.itemName.setText(mItemList.get(position).getItemName());
        if (mItemList.get(position).isSuperVip()) { // 是超级会员
            Drawable right = mContext.getResources().getDrawable(R.mipmap.super_vip);
            viewHolder.itemName.setCompoundDrawables(null, null, right, null);
        } else {
            viewHolder.itemName.setCompoundDrawables(null, null, null, null);
        }
        viewHolder.itemScholarship.setText(mItemList.get(position).getItemScholarship());
        return convertView;
    }
}
