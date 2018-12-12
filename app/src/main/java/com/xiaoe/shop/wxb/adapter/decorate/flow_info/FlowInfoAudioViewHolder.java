package com.xiaoe.shop.wxb.adapter.decorate.flow_info;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.internal.DebouncingOnClickListener;

import com.xiaoe.common.entitys.FlowInfoItem;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseViewHolder;
import com.xiaoe.shop.wxb.business.audio.presenter.AudioPlayUtil;
import com.xiaoe.shop.wxb.common.JumpDetail;
import com.xiaoe.shop.wxb.common.datareport.EventReportManager;
import com.xiaoe.shop.wxb.common.datareport.MobclickEvent;
import com.xiaoe.shop.wxb.utils.SetImageUriUtil;

import java.util.HashMap;

/**
 * 音频 ViewHolder
 */
public class FlowInfoAudioViewHolder extends BaseViewHolder {

    private Context mContext;

    @BindView(R.id.flow_info_audio_wrap)
    public FrameLayout flowInfoWrap;
    @BindView(R.id.flow_info_audio_bg)
    public SimpleDraweeView flowInfoBg;
    @BindView(R.id.flow_info_audio_avatar)
    public SimpleDraweeView flowInfoAvatar;
    @BindView(R.id.flow_info_audio_joined_desc)
    public TextView flowInfoJoinedDesc;
    @BindView(R.id.flow_info_audio_tag)
    public TextView flowInfoTag;
    @BindView(R.id.flow_info_audio_title)
    public TextView flowInfoTitle;
    @BindView(R.id.flow_info_audio_desc)
    public TextView flowInfoDesc;
    @BindView(R.id.flow_info_audio_price)
    public TextView flowInfoPrice;

    public FlowInfoAudioViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public FlowInfoAudioViewHolder(Context context, View itemView) {
        super(itemView);
        this.mContext = context;
        ButterKnife.bind(this, itemView);
    }

    public void initViewHolder(FlowInfoItem bindItem, int position) {
        String audioDefault = "res:///" + R.mipmap.audio_bg;
        flowInfoBg.getWidth();
        SetImageUriUtil.setImgURI(flowInfoBg, audioDefault, Dp2Px2SpUtil.dp2px(mContext, 190), Dp2Px2SpUtil.dp2px(mContext, 375));
        String url = "";
        if (bindItem.getItemImg() != null) {
            url = bindItem.getItemImg();
        } else {
            url = "res:///" + R.mipmap.audio_ring;
        }
        if (url.contains("res:///")) { // 本地图片
            SetImageUriUtil.setImgURI(flowInfoAvatar, url, Dp2Px2SpUtil.dp2px(mContext, 194), Dp2Px2SpUtil.dp2px(mContext, 194));
        } else { // 网络图片
            if (SetImageUriUtil.isGif(url)) { // gif 图
                SetImageUriUtil.setRoundAsCircle(flowInfoAvatar, Uri.parse(url));
            } else { // 普通图
                SetImageUriUtil.setImgURI(flowInfoAvatar, url, Dp2Px2SpUtil.dp2px(mContext, 194), Dp2Px2SpUtil.dp2px(mContext, 194));
            }
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
        String joinedDesc;
        if (bindItem.getItemJoinedDesc() == null) {
            joinedDesc = "";
        } else {
            joinedDesc = String.format(mContext.getString(R.string.people_listening), bindItem.getItemJoinedDesc());
        }
        flowInfoJoinedDesc.setText(joinedDesc);
        flowInfoWrap.setOnClickListener(new DebouncingOnClickListener() {
            @Override
            public void doClick(View v) {
                // TODO: 转场动画
                AudioPlayUtil.getInstance().setFromTag("flowInfo");
                JumpDetail.jumpAudio(mContext, bindItem.getItemId(), bindItem.isItemHasBuy() ? 1 : 0 );

                HashMap<String, String> map = new HashMap<>(1);
                map.put(MobclickEvent.INDEX, position + "");
                EventReportManager.onEvent(mContext, MobclickEvent.TODAY_LIST_ITEM_CLICK, map);
            }
        });
    }
}
