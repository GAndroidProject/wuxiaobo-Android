package xiaoe.com.shop.adapter.decorate.flow_info;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.List;

import xiaoe.com.common.entitys.DecorateEntityType;
import xiaoe.com.common.entitys.FlowInfoItem;
import xiaoe.com.common.utils.Dp2Px2SpUtil;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;
import xiaoe.com.shop.business.audio.presenter.AudioPlayUtil;
import xiaoe.com.shop.business.course.ui.CourseImageTextActivity;
import xiaoe.com.shop.business.video.ui.VideoActivity;
import xiaoe.com.shop.common.JumpDetail;
import xiaoe.com.shop.utils.SetImageUriUtil;

public class FlowInfoRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final String TAG = "FlowInfoRecyclerAdapter";

    private Context mContext;
    private List<FlowInfoItem> mItemList;

    private int currentPos;
    private FlowInfoItem currentItem;

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
        currentItem = mItemList.get(currentPos);
        switch (viewType) {
            case DecorateEntityType.FLOW_INFO_IMG_TEXT: // 图文
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flow_info_img_text, null);
                if (currentItem.isItemHasBuy()) { // 买了，价格就不显示
                    view.findViewById(R.id.flow_info_img_text_price).setVisibility(View.GONE);
                }
                view.setLayoutParams(layoutParams);
                return new FlowInfoImgTextViewHolder(view);
            // 图文、专栏、大专栏都是用一个布局
            case DecorateEntityType.FLOW_INFO_COLUMN:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flow_info_img_text, null);
                if (currentItem.isItemHasBuy()) {
                    view.findViewById(R.id.flow_info_img_text_price).setVisibility(View.GONE);
                }
                view.setLayoutParams(layoutParams);
                return new FlowInfoImgTextViewHolder(view);
            case DecorateEntityType.FLOW_INFO_TOPIC:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flow_info_img_text, null);
                if (currentItem.isItemHasBuy()) {
                    view.findViewById(R.id.flow_info_img_text_price).setVisibility(View.GONE);
                }
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
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int bindPos = holder.getAdapterPosition();
        int itemType = getItemViewType(bindPos);
        if (itemType == -1) {
            Log.d(TAG, "onBindViewHolder: flow_info component viewType get -1");
            return;
        }
        final FlowInfoItem bindItem = mItemList.get(bindPos);
        switch (itemType) {
            case DecorateEntityType.FLOW_INFO_IMG_TEXT: // 图文
                final FlowInfoImgTextViewHolder itViewHolder = (FlowInfoImgTextViewHolder) holder;
                SetImageUriUtil.setImgURI(itViewHolder.flowInfoBg, bindItem.getItemImg(), Dp2Px2SpUtil.dp2px(mContext, 250), Dp2Px2SpUtil.dp2px(mContext, 375));
                itViewHolder.flowInfoTitle.setText(bindItem.getItemTitle());
                itViewHolder.flowInfoDesc.setText(bindItem.getItemDesc());
                if (bindItem.isItemHasBuy()) {
                    itViewHolder.flowInfoPrice.setVisibility(View.GONE);
                } else {
                    itViewHolder.flowInfoPrice.setText(bindItem.getItemPrice());
                }
                final String imgUrl = bindItem.getItemImg();
                itViewHolder.flowInfoWrap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityOptions options =
                                ActivityOptions.makeSceneTransitionAnimation((Activity) mContext,
                                        Pair.create(((View) itViewHolder.flowInfoBg), mContext.getResources().getString(R.string.share_img)),
                                        Pair.create(((View) itViewHolder.flowInfoTitle), mContext.getResources().getString(R.string.share_txt)));
                        Intent transitionIntent = new Intent(mContext, CourseImageTextActivity.class);
                        transitionIntent.putExtra("type", DecorateEntityType.FLOW_INFO_IMG_TEXT_STR);
                        transitionIntent.putExtra("imgUrl", imgUrl);
                        transitionIntent.putExtra("resourceId", bindItem.getItemId());
                        mContext.startActivity(transitionIntent, options.toBundle());
                    }
                });
                break;
            case DecorateEntityType.FLOW_INFO_COLUMN: // 专栏
                final FlowInfoImgTextViewHolder columnViewHolder = (FlowInfoImgTextViewHolder) holder;
                SetImageUriUtil.setImgURI(columnViewHolder.flowInfoBg, bindItem.getItemImg(), Dp2Px2SpUtil.dp2px(mContext, 250), Dp2Px2SpUtil.dp2px(mContext, 375));
                columnViewHolder.flowInfoTitle.setText(bindItem.getItemTitle());
                columnViewHolder.flowInfoDesc.setText(bindItem.getItemDesc());
                if (bindItem.isItemHasBuy()) {
                    columnViewHolder.flowInfoPrice.setVisibility(View.GONE);
                } else {
                    columnViewHolder.flowInfoPrice.setText(bindItem.getItemPrice());
                }
                columnViewHolder.flowInfoWrap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        JumpDetail.jumpColumn(mContext, bindItem.getItemId(), bindItem.getItemType(), false);
                    }
                });
            case DecorateEntityType.FLOW_INFO_TOPIC: // 大专栏
                final FlowInfoImgTextViewHolder topicViewHolder = (FlowInfoImgTextViewHolder) holder;
                SetImageUriUtil.setImgURI(topicViewHolder.flowInfoBg, bindItem.getItemImg(), Dp2Px2SpUtil.dp2px(mContext, 250), Dp2Px2SpUtil.dp2px(mContext, 375));
                topicViewHolder.flowInfoTitle.setText(bindItem.getItemTitle());
                topicViewHolder.flowInfoDesc.setText(bindItem.getItemDesc());
                if (bindItem.isItemHasBuy()) {
                    topicViewHolder.flowInfoPrice.setVisibility(View.GONE);
                } else {
                    topicViewHolder.flowInfoPrice.setText(bindItem.getItemPrice());
                }
                topicViewHolder.flowInfoWrap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        JumpDetail.jumpColumn(mContext, bindItem.getItemId(), bindItem.getItemType(), true);
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
                SetImageUriUtil.setImgURI(audioViewHolder.flowInfoAvatar, url, Dp2Px2SpUtil.dp2px(mContext, 194), Dp2Px2SpUtil.dp2px(mContext, 194));
                audioViewHolder.flowInfoTitle.setText(bindItem.getItemTitle());
                audioViewHolder.flowInfoDesc.setText(bindItem.getItemDesc());
                if (bindItem.isItemHasBuy()) {
                    audioViewHolder.flowInfoPrice.setVisibility(View.GONE);
                } else {
                    audioViewHolder.flowInfoPrice.setText(bindItem.getItemPrice());
                }
                String joinedDesc;
                if (bindItem.getItemJoinedDesc() == null) {
                    joinedDesc = "";
                } else {
                    joinedDesc = bindItem.getItemJoinedDesc() + "人在听";
                }
                audioViewHolder.flowInfoJoinedDesc.setText(joinedDesc);
                audioViewHolder.flowInfoWrap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO: 转场动画
                        AudioPlayUtil.getInstance().setFromTag("flowInfo");
                        JumpDetail.jumpAudio(mContext, bindItem.getItemId(), bindItem.isItemHasBuy() ? 1 : 0 );
                    }
                });
                break;
            case DecorateEntityType.FLOW_INFO_VIDEO: // 视频
                final FlowInfoVideoViewHolder videoViewHolder = (FlowInfoVideoViewHolder) holder;
                SetImageUriUtil.setImgURI(videoViewHolder.flowInfoBg, bindItem.getItemImg(), Dp2Px2SpUtil.dp2px(mContext, 190), Dp2Px2SpUtil.dp2px(mContext, 375));
                videoViewHolder.flowInfoTitle.setText(bindItem.getItemTitle());
                videoViewHolder.flowInfoDesc.setText(bindItem.getItemDesc());
                if (bindItem.isItemHasBuy()) {
                    videoViewHolder.flowInfoPrice.setVisibility(View.GONE);
                } else {
                    videoViewHolder.flowInfoPrice.setText(bindItem.getItemPrice());

                }
                final String videoImageUrl = bindItem.getItemImg();
                videoViewHolder.flowInfoWrap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityOptions options =
                                ActivityOptions.makeSceneTransitionAnimation((Activity) mContext,
                                        Pair.create(((View) videoViewHolder.flowInfoBg), "share_video_preview"));
                        Intent videoIntent = new Intent(mContext, VideoActivity.class);
                        videoIntent.putExtra("videoImageUrl", videoImageUrl);
                        mContext.startActivity(videoIntent, options.toBundle());
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mItemList == null ? 0 : mItemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        currentPos = position;
        currentItem = mItemList.get(position);
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

}
