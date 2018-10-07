package xiaoe.com.shop.adapter.decorate;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import xiaoe.com.common.entitys.ComponentInfo;
import xiaoe.com.common.entitys.DecorateEntityType;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;
import xiaoe.com.shop.business.course.ui.CourseDetailActivity;

/**
 * 店铺装修组件显示列表适配器
 */
public class DecorateRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private static final String TAG = "DecorateRecyclerAdapter";

    private Context mContext;
    private Activity mActivity;
    // 组件实体类列表
    private List<ComponentInfo> mComponentList;
    // 组件 ViewHolder 列表(Integer 为组件的类型，BaseViewHolder 为组件 ViewHolder的父类)
    // private Map<Integer, BaseViewHolder> mViewHolderList;
    // 当前下标
    private int currentPos;
    // 当前 item
    private ComponentInfo currentComponent;

    public DecorateRecyclerAdapter(Context context, List<ComponentInfo> componentList) {
        this.mContext = context;
        this.mComponentList = componentList;
    }

    public DecorateRecyclerAdapter(Activity activity, List<ComponentInfo> componentList) {
        this.mActivity = activity;
        this.mComponentList = componentList;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = null;
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0,0,0,30);
        currentComponent = mComponentList.get(currentPos);
        switch(viewType) {
            case DecorateEntityType.FLOW_INFO:
                // 需要根据是那种信息流来加载不同的布局
                String subType = currentComponent.getSubType();
                switch (subType) {
                    case DecorateEntityType.FLOW_INFO_IMG_TEXT:  // 图文
                        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flow_info_img_text, null);
                        view.setLayoutParams(layoutParams);
                        if (currentComponent.isHasBuy()) { // 买了，价格就不显示
                            view.findViewById(R.id.flow_info_img_text_price).setVisibility(View.GONE);
                        }
                        final View imageView = view.findViewById(R.id.flow_info_img_text_bg);
                        final View textView = view.findViewById(R.id.flow_info_img_text_title);
                        final String imgUrl = currentComponent.getImgUrl();
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ActivityOptions options =
                                        ActivityOptions.makeSceneTransitionAnimation(mActivity,
                                                Pair.create(imageView, "share"),
                                                Pair.create(textView, "text"));
                                Intent transitionIntent = new Intent(mActivity, CourseDetailActivity.class);
                                transitionIntent.putExtra("type", DecorateEntityType.FLOW_INFO_IMG_TEXT);
                                transitionIntent.putExtra("imgUrl", imgUrl);
                                mActivity.startActivityForResult(transitionIntent, 101, options.toBundle());
                            }
                        });
                        return new FlowInfoImgTextViewHolder(view);
                    case DecorateEntityType.FLOW_INFO_AUDIO:  // 音频
                        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flow_info_audio, null);
                        view.setLayoutParams(layoutParams);
                        if (currentComponent.isHasBuy()) {
                            view.findViewById(R.id.flow_info_img_text_price).setVisibility(View.GONE);
                        }
                        return new FlowInfoAudioViewHolder(view);
                    case DecorateEntityType.FLOW_INFO_VIDEO:  // 视频
                        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flow_info_video, null);
                        view.setLayoutParams(layoutParams);
                        if (currentComponent.isHasBuy()) {
                            view.findViewById(R.id.flow_info_video_price).setVisibility(View.GONE);
                        }
                        return new FlowInfoVideoViewHolder(view);
                }
            case DecorateEntityType.RECENT_UPDATE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_update, null);
                return null;
            case DecorateEntityType.KNOWLEDGE_COMMODITY:
                break;
            case DecorateEntityType.SHUFFLING_FIGURE:
                break;
            case DecorateEntityType.BOOKCASE:
                break;
            case DecorateEntityType.GRAPHIC_NAVIGATION:
                break;
            default:
                break;
        }
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        int itemType = getItemViewType(position);
        if (itemType == -1) {
            Log.d(TAG, "onBindViewHolder: error return -1");
            return;
        }
        String price = "";
        switch(itemType) {
            case DecorateEntityType.FLOW_INFO:
                // 需要根据是那种信息流来加载不同的布局
                String subType = currentComponent.getSubType();
                switch (subType) {
                    case DecorateEntityType.FLOW_INFO_IMG_TEXT:  // 图文
                        FlowInfoImgTextViewHolder itViewHolder = (FlowInfoImgTextViewHolder) holder;
                        itViewHolder.flowInfoBg.setImageURI(currentComponent.getImgUrl());
                        itViewHolder.flowInfoTitle.setText(currentComponent.getTitle());
                        itViewHolder.flowInfoDesc.setText(currentComponent.getDesc());
                        price = "￥" + currentComponent.getPrice();
                        itViewHolder.flowInfoPrice.setText(price);
                        break;
                    case DecorateEntityType.FLOW_INFO_AUDIO:  // 音频
                        FlowInfoAudioViewHolder audioViewHolder = (FlowInfoAudioViewHolder) holder;
                        audioViewHolder.flowInfoBg.setImageURI(Uri.parse("res:///" + R.drawable.audio_bg));
                        audioViewHolder.flowInfoAvatar.setImageURI(Uri.parse("res:///" + R.drawable.audio_ring));
                        audioViewHolder.flowInfoTitle.setText(currentComponent.getTitle());
                        audioViewHolder.flowInfoDesc.setText(currentComponent.getDesc());
                        audioViewHolder.flowInfoPrice.setText(currentComponent.getPrice());
                        String joinedDesc = currentComponent.getJoinedDesc() + "人在听";
                        audioViewHolder.flowInfoJoinedDesc.setText(joinedDesc);
                        price = "￥" + currentComponent.getPrice();
                        audioViewHolder.flowInfoPrice.setText(price);
                        break;
                    case DecorateEntityType.FLOW_INFO_VIDEO:  // 视频
                        FlowInfoVideoViewHolder videoViewHolder = (FlowInfoVideoViewHolder) holder;
                        videoViewHolder.flowInfoBg.setImageURI(currentComponent.getImgUrl());
                        videoViewHolder.flowInfoTitle.setText(currentComponent.getTitle());
                        videoViewHolder.flowInfoDesc.setText(currentComponent.getDesc());
                        videoViewHolder.flowInfoPrice.setText(currentComponent.getPrice());
                        break;
                }
                break;
            case DecorateEntityType.RECENT_UPDATE:

                break;
            case DecorateEntityType.KNOWLEDGE_COMMODITY:
                break;
            case DecorateEntityType.SHUFFLING_FIGURE:
                break;
            case DecorateEntityType.BOOKCASE:
                break;
            case DecorateEntityType.GRAPHIC_NAVIGATION:
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mComponentList.size();
    }

    @Override
    public int getItemViewType(int position) {
        currentPos = position;
        if (currentComponent == null) {
            currentComponent = mComponentList.get(position);
        }
        String type = currentComponent.getType();
        switch (type) {
            case DecorateEntityType.FLOW_INFO_STR:
                return DecorateEntityType.FLOW_INFO;
            case DecorateEntityType.RECENT_UPDATE_STR:
                return DecorateEntityType.RECENT_UPDATE;
            case DecorateEntityType.KNOWLEDGE_COMMODITY_STR:
                return DecorateEntityType.KNOWLEDGE_COMMODITY;
            case DecorateEntityType.SHUFFLING_FIGURE_STR:
                return DecorateEntityType.SHUFFLING_FIGURE;
            case DecorateEntityType.BOOKCASE_STR:
                return DecorateEntityType.BOOKCASE;
            case DecorateEntityType.GRAPHIC_NAVIGATION_STR:
                return DecorateEntityType.GRAPHIC_NAVIGATION;
            default:
                return -1;
        }
    }
}
