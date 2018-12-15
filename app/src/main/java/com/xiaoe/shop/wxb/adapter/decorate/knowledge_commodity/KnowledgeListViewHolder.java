package com.xiaoe.shop.wxb.adapter.decorate.knowledge_commodity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.xiaoe.common.entitys.ComponentInfo;
import com.xiaoe.common.utils.MeasureUtil;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseViewHolder;
import com.xiaoe.shop.wxb.business.course_more.ui.CourseMoreActivity;
import com.xiaoe.shop.wxb.common.JumpDetail;
import com.xiaoe.shop.wxb.events.OnClickEvent;

// RecyclerView 的 ViewHolder
public class KnowledgeListViewHolder extends BaseViewHolder {

    private static final String TAG = "KnowledgeListViewHolder";

    Context mContext;

    @BindView(R.id.list_wrap)
    public FrameLayout knowledgeListWrap;
    @BindView(R.id.knowledge_list_title)
    public TextView knowledgeListTitle;
    @BindView(R.id.knowledge_list_more)
    public TextView knowledgeListMore;
    @BindView(R.id.knowledge_list_list_view)
    public ListView knowledgeListView;

    boolean isSearch = false;

    public void setSearch(boolean search) {
        isSearch = search;
    }

    public KnowledgeListViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public KnowledgeListViewHolder(Context context, View itemView) {
        super(itemView);
        this.mContext = context;
        ButterKnife.bind(this, itemView);
    }

    public void initViewHolder(ComponentInfo currentBindComponent, int currentBindPos, SparseArray<KnowledgeListAdapter> knowledgeListAdapterArr) {
        KnowledgeListAdapter knowledgeListAdapter;
        if (knowledgeListAdapterArr.get(currentBindPos) == null || isSearch) {
            knowledgeListAdapter = new KnowledgeListAdapter(mContext, currentBindComponent.getKnowledgeCommodityItemList());
            knowledgeListAdapterArr.put(currentBindPos, knowledgeListAdapter);
        } else {
            knowledgeListAdapter = knowledgeListAdapterArr.get(currentBindPos);
        }
        if (currentBindComponent.isHideTitle()) {
            knowledgeListTitle.setVisibility(View.GONE);
            // title 没有了需要讲 listView 的 marginTop 设置为 0
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 0, 0, 0);
            knowledgeListView.setLayoutParams(layoutParams);
        } else {
            knowledgeListTitle.setText(currentBindComponent.getTitle());
            if (TextUtils.isEmpty(currentBindComponent.getDesc())) { // 为空，不显示查看全部
                knowledgeListMore.setVisibility(View.GONE);
            } else { // 显示查看更多
                knowledgeListMore.setVisibility(View.VISIBLE);
            }
            if (knowledgeListMore.getVisibility() == View.VISIBLE) { // 列表形式，查看全部可见
                knowledgeListMore.setOnClickListener(new OnClickEvent(OnClickEvent.DEFAULT_SECOND) {
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
                        } else { // 默认跳转到搜索更多页面
                            JumpDetail.jumpSearchMore(mContext, currentBindComponent.getJoinedDesc(), currentBindComponent.getSearchType());
                        }
                    }
                });
            }
        }
        knowledgeListView.setAdapter(knowledgeListAdapter);
        knowledgeListWrap.setBackgroundColor(currentBindComponent.getKnowledgeCompBg());
        MeasureUtil.setListViewHeightBasedOnChildren(knowledgeListView);
    }
}
