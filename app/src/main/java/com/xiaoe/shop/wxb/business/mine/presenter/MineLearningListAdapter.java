package com.xiaoe.shop.wxb.business.mine.presenter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.xiaoe.common.app.CommonUserInfo;
import com.xiaoe.shop.wxb.R;

public class MineLearningListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    // 由于我正在学的 list 是非定制的，所以按设计稿编写
    private List<Integer> imgList = new ArrayList<>();
    private List<String> titleList = new ArrayList<>();

    public MineLearningListAdapter(Context context) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        imgList.add(R.mipmap.icon_my_bought);
        imgList.add(R.mipmap.profile_collect);
        imgList.add(R.mipmap.profile_download);
        imgList.add(R.mipmap.profile_coupon);
        imgList.add(R.mipmap.profile_code);
        titleList.add(mContext.getString(R.string.my_already_bought));
        titleList.add(mContext.getString(R.string.myCollect));
        titleList.add(mContext.getString(R.string.off_line_cache_text));
        titleList.add(mContext.getString(R.string.coupon_title));
        titleList.add(mContext.getString(R.string.cd_key_title));
    }

    @Override
    public int getCount() {
        return imgList == null ? 0 : imgList.size();
    }

    // 返回 title 的 item
    @Override
    public String getItem(int position) {
        return titleList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LearningListItemViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.mine_learning_list_item, parent, false);
            viewHolder = new LearningListItemViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (LearningListItemViewHolder) convertView.getTag();
        }
        viewHolder.itemIcon.setImageResource(imgList.get(position));
        viewHolder.itemTitle.setText(titleList.get(position));
        if (titleList.get(position).equals(mContext.getString(R.string.coupon_title)) && CommonUserInfo.getInstance().isHasUnreadMsg()) {
            viewHolder.itemCouponContainer.setVisibility(View.VISIBLE);
            if (CommonUserInfo.getInstance().getUnreadMsgCount() > 0) {
                viewHolder.itemContent.setVisibility(View.VISIBLE);
                viewHolder.itemContent.setText(String.format(mContext.getResources().getString(R.string.coupon_unread_msg), CommonUserInfo.getInstance().getUnreadMsgCount()));
            } else {
                viewHolder.itemContent.setVisibility(View.GONE);
            }
        } else {
            viewHolder.itemCouponContainer.setVisibility(View.GONE);
        }
        return convertView;
    }
}
