package com.xiaoe.shop.wxb.adapter.decorate.flow_info;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.xiaoe.common.entitys.DecorateEntityType;
import com.xiaoe.common.entitys.FlowInfoItem;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseViewHolder;
import com.xiaoe.shop.wxb.business.audio.presenter.AudioPlayUtil;
import com.xiaoe.shop.wxb.common.JumpDetail;
import com.xiaoe.shop.wxb.utils.SetImageUriUtil;

import java.util.List;

import butterknife.internal.DebouncingOnClickListener;

/**
 * 老的信息流适配器
 *
 * @deprecated 已废弃,使用 NewFlowInfoRecyclerAdapter
 */
public class FlowInfoRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final String TAG = "FlowInfoRecyclerAdapter";

    private Context mContext;
    private List<FlowInfoItem> mItemList;

    public FlowInfoRecyclerAdapter(Context context, List<FlowInfoItem> list) {
        this.mContext = context;
        this.mItemList = list;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,0,0,30);
        switch (viewType) {
            case DecorateEntityType.FLOW_INFO_IMG_TEXT: // 图文
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flow_info_img_text, null);
                view.setLayoutParams(layoutParams);
                return new FlowInfoImgTextViewHolder(mContext, view);
            // 图文、专栏、大专栏都是用一个布局
            case DecorateEntityType.FLOW_INFO_COLUMN:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flow_info_img_text, null);
                view.setLayoutParams(layoutParams);
                return new FlowInfoImgTextViewHolder(mContext, view);
            case DecorateEntityType.FLOW_INFO_TOPIC:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flow_info_img_text, null);
                view.setLayoutParams(layoutParams);
                return new FlowInfoImgTextViewHolder(mContext, view);
            case DecorateEntityType.FLOW_INFO_AUDIO: // 音频
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flow_info_audio, null);
                view.setLayoutParams(layoutParams);
                return new FlowInfoAudioViewHolder(mContext, view);
            case DecorateEntityType.FLOW_INFO_VIDEO: // 视频
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flow_info_video, null);
                view.setLayoutParams(layoutParams);
                return new FlowInfoVideoViewHolder(mContext, view);
            default: // 临时处理，后续废弃
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flow_info_img_text, null);
                view.setLayoutParams(layoutParams);
                return new FlowInfoImgTextViewHolder(mContext, view);
        }
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int bindPos = holder.getAdapterPosition();
        final FlowInfoItem bindItem = mItemList.get(bindPos);
        int itemType = covertStr2Int(bindItem.getItemType());
        switch (itemType) {
            case DecorateEntityType.FLOW_INFO_IMG_TEXT: // 图文
                FlowInfoImgTextViewHolder itViewHolder = (FlowInfoImgTextViewHolder) holder;
                itViewHolder.initViewHolder(bindItem, bindItem.getItemType(), position);
                break;
            case DecorateEntityType.FLOW_INFO_COLUMN: // 专栏
                FlowInfoImgTextViewHolder columnViewHolder = (FlowInfoImgTextViewHolder) holder;
                columnViewHolder.initViewHolder(bindItem, bindItem.getItemType(), position);
                break;
            case DecorateEntityType.FLOW_INFO_TOPIC: // 大专栏 / 会员
                FlowInfoImgTextViewHolder topicViewHolder = (FlowInfoImgTextViewHolder) holder;
                topicViewHolder.initViewHolder(bindItem, bindItem.getItemType(), position);
                break;
            case DecorateEntityType.FLOW_INFO_AUDIO: // 音频
                FlowInfoAudioViewHolder audioViewHolder = (FlowInfoAudioViewHolder) holder;
                audioViewHolder.initViewHolder(bindItem, position);
                break;
            case DecorateEntityType.FLOW_INFO_VIDEO: // 视频
                final FlowInfoVideoViewHolder videoViewHolder = (FlowInfoVideoViewHolder) holder;
                videoViewHolder.initViewHolder(bindItem, position);
                break;
            default:
                FlowInfoImgTextViewHolder defaultViewHolder = (FlowInfoImgTextViewHolder) holder;
                defaultViewHolder.initViewHolder(bindItem, bindItem.getItemType(), position);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mItemList == null ? 0 : mItemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        FlowInfoItem currentItem = mItemList.get(position);
        String itemType = currentItem.getItemType();
        switch (itemType) {
            case DecorateEntityType.FLOW_INFO_IMG_TEXT_STR:
                return DecorateEntityType.FLOW_INFO_IMG_TEXT;
            case DecorateEntityType.FLOW_INFO_AUDIO_STR:
                return DecorateEntityType.FLOW_INFO_AUDIO;
            case DecorateEntityType.FLOW_INFO_VIDEO_STR:
                return DecorateEntityType.FLOW_INFO_VIDEO;
            case DecorateEntityType.COLUMN:
                return DecorateEntityType.FLOW_INFO_COLUMN;
            case DecorateEntityType.TOPIC:
            case DecorateEntityType.MEMBER:
                return DecorateEntityType.FLOW_INFO_TOPIC;
            default:
                return -1;
        }
    }

    /**
     * 信息流资源类型转换 str - int
     * @param resourceType 资源类型
     * @return 资源类型的字符串形式
     */
    private int covertStr2Int(String resourceType) {
        switch (resourceType) {
            case DecorateEntityType.IMAGE_TEXT:
                return DecorateEntityType.FLOW_INFO_IMG_TEXT;
            case DecorateEntityType.AUDIO:
                return DecorateEntityType.FLOW_INFO_AUDIO;
            case DecorateEntityType.VIDEO:
                return DecorateEntityType.FLOW_INFO_VIDEO;
            case DecorateEntityType.COLUMN:
                return DecorateEntityType.FLOW_INFO_COLUMN;
            case DecorateEntityType.TOPIC:
            case DecorateEntityType.MEMBER:
                return DecorateEntityType.FLOW_INFO_TOPIC;
            default:
                return -1;
        }
    }
}
