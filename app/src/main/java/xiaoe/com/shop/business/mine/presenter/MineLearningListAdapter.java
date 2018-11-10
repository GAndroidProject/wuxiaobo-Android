package xiaoe.com.shop.business.mine.presenter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xiaoe.com.shop.R;

public class MineLearningListAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    // 由于我正在学的 list 是非定制的，所以按设计稿编写
    private List<Integer> imgList = new ArrayList<>();
    private List<String> titleList = new ArrayList<>();

    public MineLearningListAdapter(Context context) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        imgList.add(R.mipmap.profile_collect);
        imgList.add(R.mipmap.profile_download);
        imgList.add(R.mipmap.profile_coupon);
        imgList.add(R.mipmap.profile_code);
        titleList.add("我的收藏");
        titleList.add("离线缓存");
        titleList.add("优惠券");
        titleList.add("兑换码");
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
        return convertView;
    }

    class LearningListItemViewHolder{

        @BindView(R.id.learning_list_item_icon)
        ImageView itemIcon;
        @BindView(R.id.learning_list_item_title)
        TextView itemTitle;

        LearningListItemViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }
}
