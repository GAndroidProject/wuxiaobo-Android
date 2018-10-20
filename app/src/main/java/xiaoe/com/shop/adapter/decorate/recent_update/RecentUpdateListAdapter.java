package xiaoe.com.shop.adapter.decorate.recent_update;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xiaoe.com.common.entitys.RecentUpdateListItem;
import xiaoe.com.shop.R;
import xiaoe.com.shop.business.audio.ui.AudioActivity;

/**
 * 最近更新列表适配器
 */
public class RecentUpdateListAdapter extends BaseAdapter {

    private List<RecentUpdateListItem> mItemList;
    private Activity mActivity;
    private Context mContext;
    private LayoutInflater mInflater;

    public RecentUpdateListAdapter(Context mContext, List<RecentUpdateListItem> itemList) {
        this.mItemList = itemList;
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mItemList == null ? 0 : mItemList.size();
    }

    @Override
    public RecentUpdateListItem getItem(int position) {
        return mItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final RecentUpdateHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.recent_update_list_item, parent ,false);
            viewHolder = new RecentUpdateHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (RecentUpdateHolder) convertView.getTag();
        }
        viewHolder.itemTitle.setText(mItemList.get(position).getListTitle());
        String playState = mItemList.get(position).getListPlayState();
        if (playState.equals("play")) {
            viewHolder.itemIcon.setImageURI("res:///" + R.mipmap.audiolist_playall);
        } else if (playState.equals("stop")) {
            viewHolder.itemIcon.setImageURI("res:///" + R.mipmap.audiolist_playing);
        }
        viewHolder.itemIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (mItemList.get(position).getListPlayState().equals("play")) { // 暂停，点击后换成播放中状态
                viewHolder.itemIcon.setImageURI("res:///" + R.mipmap.audiolist_playing);
                mItemList.get(position).setListPlayState("stop");
            } else if (mItemList.get(position).getListPlayState().equals("stop")) { // 播放中，点击后换成准备播放状态
                viewHolder.itemIcon.setImageURI("res:///" + R.mipmap.audiolist_playall);
                mItemList.get(position).setListPlayState("play");
            }
            }
        });
        viewHolder.itemWrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent audioIntent = new Intent(mContext, AudioActivity.class);
                mContext.startActivity(audioIntent);
            }
        });
        return convertView;
    }

    class RecentUpdateHolder {

        @BindView(R.id.recent_update_item_wrap)
        RelativeLayout itemWrap;
        @BindView(R.id.recent_update_item_title)
        TextView itemTitle;
        @BindView(R.id.recent_update_item_icon)
        SimpleDraweeView itemIcon;

        RecentUpdateHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }
}
