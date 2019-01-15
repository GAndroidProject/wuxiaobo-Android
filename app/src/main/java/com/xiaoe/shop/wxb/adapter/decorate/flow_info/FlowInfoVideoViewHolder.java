package com.xiaoe.shop.wxb.adapter.decorate.flow_info;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.xiaoe.common.entitys.FlowInfoItem;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseViewHolder;
import com.xiaoe.shop.wxb.common.JumpDetail;
import com.xiaoe.shop.wxb.common.datareport.EventReportManager;
import com.xiaoe.shop.wxb.common.datareport.MobclickEvent;
import com.xiaoe.shop.wxb.utils.SetImageUriUtil;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.internal.DebouncingOnClickListener;

/**
 * 视频 ViewHolder
 *
 * @deprecated 已废弃,使用 FlowInfoItemViewHolder
 */
public class FlowInfoVideoViewHolder extends BaseViewHolder {

    private Context mContext;

    @BindView(R.id.flow_info_video_wrap)
    public FrameLayout flowInfoWrap;
    @BindView(R.id.flow_info_video_bg)
    public SimpleDraweeView flowInfoBg;
    @BindView(R.id.flow_info_video_tag)
    public TextView flowInfoTag;
    @BindView(R.id.flow_info_video_title)
    public TextView flowInfoTitle;
    @BindView(R.id.flow_info_video_desc)
    public TextView flowInfoDesc;
    @BindView(R.id.flow_info_video_price)
    public TextView flowInfoPrice;

    public FlowInfoVideoViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public FlowInfoVideoViewHolder(Context context, View itemView) {
        super(itemView);
        this.mContext = context;
        ButterKnife.bind(this, itemView);
    }

    public void initViewHolder(FlowInfoItem bindItem, int position) {
        if (SetImageUriUtil.isGif(bindItem.getItemImg())) {
            SetImageUriUtil.setGifURI(flowInfoBg, bindItem.getItemImg(), Dp2Px2SpUtil.dp2px(mContext, 190), Dp2Px2SpUtil.dp2px(mContext, 375));
        } else {
            SetImageUriUtil.setImgURI(flowInfoBg, bindItem.getItemImg(), Dp2Px2SpUtil.dp2px(mContext, 190), Dp2Px2SpUtil.dp2px(mContext, 375));
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
        flowInfoWrap.setOnClickListener(new DebouncingOnClickListener() {
            @Override
            public void doClick(View v) {
            // TODO: 转场动画
//            ActivityOptions options =
//                    ActivityOptions.makeSceneTransitionAnimation((Activity) mContext,
//                            Pair.create(((View) videoViewHolder.flowInfoBg), "share_video_preview"));
//            Intent videoIntent = new Intent(mContext, VideoActivity.class);
//            videoIntent.putExtra("videoImageUrl", videoImageUrl);
//            mContext.startActivity(videoIntent, options.toBundle());
            JumpDetail.jumpVideo(mContext, bindItem.getItemId(), bindItem.getItemImg(), false, "");

                HashMap<String, String> map = new HashMap<>(1);
                map.put(MobclickEvent.INDEX, position + "");
                EventReportManager.onEvent(mContext, MobclickEvent.TODAY_LIST_ITEM_CLICK, map);
            }
        });
    }
}
