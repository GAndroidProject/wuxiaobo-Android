package com.xiaoe.shop.wxb.adapter.decorate.knowledge_commodity;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import com.xiaoe.common.entitys.DecorateEntityType;
import com.xiaoe.common.entitys.KnowledgeCommodityItem;
import com.xiaoe.common.interfaces.OnItemClickWithKnowledgeListener;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseViewHolder;
import com.xiaoe.shop.wxb.events.OnClickEvent;
import com.xiaoe.shop.wxb.utils.SetImageUriUtil;

public class KnowledgeGroupRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final String TAG = "KnowledgeGroupRecyclerA";

    private Context mContext;
    // 知识商品分组 item 实体类列表
    private List<KnowledgeCommodityItem> mItemList;

    private OnItemClickWithKnowledgeListener onItemClickWithKnowledgeListener;
    private final int TYPE_AUDIO = 1;
    private final int TYPE_OTHER = 2;

    public void setOnItemClickWithKnowledgeListener(OnItemClickWithKnowledgeListener onItemClickWithKnowledgeListener) {
        this.onItemClickWithKnowledgeListener = onItemClickWithKnowledgeListener;
    }

    public KnowledgeGroupRecyclerAdapter(Context context, List<KnowledgeCommodityItem> list) {
        this.mContext = context;
        this.mItemList = list;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (TYPE_AUDIO == viewType)
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.knowledge_audio_group_item, null);
        else   view = LayoutInflater.from(parent.getContext()).inflate(R.layout.knowledge_commodity_group_item, null);
        return new KnowledgeItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        KnowledgeItemViewHolder knowledgeItemViewHolder = (KnowledgeItemViewHolder) holder;
        // 当前下标
        final int currentPos = knowledgeItemViewHolder.getAdapterPosition();
        // 当前元素
        final KnowledgeCommodityItem currentItem = mItemList.get(currentPos);
        if (TYPE_AUDIO == getItemViewType(position)){
            knowledgeItemViewHolder.itemIconBg.setVisibility(View.VISIBLE);
            SetImageUriUtil.setImgURI(knowledgeItemViewHolder.itemIconBg, "res:///" +
                            R.mipmap.audio_list_bg , Dp2Px2SpUtil.dp2px(mContext, 160),
                    Dp2Px2SpUtil.dp2px(mContext, 120));
            String url = TextUtils.isEmpty(mItemList.get(position).getItemImg()) ? "res:///" +
                    R.mipmap.detail_disk : mItemList.get(position).getItemImg();
            int imageWidthDp = 84;
            if (url.contains("res:///") || !SetImageUriUtil.isGif(url)) {// 本地图片
                SetImageUriUtil.setImgURI(knowledgeItemViewHolder.itemIcon, url, Dp2Px2SpUtil.dp2px(mContext, imageWidthDp),
                        Dp2Px2SpUtil.dp2px(mContext, imageWidthDp));
            } else {// 网络图片
                SetImageUriUtil.setRoundAsCircle(knowledgeItemViewHolder.itemIcon, Uri.parse(url));
            }
        }else {
            knowledgeItemViewHolder.itemIconBg.setVisibility(View.GONE);
            knowledgeItemViewHolder.itemIcon.setImageURI(currentItem.getItemImg());
        }
        // 如果是专栏的话需要有两行标题，其他单品就显示一行标题和一行描述
        String srcType = mItemList.get(currentPos).getSrcType();
        if (srcType != null) { // 写的数据的时候并没有添加 srcType 先兼容，课程页用了真数据之后删掉
            if (srcType.equals(DecorateEntityType.TOPIC) || srcType.equals(DecorateEntityType.COLUMN) || srcType.equals(DecorateEntityType.MEMBER)) { // 专栏或者大专栏
                knowledgeItemViewHolder.itemTitle.setText(mItemList.get(currentPos).getItemTitle());
                //宫格排列是不需要有副标题
//                knowledgeItemViewHolder.itemTitle.setMaxLines(1);
//                knowledgeItemViewHolder.itemTitleColumn.setVisibility(View.VISIBLE);
//                knowledgeItemViewHolder.itemTitleColumn.setText(mItemList.get(currentPos).getItemTitleColumn());
                knowledgeItemViewHolder.itemTitle.setMaxLines(2);
                knowledgeItemViewHolder.itemTitleColumn.setVisibility(View.GONE);
            } else { // 其他单品
                knowledgeItemViewHolder.itemTitle.setText(mItemList.get(currentPos).getItemTitle());
                knowledgeItemViewHolder.itemTitle.setMaxLines(2);
                knowledgeItemViewHolder.itemTitleColumn.setVisibility(View.GONE);
            }
        }
        if (currentItem.isHasBuy()) { // 买了
//            knowledgeItemViewHolder.itemPrice.setText("");
//            knowledgeItemViewHolder.itemPrice.setTextColor(mContext.getResources().getColor(R.color.knowledge_item_desc_color));
//            knowledgeItemViewHolder.itemDesc.setText(currentItem.getItemDesc());
            // 无价格，将 desc 文案设置在左边的 textView 中，右边的 textView 内容置空
            knowledgeItemViewHolder.itemDesc.setText("");
            knowledgeItemViewHolder.itemPrice.setText(currentItem.getItemDesc());
            knowledgeItemViewHolder.itemPrice.setTextColor(mContext.getResources().getColor(R.color.knowledge_item_desc_color));
        } else { // 没买
            knowledgeItemViewHolder.itemPrice.setText(currentItem.getItemPrice());
            knowledgeItemViewHolder.itemDesc.setText(currentItem.getItemDesc());
        }
        // 判断如果 item 是专栏的话就显示一行标题一行描述，其他单品的话就显示两行标题
        knowledgeItemViewHolder.itemWrap.setOnClickListener(new OnClickEvent(OnClickEvent.DEFAULT_SECOND) {
            @Override
            public void singleClick(View v) {
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
        if (DecorateEntityType.AUDIO.equals(mItemList.get(position).getSrcType()))
            return TYPE_AUDIO;
        return TYPE_OTHER;
    }
}
