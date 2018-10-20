package xiaoe.com.shop.adapter.decorate.knowledge_commodity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import xiaoe.com.common.entitys.KnowledgeCommodityItem;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;
import xiaoe.com.shop.business.column.ui.ColumnActivity;

public class KnowledgeGroupRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final String TAG = "KnowledgeGroupRecyclerA";

    private Context mContext;
    // 知识商品分组 item 实体类列表
    private List<KnowledgeCommodityItem> mItemList;

    // 当前下标
    private int currentPos = 0;
    // 当前元素
    private KnowledgeCommodityItem currentItem;

    public KnowledgeGroupRecyclerAdapter(Context context, List<KnowledgeCommodityItem> list) {
        this.mContext = context;
        this.mItemList = list;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.knowledge_commodity_group_item, null);
        currentItem = mItemList.get(currentPos);
        return new KnowledgeItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, final int position) {
        KnowledgeItemViewHolder knowledgeItemViewHolder = (KnowledgeItemViewHolder) holder;
        knowledgeItemViewHolder.itemIcon.setImageURI(currentItem.getItemImg());
        knowledgeItemViewHolder.itemTitle.setText(currentItem.getItemTitle());
        if (TextUtils.isEmpty(currentItem.getItemPrice())) { // 无价格，将 desc 文案设置在左边的 textView 中，右边的 textView 内容置空
            knowledgeItemViewHolder.itemDesc.setText("");
            knowledgeItemViewHolder.itemPrice.setText(currentItem.getItemDesc());
            knowledgeItemViewHolder.itemPrice.setTextColor(mContext.getResources().getColor(R.color.knowledge_item_desc_color));
        } else {
            if (currentItem.isHasBuy()) { // 买了
                knowledgeItemViewHolder.itemPrice.setText("已购");
                knowledgeItemViewHolder.itemPrice.setTextColor(mContext.getResources().getColor(R.color.knowledge_item_desc_color));
                knowledgeItemViewHolder.itemDesc.setText(currentItem.getItemDesc());
            } else { // 没买
                knowledgeItemViewHolder.itemPrice.setText(currentItem.getItemPrice());
                knowledgeItemViewHolder.itemDesc.setText(currentItem.getItemDesc());
            }
        }
        knowledgeItemViewHolder.itemWrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, ColumnActivity.class);
                boolean isBigColumn = position % 2 == 0 ? true : false;
                intent.putExtra("isBigColumn", isBigColumn);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItemList == null ? 0 : mItemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        currentPos = position;
        return super.getItemViewType(position);
    }
}
