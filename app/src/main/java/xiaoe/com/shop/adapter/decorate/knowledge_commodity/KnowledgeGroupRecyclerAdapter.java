package xiaoe.com.shop.adapter.decorate.knowledge_commodity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import xiaoe.com.common.entitys.DecorateEntityType;
import xiaoe.com.common.entitys.KnowledgeCommodityItem;
import xiaoe.com.common.interfaces.OnItemClickWithKnowledgeListener;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;

public class KnowledgeGroupRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final String TAG = "KnowledgeGroupRecyclerA";

    private Context mContext;
    // 知识商品分组 item 实体类列表
    private List<KnowledgeCommodityItem> mItemList;

    private OnItemClickWithKnowledgeListener onItemClickWithKnowledgeListener;

    public void setOnItemClickWithKnowledgeListener(OnItemClickWithKnowledgeListener onItemClickWithKnowledgeListener) {
        this.onItemClickWithKnowledgeListener = onItemClickWithKnowledgeListener;
    }

    public KnowledgeGroupRecyclerAdapter(Context context, List<KnowledgeCommodityItem> list) {
        this.mContext = context;
        this.mItemList = list;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.knowledge_commodity_group_item, null);
        return new KnowledgeItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        KnowledgeItemViewHolder knowledgeItemViewHolder = (KnowledgeItemViewHolder) holder;
        // 当前下标
        final int currentPos = knowledgeItemViewHolder.getAdapterPosition();
        // 当前元素
        final KnowledgeCommodityItem currentItem = mItemList.get(currentPos);
        knowledgeItemViewHolder.itemIcon.setImageURI(currentItem.getItemImg());
        // 如果是专栏的话需要有两行标题，其他单品就显示一行标题和一行描述
        String srcType = mItemList.get(currentPos).getSrcType();
        if (srcType != null) { // 写的数据的时候并没有添加 srcType 先兼容，课程页用了真数据之后删掉
            if (srcType.equals(DecorateEntityType.TOPIC) || srcType.equals(DecorateEntityType.COLUMN)) { // 专栏或者大专栏
                knowledgeItemViewHolder.itemTitle.setText(mItemList.get(currentPos).getItemTitle());
                knowledgeItemViewHolder.itemTitle.setMaxLines(1);
                knowledgeItemViewHolder.itemTitleColumn.setVisibility(View.VISIBLE);
                knowledgeItemViewHolder.itemTitleColumn.setText(mItemList.get(currentPos).getItemTitleColumn());
            } else { // 其他单品
                knowledgeItemViewHolder.itemTitle.setText(mItemList.get(currentPos).getItemTitle());
                knowledgeItemViewHolder.itemTitle.setMaxLines(2);
                knowledgeItemViewHolder.itemTitleColumn.setVisibility(View.GONE);
            }
        }
        // 无价格，将 desc 文案设置在左边的 textView 中，右边的 textView 内容置空
//        knowledgeItemViewHolder.itemDesc.setText("");
//        knowledgeItemViewHolder.itemPrice.setText(currentItem.getItemDesc());
//        knowledgeItemViewHolder.itemPrice.setTextColor(mContext.getResources().getColor(R.color.knowledge_item_desc_color));
//        viewHolder.itemPrice.setTextSize(12);
        if (currentItem.isHasBuy()) { // 买了
            knowledgeItemViewHolder.itemPrice.setText("已购");
            knowledgeItemViewHolder.itemPrice.setTextColor(mContext.getResources().getColor(R.color.knowledge_item_desc_color));
            knowledgeItemViewHolder.itemDesc.setText(currentItem.getItemDesc());
        } else { // 没买
            knowledgeItemViewHolder.itemPrice.setText(currentItem.getItemPrice());
            knowledgeItemViewHolder.itemDesc.setText(currentItem.getItemDesc());
        }
        // 判断如果 item 是专栏的话就显示一行标题一行描述，其他单品的话就显示两行标题
        knowledgeItemViewHolder.itemWrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickWithKnowledgeListener.onKnowledgeItemClick(v, currentItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItemList == null ? 0 : mItemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}
