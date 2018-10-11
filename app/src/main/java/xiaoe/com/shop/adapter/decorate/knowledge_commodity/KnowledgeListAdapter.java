package xiaoe.com.shop.adapter.decorate.knowledge_commodity;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
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
import xiaoe.com.common.entitys.KnowledgeCommodityItem;
import xiaoe.com.shop.R;

public class KnowledgeListAdapter extends BaseAdapter {

    private List<KnowledgeCommodityItem> mItemList;
    private Context mContext;
    private LayoutInflater mInflater;

    public KnowledgeListAdapter(Activity activity, List<KnowledgeCommodityItem> itemList) {
        this.mContext = activity;
        this.mItemList = itemList;
        this.mInflater = LayoutInflater.from(mContext);
    }

    public KnowledgeListAdapter(Context context, List<KnowledgeCommodityItem> itemList) {
        this.mContext = context;
        this.mItemList = itemList;
        this.mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mItemList == null ? 0 : mItemList.size();
    }

    @Override
    public KnowledgeCommodityItem getItem(int position) {
        return mItemList == null ? null : mItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final KnowledgeHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.knowledge_commodity_list_item, parent, false);
            viewHolder = new KnowledgeHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (KnowledgeHolder) convertView.getTag();
        }
        viewHolder.itemTitle.setText(mItemList.get(position).getItemTitle());
        viewHolder.itemIcon.setImageURI(mItemList.get(position).getItemImg());
        if (TextUtils.isEmpty(mItemList.get(position).getItemPrice())) { // 无价格，将 desc 文案设置在左边的 textView 中，右边的 textView 内容置空
            viewHolder.itemDesc.setText("");
            viewHolder.itemPrice.setText(mItemList.get(position).getItemDesc());
            viewHolder.itemPrice.setTextColor(mContext.getResources().getColor(R.color.knowledge_item_desc_color));
        } else { // 有价格
            if (mItemList.get(position).isHasBuy()) { // 买了
                viewHolder.itemPrice.setText("已购");
                viewHolder.itemPrice.setTextColor(mContext.getResources().getColor(R.color.knowledge_item_desc_color));
                viewHolder.itemDesc.setText(mItemList.get(position).getItemDesc());
            } else { // 没买
                viewHolder.itemDesc.setText(mItemList.get(position).getItemDesc());
                viewHolder.itemPrice.setText(mItemList.get(position).getItemPrice());
            }
        }
        viewHolder.itemWrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "点击item..", Toast.LENGTH_SHORT).show();
            }
        });
        return convertView;
    }

    class KnowledgeHolder {

        @BindView(R.id.knowledge_list_item_wrap)
        RelativeLayout itemWrap;
        @BindView(R.id.knowledge_list_item_icon)
        SimpleDraweeView itemIcon;
        @BindView(R.id.knowledge_list_item_title)
        TextView itemTitle;
        @BindView(R.id.knowledge_list_item_price)
        TextView itemPrice;
        @BindView(R.id.knowledge_list_item_desc)
        TextView itemDesc;

        KnowledgeHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }
}
