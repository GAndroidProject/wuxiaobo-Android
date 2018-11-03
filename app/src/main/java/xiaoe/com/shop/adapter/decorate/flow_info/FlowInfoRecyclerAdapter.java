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
import android.widget.Toast;

import java.util.List;

import xiaoe.com.common.entitys.DecorateEntityType;
import xiaoe.com.common.entitys.FlowInfoItem;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;
import xiaoe.com.shop.business.audio.ui.AudioActivity;
import xiaoe.com.shop.business.course.ui.CourseImageTextActivity;
import xiaoe.com.shop.business.video.ui.VideoActivity;

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
                final View imgTxtBg = view.findViewById(R.id.flow_info_img_text_bg);
                final View imgTxtTitle = view.findViewById(R.id.flow_info_img_text_title);
                final String imgUrl = currentItem.getItemImg();
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    ActivityOptions options =
                        ActivityOptions.makeSceneTransitionAnimation((Activity) mContext,
                            Pair.create(imgTxtBg, mContext.getResources().getString(R.string.share_img)),
                            Pair.create(imgTxtTitle, mContext.getResources().getString(R.string.share_txt)));
                    Intent transitionIntent = new Intent(mContext, CourseImageTextActivity.class);
                    transitionIntent.putExtra("type", DecorateEntityType.FLOW_INFO_IMG_TEXT_STR);
                    transitionIntent.putExtra("imgUrl", imgUrl);
                    mContext.startActivity(transitionIntent, options.toBundle());
                    }
                });
                return new FlowInfoImgTextViewHolder(view);
            // 图文、专栏、大专栏都是用一个布局
            case DecorateEntityType.FLOW_INFO_COLUMN:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flow_info_img_text, null);
                if (currentItem.isItemHasBuy()) {
                    view.findViewById(R.id.flow_info_img_text_price).setVisibility(View.GONE);
                }
                view.setLayoutParams(layoutParams);
                final View columnBg = view.findViewById(R.id.flow_info_img_text_bg);
                final View columnTitle = view.findViewById(R.id.flow_info_img_text_title);
                final String columnImgUrl = currentItem.getItemImg();
                // TODO: 跳转到专栏
                return new FlowInfoImgTextViewHolder(view);
            case DecorateEntityType.FLOW_INFO_TOPIC:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flow_info_img_text, null);
                if (currentItem.isItemHasBuy()) {
                    view.findViewById(R.id.flow_info_img_text_price).setVisibility(View.GONE);
                }
                view.setLayoutParams(layoutParams);
                final View topicBg = view.findViewById(R.id.flow_info_img_text_bg);
                final View topicTitle = view.findViewById(R.id.flow_info_img_text_title);
                final String topicImgUrl = currentItem.getItemImg();
                // TODO: 跳转大专栏
                return new FlowInfoImgTextViewHolder(view);
            case DecorateEntityType.FLOW_INFO_AUDIO: // 音频
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flow_info_audio, null);
                view.setLayoutParams(layoutParams);
                final View audioBG = view.findViewById(R.id.flow_info_audio_bg);
                final View audioRing = view.findViewById(R.id.flow_info_audio_avatar);
                if (currentItem.isItemHasBuy()) {
                    view.findViewById(R.id.flow_info_img_text_price).setVisibility(View.GONE);
                }
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    jumpAudioActivity(audioBG, audioRing);
                    }
                });
                return new FlowInfoAudioViewHolder(view);
            case DecorateEntityType.FLOW_INFO_VIDEO: // 视频
                final String videoImageUrl = currentItem.getItemImg();
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flow_info_video, null);
                final View videoBG = view.findViewById(R.id.flow_info_video_bg);
                view.setLayoutParams(layoutParams);
                if (currentItem.isItemHasBuy()) {
                    view.findViewById(R.id.flow_info_video_price).setVisibility(View.GONE);
                }
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityOptions options =
                                ActivityOptions.makeSceneTransitionAnimation((Activity) mContext,
                                        Pair.create(videoBG, "share_video_preview"));
                        Intent videoIntent = new Intent(mContext, VideoActivity.class);
                        videoIntent.putExtra("videoImageUrl", videoImageUrl);
                        mContext.startActivity(videoIntent, options.toBundle());
                    }
                });
                return new FlowInfoVideoViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int itemType = getItemViewType(position);
        if (itemType == -1) {
            Log.d(TAG, "onBindViewHolder: flow_info component viewType get -1");
            return;
        }
        switch (itemType) {
            case DecorateEntityType.FLOW_INFO_IMG_TEXT: // 图文
            case DecorateEntityType.FLOW_INFO_COLUMN: // 专栏
            case DecorateEntityType.FLOW_INFO_TOPIC: // 大专栏
                FlowInfoImgTextViewHolder itViewHolder = (FlowInfoImgTextViewHolder) holder;
                itViewHolder.flowInfoBg.setImageURI(currentItem.getItemImg());
                itViewHolder.flowInfoTitle.setText(currentItem.getItemTitle());
                itViewHolder.flowInfoDesc.setText(currentItem.getItemDesc());
                itViewHolder.flowInfoPrice.setText(currentItem.getItemPrice());
                break;
            case DecorateEntityType.FLOW_INFO_AUDIO: // 音频
                FlowInfoAudioViewHolder audioViewHolder = (FlowInfoAudioViewHolder) holder;
                audioViewHolder.flowInfoBg.setImageURI(Uri.parse("res:///" + R.mipmap.audio_bg));
                audioViewHolder.flowInfoAvatar.setImageURI(Uri.parse("res:///" + R.mipmap.audio_ring));
                audioViewHolder.flowInfoTitle.setText(currentItem.getItemTitle());
                audioViewHolder.flowInfoDesc.setText(currentItem.getItemDesc());
                audioViewHolder.flowInfoPrice.setText(currentItem.getItemPrice());
                String joinedDesc = currentItem.getItemJoinedDesc() + "人在听";
                audioViewHolder.flowInfoJoinedDesc.setText(joinedDesc);
                audioViewHolder.flowInfoPrice.setText(currentItem.getItemPrice());
                break;
            case DecorateEntityType.FLOW_INFO_VIDEO: // 视频
                FlowInfoVideoViewHolder videoViewHolder = (FlowInfoVideoViewHolder) holder;
                videoViewHolder.flowInfoBg.setImageURI(currentItem.getItemImg());
                videoViewHolder.flowInfoTitle.setText(currentItem.getItemTitle());
                videoViewHolder.flowInfoDesc.setText(currentItem.getItemDesc());
                videoViewHolder.flowInfoPrice.setText(currentItem.getItemPrice());
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
