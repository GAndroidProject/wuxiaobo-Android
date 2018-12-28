package com.xiaoe.shop.wxb.adapter.download;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.xiaoe.common.entitys.ExpandableItem;
import com.xiaoe.common.entitys.ExpandableLevel;
import com.xiaoe.common.utils.DateFormat;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.common.widget.CommonRefreshHeader;
import com.xiaoe.shop.wxb.R;

import java.util.List;

/**
 * 下载列表适配器
 * @author: zak
 * @date: 2018/12/24
 */
public class DownLoadListAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {

    public static final int GROUP_TITLE_ITEM = 0; // 非单品待展开
    public static final int GROUP_CONTENT_ITEM = 1; // 非单品展开
    public static final int GROUP_BOTTOM_ITEM = 2; // 非单品展开最后一项
    public static final int LOAD_STATE = 3; // 加载布局
    public static final int SINGLE_ITEM = 4; // 单品

    //0：未加载，1：加载中，2：加载完成，3：加载失败
    public static final int NO_LOAD = 0;
    public static final int LOADING = 1;
    public static final int LOADING_FINISH = 2;
    public static final int LOAD_FAIL = 3;

    private Context mContext;
    FrameLayout.LayoutParams layoutParams;

    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public DownLoadListAdapter(Context context, List<MultiItemEntity> data) {
        super(data);
        this.mContext = context;
        addItemType(SINGLE_ITEM, R.layout.download_signal);
        addItemType(GROUP_TITLE_ITEM, R.layout.download_group_title_item);
        addItemType(GROUP_CONTENT_ITEM, R.layout.download_group_content_item);
        addItemType(GROUP_BOTTOM_ITEM, R.layout.download_group_bottom_item);
        addItemType(LOAD_STATE, R.layout.layout_expandable_load);
        layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Dp2Px2SpUtil.dp2px(mContext, 56));
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {
        switch (helper.getItemViewType()) {
            case SINGLE_ITEM:
                initSingleItem(helper, item);
                break;
            case GROUP_TITLE_ITEM:
                initGroupTitleItem(helper, item);
                break;
            case GROUP_CONTENT_ITEM:
                initGroupContentItem(helper, item);
                break;
            case GROUP_BOTTOM_ITEM:
                initGroupBottomItem(helper, item);
                break;
            case LOAD_STATE:
                initLoadStateItem(helper, item);
                break;
            default:
                break;
        }
    }

    /**
     * 初始化单品 item（未兼容）
     * @param helper viewHolder
     * @param item   item
     */
    private void initSingleItem(BaseViewHolder helper, MultiItemEntity item) {
        ExpandableItem expandableItem = (ExpandableItem) item;
        TextView singleContent = helper.getView(R.id.single_title);
        TextView singleTime = helper.getView(R.id.single_time);

        singleContent.setText(expandableItem.getTitle());
        int type = expandableItem.getResource_type();
        if (type == 2) { // 音视频显示时间
            singleTime.setVisibility(View.VISIBLE);
            singleTime.setText(DateFormat.longToString(expandableItem.getAudio_length() * 1000));
        } else if (type == 3) {
            singleTime.setVisibility(View.VISIBLE);
            singleTime.setText(DateFormat.longToString(expandableItem.getVideo_length() * 1000));
        } else {
            singleTime.setVisibility(View.GONE);
        }

        // 选择点击事件
        helper.addOnClickListener(R.id.single_wrap);
    }

    /**
     * 初始化非单品 title item
     * @param helper viewHolder
     * @param item   item
     */
    private void initGroupTitleItem(BaseViewHolder helper, MultiItemEntity item) {
        ExpandableLevel expandableLevel = (ExpandableLevel) item;
        FrameLayout groupTitleWrap = helper.getView(R.id.group_title_wrap);
        TextView groupTitleHead = helper.getView(R.id.group_title_head);
        TextView groupTitleTail = helper.getView(R.id.group_title_tail);
        View groupTitleExpandLine = helper.getView(R.id.group_title_expand_line);
        View groupTitleBottomLine = helper.getView(R.id.group_title_bottom_line);

        if (expandableLevel.isSelect()) {
            groupTitleHead.setCompoundDrawablesWithIntrinsicBounds(mContext.getDrawable(R.mipmap.download_checking), null, null, null);
        } else {
            groupTitleHead.setCompoundDrawablesWithIntrinsicBounds(mContext.getDrawable(R.mipmap.download_tocheck), null, null, null);
        }

        groupTitleHead.setText(expandableLevel.getTitle());
        if (expandableLevel.isExpand()) {
            groupTitleTail.setVisibility(View.GONE);
            groupTitleExpandLine.setVisibility(View.VISIBLE);
            groupTitleBottomLine.setVisibility(View.GONE);
            layoutParams.setMargins(0, 0, 0, 0);
            groupTitleWrap.setLayoutParams(layoutParams);
        } else {
            groupTitleTail.setVisibility(View.VISIBLE);
            groupTitleExpandLine.setVisibility(View.GONE);
            groupTitleBottomLine.setVisibility(View.VISIBLE);
            layoutParams.setMargins(0, 0, 0, Dp2Px2SpUtil.dp2px(mContext, 16));
            groupTitleWrap.setLayoutParams(layoutParams);
        }

        // 展开点击事件
        helper.addOnClickListener(R.id.group_title_tail);
        // 全选点击事件
        helper.addOnClickListener(R.id.group_title_head);
    }

    /**
     * 初始化非单品 content item
     * @param helper viewHolder
     * @param item   item
     */
    private void initGroupContentItem(BaseViewHolder helper, MultiItemEntity item) {
        ExpandableItem expandableItem = (ExpandableItem) item;
        TextView groupContentHead = helper.getView(R.id.group_item_head);
        TextView groupContentTail = helper.getView(R.id.group_item_tail);
        View groupContentBottomLine = helper.getView(R.id.group_item_line);

        if (expandableItem.isSelect()) {
            groupContentHead.setCompoundDrawablesWithIntrinsicBounds(mContext.getDrawable(R.mipmap.download_checking), null, null, null);
        } else {
            groupContentHead.setCompoundDrawablesWithIntrinsicBounds(mContext.getDrawable(R.mipmap.download_tocheck), null, null, null);
        }

        groupContentHead.setText(expandableItem.getTitle());
        int type = expandableItem.getResource_type();
        if (type == 2) { // 音视频显示时间
            groupContentTail.setVisibility(View.VISIBLE);
            groupContentTail.setText(DateFormat.longToString(expandableItem.getAudio_length() * 1000));
        } else if (type == 3) {
            groupContentTail.setVisibility(View.VISIBLE);
            groupContentTail.setText(DateFormat.longToString(expandableItem.getVideo_length() * 1000));
        } else {
            groupContentTail.setVisibility(View.GONE);
        }
        if (expandableItem.isLastItem()) {
            groupContentBottomLine.setVisibility(View.GONE);
        } else {
            groupContentBottomLine.setVisibility(View.VISIBLE);
        }

        // item 点击选中事件
        helper.addOnClickListener(R.id.group_item_wrap);
    }

    /**
     * 初始化收起点击事件
     * @param helper viewHolder
     * @param item   item
     */
    private void initGroupBottomItem(BaseViewHolder helper, MultiItemEntity item) {
        ExpandableItem expandableItem = (ExpandableItem) item;
        View bottomLine = helper.getView(R.id.group_bottom_top_line);
        if (expandableItem.isLastItem()) {
            bottomLine.setVisibility(View.GONE);
        } else {
            bottomLine.setVisibility(View.VISIBLE);
        }
        // item 点击收起事件
        helper.addOnClickListener(R.id.group_bottom_wrap);
    }

    /**
     * 初始化加载 item
     * @param helper viewHolder
     * @param item   item
     */
    private void initLoadStateItem(BaseViewHolder helper, MultiItemEntity item) {
        ExpandableItem expandableItem = (ExpandableItem) item;
        LinearLayout btnLoadAll = helper.getView(R.id.btn_expand_load_all);
        CommonRefreshHeader loadState = helper.getView(R.id.load_state);
        RelativeLayout expandableLoad = helper.getView(R.id.expandable_load);
        expandableLoad.setClickable(false);

        btnLoadAll.setVisibility(View.GONE);
        int state = expandableItem.getLoadType();
        Log.d(TAG, "setLoadItem: "+state);
        if(state == 3){
            expandableLoad.setVisibility(View.VISIBLE);
            loadState.getRefreshContent().setText("加载失败，点击刷新重试。");
            loadState.setBackgroundColor(mContext.getResources().getColor(R.color.download_item_bg));
            loadState.getRefreshImg().setVisibility(View.GONE);
            helper.addOnClickListener(R.id.expandable_load);
        }else if(state == 2){
            expandableLoad.setVisibility(View.GONE);
        }else if(state == 1){
            expandableLoad.setVisibility(View.VISIBLE);
            loadState.getRefreshContent().setText("加载中...");
            loadState.setBackgroundColor(mContext.getResources().getColor(R.color.download_item_bg));
            loadState.getRefreshImg().setVisibility(View.VISIBLE);
        }else{
            btnLoadAll.setVisibility(View.VISIBLE);
            expandableLoad.setVisibility(View.GONE);
            btnLoadAll.setBackgroundColor(mContext.getResources().getColor(R.color.download_item_bg));
            helper.addOnClickListener(R.id.btn_expand_load_all);
        }
    }
}
