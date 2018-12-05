package com.xiaoe.shop.wxb.adapter.decorate.flow_info;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.internal.DebouncingOnClickListener;

import com.xiaoe.common.entitys.DecorateEntityType;
import com.xiaoe.common.entitys.FlowInfoItem;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseViewHolder;
import com.xiaoe.shop.wxb.common.JumpDetail;
import com.xiaoe.shop.wxb.utils.SetImageUriUtil;

/**
 * 图文 ViewHolder
 */
public class FlowInfoImgTextViewHolder extends BaseViewHolder {

    private Context mContext;

    @BindView(R.id.flow_info_img_text_wrap)
    public FrameLayout flowInfoWrap;
    @BindView(R.id.flow_info_img_text_bg)
    public SimpleDraweeView flowInfoBg;
    @BindView(R.id.flow_info_img_text_tag)
    public TextView flowInfoTag;
    @BindView(R.id.flow_info_img_text_title)
    public TextView flowInfoTitle;
    @BindView(R.id.flow_info_img_text_desc)
    public TextView flowInfoDesc;
    @BindView(R.id.flow_info_img_text_price)
    public TextView flowInfoPrice;

    public FlowInfoImgTextViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public FlowInfoImgTextViewHolder(Context context, View itemView) {
        super(itemView);
        this.mContext = context;
        ButterKnife.bind(this, itemView);
    }

    /**
     * 初始化图文 viewHolder
     * @param bindItem 绑定 item
     * @param resourceType 资源类型
     */
    public void initViewHolder(FlowInfoItem bindItem, String resourceType) {
        if (SetImageUriUtil.isGif(bindItem.getItemImg())) {
            SetImageUriUtil.setGifURI(flowInfoBg, bindItem.getItemImg(), Dp2Px2SpUtil.dp2px(mContext, 250), Dp2Px2SpUtil.dp2px(mContext, 375));
        } else {
            SetImageUriUtil.setImgURI(flowInfoBg, bindItem.getItemImg(), Dp2Px2SpUtil.dp2px(mContext, 250), Dp2Px2SpUtil.dp2px(mContext, 375));
        }
        if (bindItem.isItemHasBuy()) { // 买了，价格就不显示
            flowInfoPrice.setVisibility(View.GONE);
        }
        if (!"".equals(bindItem.getItemTag())) {
            flowInfoTag.setText(bindItem.getItemTag());
        }
        flowInfoTitle.setText(bindItem.getItemTitle());
        flowInfoDesc.setText(bindItem.getItemDesc());
        if (bindItem.isItemHasBuy()) {
            flowInfoPrice.setVisibility(View.GONE);
        } else {
            flowInfoPrice.setVisibility(View.VISIBLE);
            flowInfoPrice.setText(bindItem.getItemPrice());
        }
//                final String imgUrl = bindItem.getItemImg();
        switch (resourceType) {
            case DecorateEntityType.IMAGE_TEXT:
                flowInfoWrap.setOnClickListener(new DebouncingOnClickListener() {
                    @Override
                    public void doClick(View v) {
                        JumpDetail.jumpImageText(mContext, bindItem.getItemId(), "", "");
                    }
                });
                break;
            case DecorateEntityType.COLUMN:
                flowInfoWrap.setOnClickListener(new DebouncingOnClickListener() {
                    @Override
                    public void doClick(View v) {
                        JumpDetail.jumpColumn(mContext, bindItem.getItemId(), bindItem.getItemImg(), 6);
                    }
                });
                break;
            case DecorateEntityType.TOPIC:
                flowInfoWrap.setOnClickListener(new DebouncingOnClickListener() {
                    @Override
                    public void doClick(View v) {
                        JumpDetail.jumpColumn(mContext, bindItem.getItemId(), bindItem.getItemImg(), 8);
                    }
                });
                break;
        }
    }
}
