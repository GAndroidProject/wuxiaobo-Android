package com.xiaoe.shop.wxb.adapter.decorate.flow_info;

import android.content.Context;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.xiaoe.common.entitys.DecorateEntityType;
import com.xiaoe.common.entitys.FlowInfoItem;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseViewHolder;
import com.xiaoe.shop.wxb.business.search.presenter.AutoLineFeedLayoutManager;
import com.xiaoe.shop.wxb.business.search.presenter.SpacesItemDecoration;
import com.xiaoe.shop.wxb.common.JumpDetail;
import com.xiaoe.shop.wxb.common.datareport.EventReportManager;
import com.xiaoe.shop.wxb.common.datareport.MobclickEvent;
import com.xiaoe.shop.wxb.utils.SetImageUriUtil;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.internal.DebouncingOnClickListener;

/**
 * 多媒体 ViewHolder，用于展示音、视频的信息流
 */
public class FlowInfoItemViewHolder extends BaseViewHolder {

    private Context mContext;

    @BindView(R.id.flow_info_item_wrap)
    LinearLayout flowInfoWrap;
    @BindView(R.id.flow_info_item_bg)
    SimpleDraweeView flowInfoBg;
    @BindView(R.id.flow_info_tag)
    TextView flowInfoTag;
    @BindView(R.id.flow_info_item_title)
    TextView flowInfoTitle;
    @BindView(R.id.flow_info_item_desc)
    TextView flowInfoDesc;
    @BindView(R.id.flow_info_type)
    TextView flowInfoType;
    @BindView(R.id.flow_info_item_label)
    RecyclerView flowInfoBottomRecycler;

    private boolean itemHasDecorate = false;

    FlowInfoItemViewHolder(Context context, View itemView) {
        super(itemView);
        this.mContext = context;
        ButterKnife.bind(this, itemView);
    }

    public void initViewHolder(FlowInfoItem bindItem, int position) {
        setItemBgAndTag(bindItem);
        // 标签初始化
        if (bindItem.getLabelList() == null || bindItem.getLabelList().size() == 0) {
            flowInfoBottomRecycler.setVisibility(View.GONE);
            itemHasDecorate = true;
        } else {
            flowInfoBottomRecycler.setVisibility(View.VISIBLE);
        }
        if (!itemHasDecorate) {
            itemHasDecorate = true;
            AutoLineFeedLayoutManager autoLineFeedLayoutManager = new AutoLineFeedLayoutManager(mContext);
            autoLineFeedLayoutManager.setScrollEnabled(false);
            autoLineFeedLayoutManager.setStackFromEnd(true); // 软件盘弹出，recyclerView 随之上移
            SpacesItemDecoration spacesItemDecoration = new SpacesItemDecoration();
            int top = Dp2Px2SpUtil.dp2px(mContext, 14);
            int right = Dp2Px2SpUtil.dp2px(mContext, 8);
            spacesItemDecoration.setMargin(0, top, right, 0);
            FlowInfoLabelAdapter flowInfoLabelAdapter = new FlowInfoLabelAdapter(mContext, bindItem.getLabelList());
            flowInfoBottomRecycler.setLayoutManager(autoLineFeedLayoutManager);
            flowInfoBottomRecycler.addItemDecoration(spacesItemDecoration);
            flowInfoBottomRecycler.setAdapter(flowInfoLabelAdapter);
            flowInfoBottomRecycler.setNestedScrollingEnabled(false);
        }
        LinearLayout.LayoutParams descLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (flowInfoBottomRecycler.getVisibility() == View.VISIBLE) { // 标签在，简介则不需要简介不需要 marginBottom
            descLayoutParams.setMargins(Dp2Px2SpUtil.dp2px(mContext, 20), Dp2Px2SpUtil.dp2px(mContext, 12),
                    Dp2Px2SpUtil.dp2px(mContext, 20), 0);
            flowInfoDesc.setLayoutParams(descLayoutParams);
        } else {
            descLayoutParams.setMargins(Dp2Px2SpUtil.dp2px(mContext, 20), Dp2Px2SpUtil.dp2px(mContext, 12),
                    Dp2Px2SpUtil.dp2px(mContext, 20), Dp2Px2SpUtil.dp2px(mContext, 14));
            flowInfoDesc.setLayoutParams(descLayoutParams);
        }
        // 基本信息初始化
        flowInfoTitle.setText(bindItem.getItemTitle());
        if (TextUtils.isEmpty(bindItem.getItemDesc())) {
            flowInfoDesc.setVisibility(View.GONE);
        } else {
            flowInfoDesc.setText(bindItem.getItemDesc());
        }
        if (!TextUtils.isEmpty(bindItem.getItemTag())) {
            flowInfoTag.setText(bindItem.getItemTag());
            flowInfoTag.setVisibility(View.VISIBLE);
        } else {
            flowInfoTag.setVisibility(View.GONE);
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

    /**
     * 根据绑定的 item 设置图片的宽高和类型
     * @param bindItem
     */
    private void setItemBgAndTag(FlowInfoItem bindItem) {
        LinearLayout.LayoutParams bgLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int height;
        if (DecorateEntityType.AUDIO.equals(bindItem.getItemType())) {
            height = Dp2Px2SpUtil.dp2px(mContext, 190);
            flowInfoType.setText(mContext.getString(R.string.audio_text));
        } else if (DecorateEntityType.VIDEO.equals(bindItem.getItemType())) {
            height = Dp2Px2SpUtil.dp2px(mContext, 190);
            flowInfoType.setText(mContext.getString(R.string.video_text));
        } else if (DecorateEntityType.IMAGE_TEXT.equals(bindItem.getItemType())) {
            height = Dp2Px2SpUtil.dp2px(mContext, 250);
            flowInfoType.setText(mContext.getString(R.string.image_text));
        } else if (DecorateEntityType.COLUMN.equals(bindItem.getItemType())) {
            height = Dp2Px2SpUtil.dp2px(mContext, 250);
            flowInfoType.setText(mContext.getString(R.string.column_text));
        } else if (DecorateEntityType.TOPIC.equals(bindItem.getItemType())) {
            height = Dp2Px2SpUtil.dp2px(mContext, 250);
            flowInfoType.setText(mContext.getString(R.string.big_column_text));
        } else if (DecorateEntityType.MEMBER.equals(bindItem.getItemType())) {
            height = Dp2Px2SpUtil.dp2px(mContext, 250);
            flowInfoType.setText(mContext.getString(R.string.member_text));
        } else {
            height = 0;
        }
        bgLayoutParams.height = height;
        flowInfoBg.setLayoutParams(bgLayoutParams);
        if (SetImageUriUtil.isGif(bindItem.getItemImg())) {
            SetImageUriUtil.setGifURI(flowInfoBg, bindItem.getItemImg(), Dp2Px2SpUtil.dp2px(mContext, 375), height);
        } else {
            SetImageUriUtil.setImgURI(flowInfoBg, bindItem.getItemImg(), Dp2Px2SpUtil.dp2px(mContext, 375), height);
        }
    }
}
