package com.xiaoe.shop.wxb.adapter.decorate.recent_update;


import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.xiaoe.common.entitys.AudioPlayEntity;
import com.xiaoe.common.entitys.DecorateEntityType;
import com.xiaoe.common.entitys.RecentUpdateListItem;
import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.business.audio.presenter.AudioMediaPlayer;
import com.xiaoe.shop.wxb.business.audio.presenter.AudioPlayUtil;
import com.xiaoe.shop.wxb.business.audio.presenter.AudioPresenter;
import com.xiaoe.shop.wxb.common.JumpDetail;
import com.xiaoe.shop.wxb.common.datareport.EventReportManager;
import com.xiaoe.shop.wxb.common.datareport.MobclickEvent;
import com.xiaoe.shop.wxb.events.OnClickEvent;
import com.xiaoe.shop.wxb.utils.LoginDialogUtils;
import com.xiaoe.shop.wxb.utils.SetImageUriUtil;
import com.xiaoe.shop.wxb.widget.TouristDialog;

/**
 * 最近更新列表适配器
 */
public class RecentUpdateListAdapter extends BaseAdapter {
    private static final String TAG = "RecentUpdateListAdapter";

    private List<RecentUpdateListItem> mItemList;
    private Activity mActivity;
    private Context mContext;
    private LayoutInflater mInflater;
    private boolean hasBuy = false;
    public boolean isPlaying = false;
    // private boolean isClickPauseAllButton = false;
    private String columnId;
    private String columnType;

    public RecentUpdateListAdapter(Context mContext, List<RecentUpdateListItem> itemList, boolean hasBuy) {
        this.mItemList = itemList;
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(mContext);
        this.hasBuy = hasBuy;
    }

    public void setColumnMsg(String columnId, String columnType) {
        this.columnId = columnId;
        this.columnType = columnType;
    }

    // 设置集合
    public void setItemList(List<RecentUpdateListItem> itemList) {
        if (this.mItemList != null) { // 有值，先清空然后再赋值
            this.mItemList.clear();
            this.mItemList = null;
        }
        this.mItemList = itemList;
        notifyDataSetChanged();
    }

    public List<RecentUpdateListItem> getItemList() {
        return this.mItemList;
    }

//    public List<String> getResourceIdList() {
//        List<String> resourceIdList = new ArrayList<>();
//        if (mItemList != null) {
//            for (RecentUpdateListItem item : mItemList) {
//                resourceIdList.add(item.getListResourceId());
//            }
//        }
//        return resourceIdList;
//    }
//
    public String getColumnId() {
        return columnId;
    }

    @Override
    public int getCount() {
        return mItemList == null ? 0 : mItemList.size();
    }

    @Override
    public RecentUpdateListItem getItem(int position) {
        return mItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final RecentUpdateListViewHolder viewHolder;
        final RecentUpdateListItem recentUpdateListItem = mItemList.get(position);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.recent_update_list_item, parent ,false);
            viewHolder = new RecentUpdateListViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (RecentUpdateListViewHolder) convertView.getTag();
        }
        viewHolder.itemTitle.setText(recentUpdateListItem.getListTitle());
        viewHolder.itemWrap.setOnClickListener(new OnClickEvent(OnClickEvent.DEFAULT_SECOND) {
            @Override
            public void singleClick(View v) {

                HashMap<String, String> map = new HashMap<>(1);
                map.put(MobclickEvent.INDEX, position + "");
                EventReportManager.onEvent(mContext, MobclickEvent.COURSE_WXB_EVERYDAY_ITEM_CLICK, map);

                if (recentUpdateListItem.isListIsFormUser()) {
                    if(!hasBuy){
                        switch (columnType) {
                            case DecorateEntityType.TOPIC:
                                JumpDetail.jumpColumn(mContext, columnId, "", 8);
                                return;
                            case DecorateEntityType.COLUMN:
                                JumpDetail.jumpColumn(mContext, columnId, "", 6);
                                return;
                            case DecorateEntityType.MEMBER:
                                JumpDetail.jumpColumn(mContext, columnId, "", 5);
                                return;
                            default:
                                break;
                        }
                    } else {
                        String itemResourceType = convertInt2Str(recentUpdateListItem.getResourceType());
                        if (itemResourceType != null) {
                            switch (itemResourceType) {
                                case DecorateEntityType.AUDIO:
                                    if (!DecorateEntityType.RECENT_UPDATE_STR.equals(AudioPlayUtil.getInstance().getFromTag())) {
                                        AudioPlayUtil.getInstance().setFromTag(DecorateEntityType.RECENT_UPDATE_STR);
                                        AudioPlayUtil.getInstance().setAudioList(getAudioPlayList(mItemList));
                                        AudioPlayUtil.getInstance().setSingleAudio(false);
                                    }
                                    playPosition(recentUpdateListItem.getListResourceId(), recentUpdateListItem.getColumnId(), recentUpdateListItem.getBigColumnId(), true);
                                    JumpDetail.jumpAudio(mContext, recentUpdateListItem.getListResourceId(), 1);
                                    break;
                                case DecorateEntityType.IMAGE_TEXT:
                                    JumpDetail.jumpImageText(mContext, recentUpdateListItem.getListResourceId(), "", recentUpdateListItem.getColumnId());
                                    break;
                                case DecorateEntityType.VIDEO:
                                    JumpDetail.jumpVideo(mContext, recentUpdateListItem.getListResourceId(), "", false, recentUpdateListItem.getColumnId());
                                    break;
                                case DecorateEntityType.COLUMN:
                                    JumpDetail.jumpColumn(mContext, columnId, "", 6);
                                    break;
                                case DecorateEntityType.TOPIC:
                                    JumpDetail.jumpColumn(mContext, columnId, "", 8);
                                    break;
                                case DecorateEntityType.MEMBER:
                                    JumpDetail.jumpColumn(mContext, columnId, "", 5);
                                    break;
                                default:
                                    break;
                            }
                        } else {
                            Log.d(TAG, "singleClick: type 有误");
                        }
                    }
                } else {
                    LoginDialogUtils.showTouristDialog(mContext);
                }
            }
        });
        if(recentUpdateListItem.getResourceType() == 2){ // 音频
            boolean resourceEqual = playResourceEquals(recentUpdateListItem.getListResourceId(), recentUpdateListItem.getColumnId(), recentUpdateListItem.getBigColumnId());
            Log.d(TAG, "getView: resourceEqual = "+resourceEqual+" ; "+AudioMediaPlayer.isPlaying());
            if(resourceEqual){
                viewHolder.itemIcon.setVisibility(View.VISIBLE);
                viewHolder.itemTitle.setTextColor(mContext.getResources().getColor(R.color.high_title_color));
            }else{
                viewHolder.itemIcon.setVisibility(View.VISIBLE);
                viewHolder.itemTitle.setTextColor(mContext.getResources().getColor(R.color.recent_list_color));
            }
            if(resourceEqual && (AudioMediaPlayer.isPlaying() || AudioMediaPlayer.getAudio().isPlaying())){
                SetImageUriUtil.setImgURI(viewHolder.itemIcon, "res:///" + R.mipmap.audiolist_playing, Dp2Px2SpUtil.dp2px(mContext, 16), Dp2Px2SpUtil.dp2px(mContext, 16));
            }else{
                SetImageUriUtil.setImgURI(viewHolder.itemIcon, "res:///" + R.mipmap.audiolist_playall, Dp2Px2SpUtil.dp2px(mContext, 16), Dp2Px2SpUtil.dp2px(mContext, 16));
                viewHolder.itemTitle.setTextColor(mContext.getResources().getColor(R.color.recent_list_color));
//                if (isClickPauseAllButton)
//                    viewHolder.itemTitle.setTextColor(mContext.getResources().getColor(R.color.recent_list_color));
            }
        } else if (recentUpdateListItem.getResourceType() == 1) { // 图文
            viewHolder.itemIcon.setVisibility(View.VISIBLE);
            viewHolder.itemTitle.setTextColor(mContext.getResources().getColor(R.color.recent_list_color));
            SetImageUriUtil.setImgURI(viewHolder.itemIcon, "res:///" + R.mipmap.image_text_pic, Dp2Px2SpUtil.dp2px(mContext, 16), Dp2Px2SpUtil.dp2px(mContext, 16));
        } else if (recentUpdateListItem.getResourceType() == 3) { // 视频
            viewHolder.itemIcon.setVisibility(View.VISIBLE);
            viewHolder.itemTitle.setTextColor(mContext.getResources().getColor(R.color.recent_list_color));
            SetImageUriUtil.setImgURI(viewHolder.itemIcon, "res:///" + R.mipmap.audiolist_vedio, Dp2Px2SpUtil.dp2px(mContext, 16), Dp2Px2SpUtil.dp2px(mContext, 16));
        } else {
            // 除了单品，就隐藏这个播放按钮
            viewHolder.itemIcon.setVisibility(View.GONE);
            viewHolder.itemTitle.setTextColor(mContext.getResources().getColor(R.color.recent_list_color));
        }

        viewHolder.itemIcon.setOnClickListener(new OnClickEvent(OnClickEvent.DEFAULT_SECOND) {
            @Override
            public void singleClick(View v) {
                if (recentUpdateListItem.isListIsFormUser()) {
                    if(!hasBuy){
                        switch (columnType) {
                            case DecorateEntityType.TOPIC:
                                JumpDetail.jumpColumn(mContext, columnId, "", 8);
                                return;
                            case DecorateEntityType.COLUMN:
                                JumpDetail.jumpColumn(mContext, columnId, "", 6);
                                return;
                            case DecorateEntityType.MEMBER:
                                JumpDetail.jumpColumn(mContext, columnId, "", 5);
                                return;
                            default:
                                break;
                        }
                    }
                    if(!DecorateEntityType.RECENT_UPDATE_STR.equals(AudioPlayUtil.getInstance().getFromTag())){
                        AudioPlayUtil.getInstance().setFromTag(DecorateEntityType.RECENT_UPDATE_STR);
                        AudioPlayUtil.getInstance().setAudioList(getAudioPlayList(mItemList));
                        AudioPlayUtil.getInstance().setSingleAudio(false);
                    }
                    playPosition(recentUpdateListItem.getListResourceId(), recentUpdateListItem.getColumnId(), recentUpdateListItem.getBigColumnId(), false);
                } else {
                    LoginDialogUtils.showTouristDialog(mContext);
                }
            }
        });
//        viewHolder.itemWrap.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                String resourceId = mItemList.get(position).getListResourceId();
////                Intent audioIntent = new Intent(mContext, AudioActivity.class);
////                audioIntent.putExtra("resource_id", resourceId);
////                mContext.startActivity(audioIntent);
//            }
//        });
        return convertView;
    }

    /**
     * 获取播放列表
     * @param list
     * @return
     */
    public List<AudioPlayEntity> getAudioPlayList(List<RecentUpdateListItem> list){
        List<AudioPlayEntity> playList = new ArrayList<AudioPlayEntity>();
        int index = 0;
        for (RecentUpdateListItem entity : list) {
            if(entity.getResourceType() != 2){
                continue;
            }
            AudioPlayEntity playEntity = new AudioPlayEntity();
            playEntity.setAppId(entity.getAppId());
            playEntity.setResourceId(entity.getListResourceId());
            playEntity.setIndex(index);
            playEntity.setCurrentPlayState(0);
            playEntity.setTitle(entity.getListTitle());
            playEntity.setState(0);
            playEntity.setPlay(false);
            playEntity.setPlayUrl(entity.getAudioUrl());
            playEntity.setCode(-1);
            playEntity.setHasBuy(hasBuy ? 1 : 0);
            playEntity.setColumnId(entity.getColumnId());
            playEntity.setBigColumnId(entity.getBigColumnId());
            playEntity.setTotalDuration(entity.getAudioLength());
            playEntity.setProductsTitle(entity.getColumnTitle());
            index++;
            playList.add(playEntity);
        }
        return playList;
    }

    /**
     * 播放音频
     * @param resourceId
     * @param columnId
     * @param bigColumnId
     */
    public void playPosition(String resourceId, String columnId, String bigColumnId, boolean jump){
        // isClickPauseAllButton = false;
        AudioPlayEntity playAudio = AudioMediaPlayer.getAudio();

        boolean resourceEquals = false;
        if(playAudio != null){
            resourceEquals = AudioPlayUtil.resourceEquals(playAudio.getResourceId(), playAudio.getColumnId(), playAudio.getBigColumnId(),
                    resourceId, columnId, bigColumnId);
        }
        //正在播放的资源和点击的资源相同，则播放暂停操作
        if(playAudio != null && resourceEquals){
            playAudio.setPlaying(!playAudio.isPlaying());
            if(AudioMediaPlayer.isStop()){
                AudioMediaPlayer.start();
            }else{
                //如果是跳转详情页，则保持播放状态,如果不是，则进行播放暂停动作
                if(!jump){
                    AudioMediaPlayer.play();
                }
            }
            return;
        }
        AudioMediaPlayer.stop();
        for (AudioPlayEntity playEntity : AudioPlayUtil.getInstance().getAudioList()) {
            if(playEntity != null && !TextUtils.isEmpty(playEntity.getResourceId())
                    && playEntity.getResourceId().equals(resourceId)){
                playEntity.setPlaying(true);
                playEntity.setPlay(true);
//                playEntity.setColumnId(columnId);
                AudioMediaPlayer.setAudio(playEntity, true);
                new AudioPresenter(null).requestDetail(playEntity.getResourceId());
                break;
            }
        }
        notifyDataSetChanged();
    }

    public void clickPlayAll() {
        // isClickPauseAllButton = false;
        isPlaying = true;
        AudioPlayEntity playAudio = AudioMediaPlayer.getAudio();
        List<RecentUpdateListItem> audioList = new ArrayList<>();
        for (RecentUpdateListItem listItem : mItemList) {
            if (listItem.getResourceType() == 2) { // 视音频
                audioList.add(listItem);
            }
        }
        if(!DecorateEntityType.RECENT_UPDATE_STR.equals(AudioPlayUtil.getInstance().getFromTag())){
            AudioPlayUtil.getInstance().setFromTag(DecorateEntityType.RECENT_UPDATE_STR);
            AudioPlayUtil.getInstance().setAudioList(getAudioPlayList(audioList));
            AudioPlayUtil.getInstance().setSingleAudio(false);
        }

        if (playAudio == null) { // 没有音频在播放
            if(AudioPlayUtil.getInstance().getAudioList().size() > 0){
                playFirstAudio(true);
            }
        } else { // 有音频在播放
            if (!AudioPlayUtil.resourceEquals(playAudio.getResourceId(), playAudio.getColumnId(), playAudio.getBigColumnId(),
                    audioList.get(0).getListResourceId(), columnId, "")) { // 不同一个专栏
                AudioPlayUtil.getInstance().setAudioList(getAudioPlayList(audioList));
                playFirstAudio(true);
            } else {
                playFirstAudio(false);
            }
        }

        notifyDataSetChanged();
    }

    // 播放第一条音频
    private void playFirstAudio(boolean needStop) {
        if (needStop) {
            AudioMediaPlayer.stop();
        }
        AudioPlayEntity playEntity = AudioPlayUtil.getInstance().getAudioList().get(0);
        playEntity.setPlay(true);
        AudioMediaPlayer.setAudio(playEntity, true);
        AudioMediaPlayer.getAudio().setPlaying(true);
        new AudioPresenter(null).requestDetail(playEntity.getResourceId());
    }

    public void stopPlayAll() {
        // isClickPauseAllButton = true;
        isPlaying = false;
        AudioMediaPlayer.stop();
        AudioMediaPlayer.getAudio().setPlaying(false);
        notifyDataSetChanged();
    }

    //设置播放态
    private boolean playResourceEquals(String resourceId, String columnId, String bigColumnId) {

        AudioPlayEntity playEntity = AudioMediaPlayer.getAudio();
        boolean resourceEquals = false;
        if(playEntity != null){
            resourceEquals = AudioPlayUtil.resourceEquals(playEntity.getResourceId(), playEntity.getColumnId(), playEntity.getBigColumnId(),
                    resourceId, columnId, bigColumnId);
        }
        return resourceEquals;
    }

    /**
     * 资源类型转换 int - str
     *
     * @param resourceType 资源类型
     * @return 资源类型的字符串形式
     */
    private String convertInt2Str(int resourceType) {
        switch (resourceType) {
            case 1: // 图文
                return DecorateEntityType.IMAGE_TEXT;
            case 2: // 音频
                return DecorateEntityType.AUDIO;
            case 3: // 视频
                return DecorateEntityType.VIDEO;
            case 5: // 会员
                return DecorateEntityType.MEMBER;
            case 6: // 专栏
                return DecorateEntityType.COLUMN;
            case 8: // 大专栏
                return DecorateEntityType.TOPIC;
            default:
                return "";
        }
    }
}
