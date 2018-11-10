package xiaoe.com.shop.adapter.decorate.flow_info;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

import xiaoe.com.common.entitys.DecorateEntityType;
import xiaoe.com.common.entitys.FlowInfoItem;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;
import xiaoe.com.shop.business.audio.ui.AudioActivity;
import xiaoe.com.shop.business.course.ui.CourseImageTextActivity;
import xiaoe.com.shop.business.video.ui.VideoActivity;
import xiaoe.com.shop.common.JumpDetail;

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
                itViewHolder.flowInfoBg.setImageURI(bindItem.getItemImg());
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
                        mContext.startActivity(transitionIntent, options.toBundle());
                    }
                });
                break;
            case DecorateEntityType.FLOW_INFO_COLUMN: // 专栏
                final FlowInfoImgTextViewHolder columnViewHolder = (FlowInfoImgTextViewHolder) holder;
                columnViewHolder.flowInfoBg.setImageURI(bindItem.getItemImg());
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
                topicViewHolder.flowInfoBg.setImageURI(bindItem.getItemImg());
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
                audioViewHolder.flowInfoBg.setImageURI(Uri.parse("res:///" + R.mipmap.audio_bg));
                audioViewHolder.flowInfoAvatar.setImageURI(Uri.parse("res:///" + R.mipmap.audio_ring));
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
                        JumpDetail.jumpAudio(mContext, bindItem.getItemId(), bindItem.isItemHasBuy() ? 1 : 0 );
                    }
                });
                break;
            case DecorateEntityType.FLOW_INFO_VIDEO: // 视频
                final FlowInfoVideoViewHolder videoViewHolder = (FlowInfoVideoViewHolder) holder;
                videoViewHolder.flowInfoBg.setImageURI(bindItem.getItemImg());
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

    private void jumpAudioActivity(View audioBG, View audioRing){
        ActivityOptions options =
                ActivityOptions.makeSceneTransitionAnimation((Activity) mContext,
                        Pair.create(audioBG, "share_img_audio_bg"),
                        Pair.create(audioRing, mContext.getResources().getString(R.string.share_img)));
        Intent audioIntent = new Intent(mContext, AudioActivity.class);
        mContext.startActivity(audioIntent, options.toBundle());
    }
}
