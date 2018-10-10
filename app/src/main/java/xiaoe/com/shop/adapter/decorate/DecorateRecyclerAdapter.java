package xiaoe.com.shop.adapter.decorate;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;

import java.util.List;

import xiaoe.com.common.entitys.ComponentInfo;
import xiaoe.com.common.entitys.DecorateEntityType;
import xiaoe.com.shop.R;
import xiaoe.com.shop.adapter.decorate.flow_info.FlowInfoAudioViewHolder;
import xiaoe.com.shop.adapter.decorate.flow_info.FlowInfoImgTextViewHolder;
import xiaoe.com.shop.adapter.decorate.flow_info.FlowInfoVideoViewHolder;
import xiaoe.com.shop.adapter.decorate.knowledge_commodity.KnowledgeGroupRecyclerAdapter;
import xiaoe.com.shop.adapter.decorate.knowledge_commodity.KnowledgeGroupRecyclerItemDecoration;
import xiaoe.com.shop.adapter.decorate.knowledge_commodity.KnowledgeGroupViewHolder;
import xiaoe.com.shop.adapter.decorate.knowledge_commodity.KnowledgeListAdapter;
import xiaoe.com.shop.adapter.decorate.knowledge_commodity.KnowledgeListViewHolder;
import xiaoe.com.shop.adapter.decorate.recent_update.RecentUpdateListAdapter;
import xiaoe.com.shop.adapter.decorate.recent_update.RecentUpdateViewHolder;
import xiaoe.com.shop.adapter.decorate.shuffling_figure.PicViewHolder;
import xiaoe.com.shop.adapter.decorate.shuffling_figure.ShufflingFigureViewHolder;
import xiaoe.com.shop.base.BaseViewHolder;
import xiaoe.com.shop.business.audio.ui.AudioActivity;
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
        // 获取组件的显示类型
        String subType = currentComponent.getSubType();
        switch(viewType) {
            case DecorateEntityType.FLOW_INFO:
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
                            mActivity.startActivity(transitionIntent, options.toBundle());
                            }
                        });
                        // TODO: 设想在 touch 事件中实现 view 的缩放效果
                        return new FlowInfoImgTextViewHolder(view);
                    case DecorateEntityType.FLOW_INFO_AUDIO:  // 音频
                        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flow_info_audio, null);
                        final View audioBG = view.findViewById(R.id.flow_info_audio_bg);
                        final View audioTitle = view.findViewById(R.id.flow_info_audio_desc);
                        view.setLayoutParams(layoutParams);
                        if (currentComponent.isHasBuy()) {
                            view.findViewById(R.id.flow_info_img_text_price).setVisibility(View.GONE);
                        }
                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ActivityOptions options =
                                    ActivityOptions.makeSceneTransitionAnimation(mActivity,
                                        Pair.create(audioBG, "audio_bg_share"),
                                        Pair.create(audioTitle, "share_audio_title"));
                                Intent audioIntent = new Intent(mActivity, AudioActivity.class);
                                mActivity.startActivity(audioIntent, options.toBundle());
                            }
                        });
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
                view.setLayoutParams(layoutParams);
                return new RecentUpdateViewHolder(view);
            case DecorateEntityType.KNOWLEDGE_COMMODITY:
                switch (subType) {
                    case DecorateEntityType.KNOWLEDGE_LIST:
                        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.knowledge_commodity_list, null);
                        view.setLayoutParams(layoutParams);
                        return new KnowledgeListViewHolder(view);
                    case DecorateEntityType.KNOWLEDGE_GROUP:
                        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.knowledge_commodity_group, null);
                        view.setLayoutParams(layoutParams);
                        return new KnowledgeGroupViewHolder(view);
                }
            case DecorateEntityType.SHUFFLING_FIGURE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shuffling_figure, null);
                view.setLayoutParams(layoutParams);
                return new ShufflingFigureViewHolder(view);
            case DecorateEntityType.BOOKCASE: // 书架的 case 本次不做
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
        // 需要根据是那种信息流来加载不同的布局
        String subType = currentComponent.getSubType();
        switch(itemType) {
            case DecorateEntityType.FLOW_INFO:
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
                        audioViewHolder.flowInfoBg.setImageURI(Uri.parse("res:///" + R.mipmap.audio_bg));
                        audioViewHolder.flowInfoAvatar.setImageURI(Uri.parse("res:///" + R.mipmap.audio_ring));
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
                RecentUpdateViewHolder recentUpdateViewHolder = (RecentUpdateViewHolder) holder;
                recentUpdateViewHolder.recentUpdateAvatar.setImageURI("res:///" + R.mipmap.audio_ring);
                recentUpdateViewHolder.recentUpdateSubTitle.setText(currentComponent.getTitle());
                recentUpdateViewHolder.recentUpdateSubDesc.setText(currentComponent.getDesc());
                recentUpdateViewHolder.recentUpdateSubBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    Toast.makeText(mActivity, "点击了按钮", Toast.LENGTH_SHORT).show();
                    }
                });
                // 加载 ListView 的数据
                RecentUpdateListAdapter adapter = new RecentUpdateListAdapter(mActivity, currentComponent.getSubList());
                recentUpdateViewHolder.recentUpdateListView.setAdapter(adapter);
                setListViewHeightBasedOnChildren(recentUpdateViewHolder.recentUpdateListView);
                break;
            case DecorateEntityType.KNOWLEDGE_COMMODITY:
                switch (subType) {
                    case DecorateEntityType.KNOWLEDGE_LIST:
                        KnowledgeListViewHolder knowledgeListViewHolder = (KnowledgeListViewHolder) holder;
                        KnowledgeListAdapter knowledgeListAdapter = new KnowledgeListAdapter(mActivity, currentComponent.getKnowledgeCommodityItemList());
                        knowledgeListViewHolder.knowledgeListView.setAdapter(knowledgeListAdapter);
                        setListViewHeightBasedOnChildren(knowledgeListViewHolder.knowledgeListView);
                        break;
                    case DecorateEntityType.KNOWLEDGE_GROUP:
                        KnowledgeGroupViewHolder knowledgeGroupViewHolder = (KnowledgeGroupViewHolder) holder;
                        knowledgeGroupViewHolder.groupTitle.setText(currentComponent.getTitle());
                        knowledgeGroupViewHolder.groupMore.setText(currentComponent.getDesc());
                        GridLayoutManager lm = new GridLayoutManager(mActivity, 2);
                        knowledgeGroupViewHolder.groupRecyclerView.setLayoutManager(lm);
                        knowledgeGroupViewHolder.groupRecyclerView.addItemDecoration(new KnowledgeGroupRecyclerItemDecoration(15, 2));
                        KnowledgeGroupRecyclerAdapter groupAdapter = new KnowledgeGroupRecyclerAdapter(mActivity, currentComponent.getKnowledgeCommodityItemList());
                        knowledgeGroupViewHolder.groupRecyclerView.setAdapter(groupAdapter);
                        break;
                }
                break;
            case DecorateEntityType.SHUFFLING_FIGURE:
                // TODO: 问题 -- 轮播图只显示其中两张
                ShufflingFigureViewHolder shufflingFigureViewHolder = (ShufflingFigureViewHolder) holder;
                shufflingFigureViewHolder.convenientBanner.setPages(new CBViewHolderCreator() {
                    @Override
                    public Holder createHolder(View itemView) {
                        return new PicViewHolder(itemView);
                    }

                    @Override
                    public int getLayoutId() {
                        return R.layout.sd_layout;
                    }
                }, currentComponent.getShufflingList());
                break;
            case DecorateEntityType.BOOKCASE:
                break;
            case DecorateEntityType.GRAPHIC_NAVIGATION:
                break;
            default:
                break;
        }
    }

    /**
     * 根据 ListView 的子项计算高度
     * @param recentUpdateSubList 需要计算的 ListView
     */
    private void setListViewHeightBasedOnChildren(ListView recentUpdateSubList) {
        // 获取 ListView 对应的 Adapter
        ListAdapter adapter = recentUpdateSubList.getAdapter();
        if (adapter == null) {
            return;
        }
        int totalHeight = 0;
        for(int i = 0, len = adapter.getCount(); i < len; i++) {
            // 拿到每个 View
            View listItem = adapter.getView(i, null, recentUpdateSubList);
            // 计算宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = recentUpdateSubList.getLayoutParams();
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        params.height = totalHeight + (recentUpdateSubList.getDividerHeight() * (adapter.getCount() - 1));
        recentUpdateSubList.setLayoutParams(params);
    }

    @Override
    public int getItemCount() {
        return mComponentList.size();
    }

    @Override
    public int getItemViewType(int position) {
        currentPos = position;
        currentComponent = mComponentList.get(position);
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
