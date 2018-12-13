package com.xiaoe.shop.wxb.adapter.tree;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.xiaoe.common.entitys.AudioPlayEntity;
import com.xiaoe.common.entitys.ExpandableItem;
import com.xiaoe.common.entitys.ExpandableLevel;
import com.xiaoe.common.utils.DateFormat;
import com.xiaoe.common.widget.CommonRefreshHeader;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.business.audio.presenter.AudioMediaPlayer;
import com.xiaoe.shop.wxb.business.audio.presenter.AudioPlayUtil;

import java.util.List;

public class ExpandableItemAdapter extends BaseMultiItemQuickAdapter<MultiItemEntity, BaseViewHolder> {
    public static final int TYPE_LEVEL_0 = 0;
    public static final int TYPE_PERSON = 1;
    public static final int TYPE_EMPTY = 2;
    public static final int TYPE_LOAD = 3;
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public ExpandableItemAdapter(List<MultiItemEntity> data) {
        super(data);
        addItemType(TYPE_LEVEL_0, R.layout.layout_column_expandable_item_0);
        addItemType(TYPE_PERSON, R.layout.layout_column_expandable_item_1);
        addItemType(TYPE_LOAD, R.layout.layout_expandable_load);
        addItemType(TYPE_EMPTY, R.layout.layout_pack_up);
    }
    @Override
    protected void convert(BaseViewHolder helper, MultiItemEntity item) {
        switch (helper.getItemViewType()){
            case TYPE_LEVEL_0:
                setExpandableItem(helper, item);
                break;
            case TYPE_PERSON:
                setExpandableChildItem(helper, item);
                break;
            case TYPE_EMPTY:
                setEmptyItem(helper, item);
                break;
            case TYPE_LOAD:
                setLoadItem(helper, item);
                break;
            default:
                break;
        }
    }

    private void setExpandableItem(BaseViewHolder helper, MultiItemEntity entity) {
        ExpandableLevel level = (ExpandableLevel) entity;
        if(level.isExpanded()){
            helper.setBackgroundRes(R.id.expandable_container, R.drawable.border_expandable_up);
            helper.getView(R.id.play_all_btn).setVisibility(View.VISIBLE);
            helper.getView(R.id.expand_down_btn).setVisibility(View.GONE);
        }else{
            helper.setBackgroundRes(R.id.expandable_container, R.drawable.border);
            helper.getView(R.id.play_all_btn).setVisibility(View.GONE);
            helper.getView(R.id.expand_down_btn).setVisibility(View.VISIBLE);
        }
        TextView title = helper.getView(R.id.text);
        title.setText(level.getTitle());
        //展开事件
        helper.addOnClickListener(R.id.expand_down_btn);
        //播放全部事件
        helper.addOnClickListener(R.id.play_all_btn);

        TextView playAllText = helper.getView(R.id.play_all_text);
        ImageView playAllIcon = helper.getView(R.id.play_all_icon);

        String bigColumnId = level.getBigColumnId();
        if(!TextUtils.isEmpty(bigColumnId) && AudioMediaPlayer.isPlaying()
                && bigColumnId.equals(AudioMediaPlayer.getAudio().getBigColumnId())
                && level.getResource_id().equals(AudioMediaPlayer.getAudio().getColumnId())){
            playAllText.setText(mContext.getString(R.string.stop_all));
            playAllIcon.setImageResource(R.mipmap.audiolist_playing);
        }else {
            playAllText.setText(mContext.getString(R.string.play_all));
            playAllIcon.setImageResource(R.mipmap.audiolist_playall);
        }
    }

    private void setExpandableChildItem(BaseViewHolder helper, MultiItemEntity entity) {
        ExpandableItem item = (ExpandableItem) entity;
        TextView title = helper.getView(R.id.text);
        title.setText(item.getTitle());
        int type = item.getResource_type();
        TextView playLength = helper.getView(R.id.item_play_length);
        ImageView playIcon = helper.getView(R.id.item_play_icon);
        TextView resourceTry = helper.getView(R.id.resource_try);

        if(item.isLastItem()){
            helper.setVisible(R.id.gap_line, false);
        }else{
            helper.setVisible(R.id.gap_line, true);
        }

        if(type == 2){
            helper.addOnClickListener(R.id.btn_item_play);
        }else{
            helper.getView(R.id.btn_item_play).setClickable(false);
        }

        helper.addOnClickListener(R.id.expandable_container_child);

        if(item.getIsTry() == 1 && item.getIsHasBuy() == 0){
            resourceTry.setVisibility(View.VISIBLE);
        }else{
            resourceTry.setVisibility(View.GONE);
        }

        if(type == 1){
            playLength.setVisibility(View.GONE);
            playIcon.setImageResource(R.mipmap.image_text_pic);
            resourceTry.setText(mContext.getString(R.string.resource_try_see));
        } else if(type == 2){
            playLength.setVisibility(View.VISIBLE);
            playLength.setText(DateFormat.longToString(item.getAudio_length() * 1000));
            playIcon.setImageResource(R.mipmap.audiolist_playall);
            resourceTry.setText(mContext.getString(R.string.resource_try_audition));
        }else if (type == 3){
            playLength.setVisibility(View.VISIBLE);
            playLength.setText(DateFormat.longToString(item.getVideo_length() * 1000));
            playIcon.setImageResource(R.mipmap.audiolist_vedio);
            resourceTry.setText(mContext.getString(R.string.resource_try_see));
        }else{
            playLength.setVisibility(View.GONE);
        }
        setPlayState(helper, item.getResource_id(), item.getColumnId(), item.getBigColumnId());
    }

    //设置播放态
    private void setPlayState(BaseViewHolder helper, String resourceId, String columnId, String bigColumnId) {
        AudioPlayEntity playEntity = AudioMediaPlayer.getAudio();
        boolean resourceEquals = false;
        if(playEntity != null){
            resourceEquals = AudioPlayUtil.resourceEquals(playEntity.getResourceId(), playEntity.getColumnId(), playEntity.getBigColumnId(),
                    resourceId, columnId, bigColumnId);
        }

        TextView text = helper.getView(R.id.text);
        TextView playLength = helper.getView(R.id.item_play_length);
        ImageView playIcon = helper.getView(R.id.item_play_icon);

        if(resourceEquals){
            int hightColor = mContext.getResources().getColor(R.color.high_title_color);
            text.setTextColor(hightColor);
            playLength.setTextColor(hightColor);
            if(AudioMediaPlayer.isPlaying() || playEntity.isPlaying()){
                playEntity.setPlaying(true);
                playIcon.setImageResource(R.mipmap.audiolist_playing);
            }else{
                playEntity.setPlaying(false);
                playIcon.setImageResource(R.mipmap.class_play);
            }
        }else{
            text.setTextColor(mContext.getResources().getColor(R.color.main_title_color));
            playLength.setTextColor(mContext.getResources().getColor(R.color.secondary_title_color));
        }
    }

    private void setEmptyItem(BaseViewHolder helper, MultiItemEntity item) {
        helper.addOnClickListener(R.id.expand_top_btn);
    }

    private void setLoadItem(BaseViewHolder helper, MultiItemEntity item){
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
            loadState.getRefreshImg().setVisibility(View.GONE);
            helper.addOnClickListener(R.id.expandable_load);
        }else if(state == 2){
            expandableLoad.setVisibility(View.GONE);
        }else if(state == 1){
            expandableLoad.setVisibility(View.VISIBLE);
            loadState.getRefreshContent().setText("加载中...");
            loadState.getRefreshImg().setVisibility(View.VISIBLE);
        }else{
            btnLoadAll.setVisibility(View.VISIBLE);
            expandableLoad.setVisibility(View.GONE);
            helper.addOnClickListener(R.id.btn_expand_load_all);
        }
    }

}
