package xiaoe.com.shop.adapter.decorate.knowledge_commodity;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import xiaoe.com.common.entitys.KnowledgeCommodityItem;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;

public class KnowledgeGroupRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final String TAG = "KnowledgeGroupRecyclerA";

    private Context mContext;
    private Activity mActivity;
    // 知识商品分组 item 实体类列表
    private List<KnowledgeCommodityItem> itemList;

    // 当前下标
    private int currentPos = 0;
    // 当前元素
    private KnowledgeCommodityItem currentItem;

    public KnowledgeGroupRecyclerAdapter(Context context, List<KnowledgeCommodityItem> list) {
        this.mContext = context;
        this.itemList = list;
    }

    public KnowledgeGroupRecyclerAdapter(Activity activity, List<KnowledgeCommodityItem> list) {
        this.mActivity = activity;
        this.itemList = list;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.knowledge_commodity_group_item, null);
        currentItem = itemList.get(currentPos);
        return new KnowledgeItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        KnowledgeItemViewHolder knowledgeItemViewHolder = (KnowledgeItemViewHolder) holder;
        knowledgeItemViewHolder.itemIcon.setImageURI(currentItem.getItemImg());
        knowledgeItemViewHolder.itemTitle.setText(currentItem.getItemTitle());
        if (TextUtils.isEmpty(currentItem.getItemPrice())) { // 无价格，将 desc 文案设置在左边的 textView 中，右边的 textView 内容置空
            knowledgeItemViewHolder.itemDesc.setText("");
            knowledgeItemViewHolder.itemPrice.setText(currentItem.getItemDesc());
            knowledgeItemViewHolder.itemPrice.setTextColor(mActivity.getResources().getColor(R.color.knowledge_item_desc_color));
        } else {
            if (currentItem.isHasBuy()) { // 买了
                knowledgeItemViewHolder.itemPrice.setText("已购");
                knowledgeItemViewHolder.itemPrice.setTextColor(mActivity.getResources().getColor(R.color.knowledge_item_desc_color));
                knowledgeItemViewHolder.itemDesc.setText(currentItem.getItemDesc());
            } else { // 没买
                knowledgeItemViewHolder.itemPrice.setText(currentItem.getItemPrice());
                knowledgeItemViewHolder.itemDesc.setText(currentItem.getItemDesc());
            }
        }
    }

    @Override
    public int getItemCount() {
        return itemList == null ? 0 : itemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        currentPos = position;
        return super.getItemViewType(position);
    }
}
