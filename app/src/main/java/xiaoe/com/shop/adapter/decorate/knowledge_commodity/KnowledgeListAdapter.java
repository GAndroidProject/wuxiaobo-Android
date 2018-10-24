package xiaoe.com.shop.adapter.decorate.knowledge_commodity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
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
import xiaoe.com.common.entitys.DecorateEntityType;
import xiaoe.com.common.entitys.KnowledgeCommodityItem;
import xiaoe.com.common.utils.Dp2Px2SpUtil;
import xiaoe.com.shop.R;
import xiaoe.com.shop.business.column.ui.ColumnActivity;
import xiaoe.com.shop.business.course.ui.CourseItemActivity;

public class KnowledgeListAdapter extends BaseAdapter {

    private static final String TAG = "KnowledgeListAdapter";

    private List<KnowledgeCommodityItem> mItemList;
    private Context mContext;
    private LayoutInflater mInflater;

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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final KnowledgeHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.knowledge_commodity_list_item, parent, false);
            viewHolder = new KnowledgeHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (KnowledgeHolder) convertView.getTag();
        }
        // 如果是专栏的话需要有两行标题，其他单品就显示一行标题和一行描述
        String srcType = mItemList.get(position).getSrcType();
        if (srcType != null) { // 写的数据的时候并没有添加 srcType 先兼容，课程页用了真数据之后删掉
            if (srcType.equals(DecorateEntityType.TOPIC) || srcType.equals(DecorateEntityType.COLUMN)) { // 专栏或者大专栏
                viewHolder.itemTitle.setText(mItemList.get(position).getItemTitle());
                viewHolder.itemTitle.setMaxLines(1);
                viewHolder.itemTitleColumn.setVisibility(View.VISIBLE);
                viewHolder.itemTitleColumn.setText(mItemList.get(position).getItemTitleColumn());
            } else { // 其他单品
                viewHolder.itemTitle.setText(mItemList.get(position).getItemTitle());
                viewHolder.itemTitle.setMaxLines(2);
                viewHolder.itemTitleColumn.setVisibility(View.GONE);
            }
        }
        viewHolder.itemIcon.setImageURI(mItemList.get(position).getItemImg());
        // 将 desc 文案设置在左边的 textView 中，右边的 textView 内容置空，这个是什么状态
//            viewHolder.itemDesc.setText("");
//            viewHolder.itemPrice.setText(mItemList.get(position).getItemDesc());
//            viewHolder.itemPrice.setTextColor(mContext.getResources().getColor(R.color.knowledge_item_desc_color));
//            viewHolder.itemPrice.setTextSize(12);
        if (mItemList.get(position).isHasBuy()) { // 买了
            viewHolder.itemPrice.setText("已购");
            viewHolder.itemPrice.setTextColor(mContext.getResources().getColor(R.color.knowledge_item_desc_color));
            viewHolder.itemDesc.setText(mItemList.get(position).getItemDesc());
        } else { // 没买
            viewHolder.itemDesc.setText(mItemList.get(position).getItemDesc());
            viewHolder.itemPrice.setText(mItemList.get(position).getItemPrice());
        }
        viewHolder.itemWrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                switch (mItemList.get(position).getSrcType()) {
                    case DecorateEntityType.IMAGE_TEXT:
                        intent = new Intent(mContext, CourseItemActivity.class);
                        intent.putExtra("imgUrl", mItemList.get(position).getItemImg());
                        mContext.startActivity(intent);
                        break;
                    case DecorateEntityType.AUDIO:
                        break;
                    case DecorateEntityType.VIDEO:
                        break;
                    case DecorateEntityType.COLUMN:
                        break;
                }
            }
        });
        return convertView;
    }
}
