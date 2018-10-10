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

import java.util.List;

import xiaoe.com.common.entitys.DecorateEntityType;
import xiaoe.com.common.entitys.FlowInfoItem;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;
import xiaoe.com.shop.business.audio.ui.AudioActivity;
import xiaoe.com.shop.business.course.ui.CourseDetailActivity;

public class FlowInfoRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final String TAG = "FlowInfoRecyclerAdapter";

    private Context mContext;
    private Activity mActivity;
    private List<FlowInfoItem> mItemList;

    private int currentPos;
    private FlowInfoItem currentItem;

    public FlowInfoRecyclerAdapter(Context context, List<FlowInfoItem> list) {
        this.mContext = context;
        this.mItemList = list;
    }

    public FlowInfoRecyclerAdapter(Activity activity, List<FlowInfoItem> list) {
        this.mActivity = activity;
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
                final View imageView = view.findViewById(R.id.flow_info_img_text_bg);
                final View textView = view.findViewById(R.id.flow_info_img_text_title);
                final String imgUrl = currentItem.getItemImg();
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    ActivityOptions options =
                        ActivityOptions.makeSceneTransitionAnimation(mActivity,
                            Pair.create(imageView, "share"),
                            Pair.create(textView, "text"));
                    Intent transitionIntent = new Intent(mActivity, CourseDetailActivity.class);
                    transitionIntent.putExtra("type", DecorateEntityType.FLOW_INFO_IMG_TEXT_STR);
                    transitionIntent.putExtra("imgUrl", imgUrl);
                    mActivity.startActivity(transitionIntent, options.toBundle());
                    }
                });
                // TODO: 设想在 touch 事件中实现 view 的缩放效果
                return new FlowInfoImgTextViewHolder(view);
            case DecorateEntityType.FLOW_INFO_AUDIO: // 音频
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flow_info_audio, null);
                view.setLayoutParams(layoutParams);
                final View audioBG = view.findViewById(R.id.flow_info_audio_bg);
                if (currentItem.isItemHasBuy()) {
                    view.findViewById(R.id.flow_info_img_text_price).setVisibility(View.GONE);
                }
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    ActivityOptions options =
                        ActivityOptions.makeSceneTransitionAnimation(mActivity,
                            Pair.create(audioBG, "audio_bg_share"));
                    Intent audioIntent = new Intent(mActivity, AudioActivity.class);
                    mActivity.startActivity(audioIntent, options.toBundle());
                    }
                });
                return new FlowInfoAudioViewHolder(view);
            case DecorateEntityType.FLOW_INFO_VIDEO: // 视频
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flow_info_video, null);
                view.setLayoutParams(layoutParams);
                if (currentItem.isItemHasBuy()) {
                    view.findViewById(R.id.flow_info_video_price).setVisibility(View.GONE);
                }
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
            default:
                return -1;
        }
    }
}
