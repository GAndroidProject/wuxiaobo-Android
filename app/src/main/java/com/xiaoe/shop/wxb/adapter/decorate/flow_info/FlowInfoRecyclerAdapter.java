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
        if (viewType == -1) {
            Log.d(TAG, "onCreateViewHolder: flow_info component viewType get -1");
            return null;
        }
        View view = null;
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,0,0,30);
        switch (viewType) {
            case DecorateEntityType.FLOW_INFO_IMG_TEXT: // 图文
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flow_info_img_text, null);
                view.setLayoutParams(layoutParams);
                return new FlowInfoImgTextViewHolder(view);
            // 图文、专栏、大专栏都是用一个布局
            case DecorateEntityType.FLOW_INFO_COLUMN:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flow_info_img_text, null);
                view.setLayoutParams(layoutParams);
                return new FlowInfoImgTextViewHolder(view);
            case DecorateEntityType.FLOW_INFO_TOPIC:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flow_info_img_text, null);
                view.setLayoutParams(layoutParams);
                return new FlowInfoImgTextViewHolder(view);
            case DecorateEntityType.FLOW_INFO_AUDIO: // 音频
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flow_info_audio, null);
                view.setLayoutParams(layoutParams);
                return new FlowInfoAudioViewHolder(view);
            case DecorateEntityType.FLOW_INFO_VIDEO: // 视频
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flow_info_video, null);
                view.setLayoutParams(layoutParams);
                return new FlowInfoVideoViewHolder(view);
            default:
                break;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int bindPos = holder.getAdapterPosition();
        final FlowInfoItem bindItem = mItemList.get(bindPos);
        int itemType = covertStr2Int(bindItem.getItemType());
        if (itemType == -1) {
            Log.d(TAG, "onBindViewHolder: flow_info component viewType get -1");
            return;
        }
        switch (itemType) {
            case DecorateEntityType.FLOW_INFO_IMG_TEXT: // 图文
                final FlowInfoImgTextViewHolder itViewHolder = (FlowInfoImgTextViewHolder) holder;
                if (SetImageUriUtil.isGif(bindItem.getItemImg())) {
                    SetImageUriUtil.setGifURI(itViewHolder.flowInfoBg, bindItem.getItemImg(), Dp2Px2SpUtil.dp2px(mContext, 250), Dp2Px2SpUtil.dp2px(mContext, 375));
                } else {
                    SetImageUriUtil.setImgURI(itViewHolder.flowInfoBg, bindItem.getItemImg(), Dp2Px2SpUtil.dp2px(mContext, 250), Dp2Px2SpUtil.dp2px(mContext, 375));
                }
                if (bindItem.isItemHasBuy()) { // 买了，价格就不显示
                    itViewHolder.flowInfoPrice.setVisibility(View.GONE);
                }
                if (!"".equals(bindItem.getItemTag())) {
                    itViewHolder.flowInfoTag.setText(bindItem.getItemTag());
                }
                itViewHolder.flowInfoTitle.setText(bindItem.getItemTitle());
                itViewHolder.flowInfoDesc.setText(bindItem.getItemDesc());
                if (bindItem.isItemHasBuy()) {
                    itViewHolder.flowInfoPrice.setVisibility(View.GONE);
                } else {
                    itViewHolder.flowInfoPrice.setVisibility(View.VISIBLE);
                    itViewHolder.flowInfoPrice.setText(bindItem.getItemPrice());
                }
//                final String imgUrl = bindItem.getItemImg();
                itViewHolder.flowInfoWrap.setOnClickListener(new DebouncingOnClickListener() {
                    @Override
                    public void doClick(View v) {
                        JumpDetail.jumpImageText(mContext, bindItem.getItemId(), "", "");
                    }
                });
                break;
            case DecorateEntityType.FLOW_INFO_COLUMN: // 专栏
                final FlowInfoImgTextViewHolder columnViewHolder = (FlowInfoImgTextViewHolder) holder;
                SetImageUriUtil.setImgURI(columnViewHolder.flowInfoBg, bindItem.getItemImg(), Dp2Px2SpUtil.dp2px(mContext, 250), Dp2Px2SpUtil.dp2px(mContext, 375));
                if (bindItem.isItemHasBuy()) {
                    columnViewHolder.flowInfoPrice.setVisibility(View.GONE);
                }
                if (!"".equals(bindItem.getItemTag())) {
                    columnViewHolder.flowInfoTag.setText(bindItem.getItemTag());
                }
                columnViewHolder.flowInfoTitle.setText(bindItem.getItemTitle());
                columnViewHolder.flowInfoDesc.setText(bindItem.getItemDesc());
                if (bindItem.isItemHasBuy()) {
                    columnViewHolder.flowInfoPrice.setVisibility(View.GONE);
                } else {
                    columnViewHolder.flowInfoPrice.setVisibility(View.VISIBLE);
                    columnViewHolder.flowInfoPrice.setText(bindItem.getItemPrice());
                }
                columnViewHolder.flowInfoWrap.setOnClickListener(new DebouncingOnClickListener() {
                    @Override
                    public void doClick(View v) {
                        JumpDetail.jumpColumn(mContext, bindItem.getItemId(), bindItem.getItemImg(), 6);
                    }
                });
                break;
            case DecorateEntityType.FLOW_INFO_TOPIC: // 大专栏
                final FlowInfoImgTextViewHolder topicViewHolder = (FlowInfoImgTextViewHolder) holder;
                SetImageUriUtil.setImgURI(topicViewHolder.flowInfoBg, bindItem.getItemImg(), Dp2Px2SpUtil.dp2px(mContext, 250), Dp2Px2SpUtil.dp2px(mContext, 375));
                if (bindItem.isItemHasBuy()) {
                    topicViewHolder.flowInfoPrice.setVisibility(View.GONE);
                }
                if (!"".equals(bindItem.getItemTag())) {
                    topicViewHolder.flowInfoTag.setText(bindItem.getItemTag());
                }
                topicViewHolder.flowInfoTitle.setText(bindItem.getItemTitle());
                topicViewHolder.flowInfoDesc.setText(bindItem.getItemDesc());
                if (bindItem.isItemHasBuy()) {
                    topicViewHolder.flowInfoPrice.setVisibility(View.GONE);
                } else {
                    topicViewHolder.flowInfoPrice.setVisibility(View.VISIBLE);
                    topicViewHolder.flowInfoPrice.setText(bindItem.getItemPrice());
                }
                topicViewHolder.flowInfoWrap.setOnClickListener(new DebouncingOnClickListener() {
                    @Override
                    public void doClick(View v) {
                        JumpDetail.jumpColumn(mContext, bindItem.getItemId(), bindItem.getItemImg(), 8);
                    }
                });
                break;
            case DecorateEntityType.FLOW_INFO_AUDIO: // 音频
                FlowInfoAudioViewHolder audioViewHolder = (FlowInfoAudioViewHolder) holder;
                String audioDefault = "res:///" + R.mipmap.audio_bg;
                audioViewHolder.flowInfoBg.getWidth();
                SetImageUriUtil.setImgURI(audioViewHolder.flowInfoBg, audioDefault, Dp2Px2SpUtil.dp2px(mContext, 190), Dp2Px2SpUtil.dp2px(mContext, 375));
                String url = "";
                if (bindItem.getItemImg() != null) {
                    url = bindItem.getItemImg();
                } else {
                    url = "res:///" + R.mipmap.audio_ring;
                }
                if (url.contains("res:///")) { // 本地图片
                    SetImageUriUtil.setImgURI(audioViewHolder.flowInfoAvatar, url, Dp2Px2SpUtil.dp2px(mContext, 194), Dp2Px2SpUtil.dp2px(mContext, 194));
                } else { // 网络图片
                    if (SetImageUriUtil.isGif(url)) { // gif 图
                        SetImageUriUtil.setRoundAsCircle(audioViewHolder.flowInfoAvatar, Uri.parse(url));
                    } else { // 普通图
                        SetImageUriUtil.setImgURI(audioViewHolder.flowInfoAvatar, url, Dp2Px2SpUtil.dp2px(mContext, 194), Dp2Px2SpUtil.dp2px(mContext, 194));
                    }
                }
                if (!"".equals(bindItem.getItemTag())) {
                    audioViewHolder.flowInfoTag.setText(bindItem.getItemTag());
                }
                audioViewHolder.flowInfoTitle.setText(bindItem.getItemTitle());
                audioViewHolder.flowInfoDesc.setText(bindItem.getItemDesc());
                if (bindItem.isItemHasBuy()) {
                    audioViewHolder.flowInfoPrice.setVisibility(View.GONE);
                } else {
                    audioViewHolder.flowInfoPrice.setVisibility(View.VISIBLE);
                    audioViewHolder.flowInfoPrice.setText(bindItem.getItemPrice());
                }
                String joinedDesc;
                if (bindItem.getItemJoinedDesc() == null) {
                    joinedDesc = "";
                } else {
                    joinedDesc = bindItem.getItemJoinedDesc() + "人在听";
                }
                audioViewHolder.flowInfoJoinedDesc.setText(joinedDesc);
                audioViewHolder.flowInfoWrap.setOnClickListener(new DebouncingOnClickListener() {
                    @Override
                    public void doClick(View v) {
                        // TODO: 转场动画
                        AudioPlayUtil.getInstance().setFromTag("flowInfo");
                        JumpDetail.jumpAudio(mContext, bindItem.getItemId(), bindItem.isItemHasBuy() ? 1 : 0 );
                    }
                });
                break;
            case DecorateEntityType.FLOW_INFO_VIDEO: // 视频
                final FlowInfoVideoViewHolder videoViewHolder = (FlowInfoVideoViewHolder) holder;
                SetImageUriUtil.setImgURI(videoViewHolder.flowInfoBg, bindItem.getItemImg(), Dp2Px2SpUtil.dp2px(mContext, 190), Dp2Px2SpUtil.dp2px(mContext, 375));
                if (!"".equals(bindItem.getItemTag())) {
                    videoViewHolder.flowInfoTag.setText(bindItem.getItemTag());
                }
                videoViewHolder.flowInfoTitle.setText(bindItem.getItemTitle());
                videoViewHolder.flowInfoDesc.setText(bindItem.getItemDesc());
                if (bindItem.isItemHasBuy()) {
                    videoViewHolder.flowInfoPrice.setVisibility(View.GONE);
                } else {
                    videoViewHolder.flowInfoPrice.setVisibility(View.VISIBLE);
                    videoViewHolder.flowInfoPrice.setText(bindItem.getItemPrice());

                }
                videoViewHolder.flowInfoWrap.setOnClickListener(new DebouncingOnClickListener() {
                    @Override
                    public void doClick(View v) {
                        // TODO: 转场动画
//                        ActivityOptions options =
//                                ActivityOptions.makeSceneTransitionAnimation((Activity) mContext,
//                                        Pair.create(((View) videoViewHolder.flowInfoBg), "share_video_preview"));
//                        Intent videoIntent = new Intent(mContext, VideoActivity.class);
//                        videoIntent.putExtra("videoImageUrl", videoImageUrl);
//                        mContext.startActivity(videoIntent, options.toBundle());
                        JumpDetail.jumpVideo(mContext, bindItem.getItemId(), bindItem.getItemImg(), false, "");
                    }
                });
                break;
            default:
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
                return DecorateEntityType.FLOW_INFO_TOPIC;
            default:
                return -1;
        }
    }
}
