package com.xiaoe.shop.wxb.adapter.decorate.knowledge_commodity;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.xiaoe.common.entitys.DecorateEntityType;
import com.xiaoe.common.entitys.KnowledgeCommodityItem;
import com.xiaoe.common.interfaces.OnItemClickWithKnowledgeListener;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseViewHolder;
import com.xiaoe.shop.wxb.events.OnClickEvent;
import com.xiaoe.shop.wxb.utils.SetImageUriUtil;

/**
 * 知识商品宫格形式 ViewHolder
 */
class KnowledgeItemViewHolder extends BaseViewHolder {

    private Context mContext;

    @BindView(R.id.knowledge_new_group_item_wrap)
    LinearLayout itemWrap;
    @BindView(R.id.knowledge_new_group_item_icon)
    SimpleDraweeView itemIcon;
//    @BindView(R.id.knowledge_group_item_icon_bg)
//    SimpleDraweeView itemIconBg;
    @BindView(R.id.knowledge_new_group_item_learn)
    TextView itemLearn;
    @BindView(R.id.knowledge_new_group_item_title)
    TextView itemTitle;
    @BindView(R.id.knowledge_new_group_item_desc)
    TextView itemTitleColumn;
    @BindView(R.id.knowledge_new_group_item_price)
    TextView itemPrice;
//    @BindView(R.id.knowledge_group_item_desc)
//    TextView itemDesc;

    KnowledgeItemViewHolder(Context context, View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        this.mContext = context;
    }

    /**
     * 初始化 viewHolder
     * @param item    知识商品组件
     */
    public void initViewHolder(KnowledgeCommodityItem item, OnItemClickWithKnowledgeListener onItemClickWithKnowledgeListener) {
        if (item == null) {
            return;
        }
        String srcType = item.getSrcType();
        SetImageUriUtil.setImgURI(itemIcon, item.getItemImg(), Dp2Px2SpUtil.dp2px(mContext, 132),
                Dp2Px2SpUtil.dp2px(mContext, 118));
        itemTitle.setEllipsize(TextUtils.TruncateAt.END);
        itemTitleColumn.setEllipsize(TextUtils.TruncateAt.END);
        if (DecorateEntityType.IMAGE_TEXT.equals(srcType) || DecorateEntityType.AUDIO.equals(srcType) || DecorateEntityType.VIDEO.equals(srcType)) { // 单品，标题两行，没有描述
            itemTitle.setMaxLines(2);
            itemTitleColumn.setVisibility(View.GONE);
        } else if (DecorateEntityType.COLUMN.equals(srcType) || DecorateEntityType.TOPIC.equals(srcType) || DecorateEntityType.MEMBER.equals(srcType)) { // 非单品，标题一行，描述一行
            itemTitle.setMaxLines(1);
            itemTitleColumn.setVisibility(View.VISIBLE);
            itemTitleColumn.setMaxLines(1);
        } else { // 未兼容类型，显示展示与单品一致
            itemTitle.setMaxLines(2);
            itemTitleColumn.setVisibility(View.GONE);
        }
        itemTitle.setText(item.getItemTitle());
        itemTitleColumn.setText(item.getItemTitleColumn());
        itemLearn.setText(item.getItemDesc());
        if ("".equals(item.getItemPrice())) { // 没有价格
            if (item.isFree()) { // 免费的，隐藏价格
                itemPrice.setVisibility(View.GONE);
            }
            if (item.isHasBuy()) { // 已购的，显示已购字段
                itemPrice.setText(mContext.getString(R.string.has_buy_desc));
                GradientDrawable drawable = (GradientDrawable) mContext.getDrawable(R.drawable.flow_info_type_bg);
                if (drawable != null) {
                    drawable.setCornerRadius(4f);
                    itemPrice.setBackground(drawable);
                }
                itemPrice.setPadding(Dp2Px2SpUtil.dp2px(mContext, 8), Dp2Px2SpUtil.dp2px(mContext, 2),
                        Dp2Px2SpUtil.dp2px(mContext, 8), Dp2Px2SpUtil.dp2px(mContext, 2));
                itemPrice.setTextColor(ContextCompat.getColor(mContext, R.color.knowledge_item_desc_color));
            }
        } else {
            itemPrice.setVisibility(View.VISIBLE);
            String priceStr = "";
            if (DecorateEntityType.IMAGE_TEXT.equals(item.getSrcType()) || DecorateEntityType.AUDIO.equals(item.getSrcType()) || DecorateEntityType.VIDEO.equals(item.getSrcType())) {
                priceStr = item.getItemPrice();
            } else if (DecorateEntityType.COLUMN.equals(item.getSrcType()) || DecorateEntityType.TOPIC.equals(item.getSrcType()) || DecorateEntityType.MEMBER.equals(item.getSrcType())) {
                priceStr = item.getResourceCount() + "期/" + item.getItemPrice();
            }
            itemPrice.setText(priceStr);
            itemPrice.setTextColor(ContextCompat.getColor(mContext, R.color.price_color));
        }
        // 判断如果 item 是专栏的话就显示一行标题一行描述，其他单品的话就显示两行标题
        itemWrap.setOnClickListener(new OnClickEvent(OnClickEvent.DEFAULT_SECOND) {
            @Override
            public void singleClick(View v) {
                if (onItemClickWithKnowledgeListener != null) {
                    onItemClickWithKnowledgeListener.onKnowledgeItemClick(v, item);
                }
            }
        });
    }
}
