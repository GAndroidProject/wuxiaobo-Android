package com.xiaoe.shop.wxb.adapter.decorate.knowledge_commodity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.xiaoe.common.entitys.ComponentInfo;
import com.xiaoe.common.entitys.DecorateEntityType;
import com.xiaoe.common.entitys.KnowledgeCommodityItem;
import com.xiaoe.common.interfaces.OnItemClickWithKnowledgeListener;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseViewHolder;
import com.xiaoe.shop.wxb.business.course_more.ui.CourseMoreActivity;
import com.xiaoe.shop.wxb.common.JumpDetail;
import com.xiaoe.shop.wxb.events.OnClickEvent;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class KnowledgeGroupViewHolder extends BaseViewHolder implements OnItemClickWithKnowledgeListener {

    private static final String TAG = "KnowledgeGroupVh";

    Context mContext;

    @BindView(R.id.group_wrap)
    public FrameLayout groupWrap;
    @BindView(R.id.knowledge_group_title)
    public TextView groupTitle;
    @BindView(R.id.knowledge_group_more)
    public TextView groupMore;
    @BindView(R.id.knowledge_group_recycler)
    public RecyclerView groupRecyclerView;

    private List<RecyclerView> knowledgeGroupRecyclerList;
    private boolean localDecorate = true;

    public KnowledgeGroupViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public KnowledgeGroupViewHolder(Context context, View itemView) {
        super(itemView);
        this.mContext = context;
        ButterKnife.bind(this, itemView);
    }

    public void setKnowledgeGroupRecyclerList(List<RecyclerView> knowledgeGroupRecyclerList) {
        this.knowledgeGroupRecyclerList = knowledgeGroupRecyclerList;
    }

    public void initViewHolder(ComponentInfo currentBindComponent, int currentBindPos, SparseArray<KnowledgeGroupRecyclerAdapter> knowledgeGroupRecyclerAdapterArr) {
        KnowledgeGroupRecyclerAdapter knowledgeGroupRecyclerAdapter;
        if (currentBindComponent.isHideTitle()) {
            groupTitle.setVisibility(View.GONE);
            groupMore.setVisibility(View.GONE);
        } else {
            groupTitle.setText(currentBindComponent.getTitle());
            groupMore.setText(currentBindComponent.getDesc());
            groupMore.setOnClickListener(new OnClickEvent(OnClickEvent.DEFAULT_SECOND) {
                @Override
                public void singleClick(View v) {
                    if (currentBindComponent.isInMicro()) {
                        // 跳转到更多课程的页面
                        String groupId = currentBindComponent.getGroupId();
                        if (TextUtils.isEmpty(groupId)) {
                            Log.d(TAG, "singleClick: 没有 groupId");
                            return;
                        }
                        JumpDetail.jumpCourseMore(mContext, groupId);
                    } else {
                        // do nothing
                        Log.d(TAG, "singleClick: 扩展位置");
                    }
                }
            });
        }
        if (groupRecyclerView.getLayoutManager() == null) {
            GridLayoutManager lm = new GridLayoutManager(mContext, 2);
            groupRecyclerView.setLayoutManager(lm);
        }
        if (!knowledgeGroupRecyclerList.contains(groupRecyclerView)) { // 防止重复添加
            knowledgeGroupRecyclerList.add(groupRecyclerView);
        }
        if (currentBindComponent.isNeedDecorate() && localDecorate) {
            groupRecyclerView.addItemDecoration(new KnowledgeGroupRecyclerItemDecoration(Dp2Px2SpUtil.dp2px(mContext, 16), 2));
            currentBindComponent.setNeedDecorate(false);
            localDecorate = false;
        }
        if (knowledgeGroupRecyclerAdapterArr.get(currentBindPos) == null) {
            knowledgeGroupRecyclerAdapter = new KnowledgeGroupRecyclerAdapter(mContext, currentBindComponent.getKnowledgeCommodityItemList());;
            knowledgeGroupRecyclerAdapterArr.put(currentBindPos, knowledgeGroupRecyclerAdapter);
        } else {
            knowledgeGroupRecyclerAdapter = knowledgeGroupRecyclerAdapterArr.get(currentBindPos);
        }
        knowledgeGroupRecyclerAdapter.setOnItemClickWithKnowledgeListener(this);
        groupRecyclerView.setAdapter(knowledgeGroupRecyclerAdapter);
        groupWrap.setBackgroundColor(currentBindComponent.getKnowledgeCompBg());
    }

    @Override
    public void onKnowledgeItemClick(View view, KnowledgeCommodityItem knowledgeCommodityItem) {
        // 知识商品分组形式
        for (RecyclerView itemRecycler : knowledgeGroupRecyclerList) {
            Log.d(TAG, "onKnowledgeItemClick: view.parent() == itemRecycler ---> " + (view.getParent() == itemRecycler));
            if (view.getParent() == itemRecycler) {
                switch (knowledgeCommodityItem.getSrcType()) {
                    case DecorateEntityType.IMAGE_TEXT: // 图文 -- resourceType 为 1，resourceId 需要取
                        JumpDetail.jumpImageText(mContext, knowledgeCommodityItem.getResourceId(), knowledgeCommodityItem.getItemImg(), "");
                        break;
                    case DecorateEntityType.AUDIO: // 音频
                        JumpDetail.jumpAudio(mContext, knowledgeCommodityItem.getResourceId(), 0);
                        break;
                    case DecorateEntityType.VIDEO: // 视频
                        JumpDetail.jumpVideo(mContext, knowledgeCommodityItem.getResourceId(), "", false, "");
                        break;
                    case DecorateEntityType.COLUMN: // 专栏
                        JumpDetail.jumpColumn(mContext, knowledgeCommodityItem.getResourceId(), knowledgeCommodityItem.getItemImg(), 6);
                        break;
                    case DecorateEntityType.TOPIC: // 大专栏
                        JumpDetail.jumpColumn(mContext, knowledgeCommodityItem.getResourceId(), knowledgeCommodityItem.getItemImg(), 8);
                        break;
                    case DecorateEntityType.MEMBER: // 会员
                        JumpDetail.jumpColumn(mContext, knowledgeCommodityItem.getResourceId(), knowledgeCommodityItem.getItemImg(), 5);
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
