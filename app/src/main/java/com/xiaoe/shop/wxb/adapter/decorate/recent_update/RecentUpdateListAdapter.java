package com.xiaoe.shop.wxb.adapter.decorate.recent_update;


import android.app.Activity;
import android.content.Context;
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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.xiaoe.common.entitys.AudioPlayEntity;
import com.xiaoe.common.entitys.DecorateEntityType;
import com.xiaoe.common.entitys.RecentUpdateListItem;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.business.audio.presenter.AudioMediaPlayer;
import com.xiaoe.shop.wxb.business.audio.presenter.AudioPlayUtil;
import com.xiaoe.shop.wxb.business.audio.presenter.AudioPresenter;
import com.xiaoe.shop.wxb.common.JumpDetail;
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
    private boolean isClickPauseAllButton = false;

    public RecentUpdateListAdapter(Context mContext, List<RecentUpdateListItem> itemList, boolean hasBuy) {
        this.mItemList = itemList;
        this.mContext = mContext;
        this.mInflater = LayoutInflater.from(mContext);
        this.hasBuy = hasBuy;
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
        final RecentUpdateHolder viewHolder;
        final RecentUpdateListItem recentUpdateListItem = mItemList.get(position);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.recent_update_list_item, parent ,false);
            viewHolder = new RecentUpdateHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (RecentUpdateHolder) convertView.getTag();
        }
        viewHolder.itemTitle.setText(recentUpdateListItem.getListTitle());
        viewHolder.itemWrap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recentUpdateListItem.isListIsFormUser()) {
                    if(!hasBuy){
                        Toast.makeText(mContext, "未购买课程", Toast.LENGTH_SHORT).show();
                        return;
                    }else{
                        if(!DecorateEntityType.RECENT_UPDATE_STR.equals(AudioPlayUtil.getInstance().getFromTag())){
                            AudioPlayUtil.getInstance().setFromTag(DecorateEntityType.RECENT_UPDATE_STR);
                            AudioPlayUtil.getInstance().setAudioList(getAudioPlayList(mItemList));
                            AudioPlayUtil.getInstance().setSingleAudio(false);
                        }
                        playPosition(recentUpdateListItem.getListResourceId(), recentUpdateListItem.getColumnId(), recentUpdateListItem.getBigColumnId(), true);
                        JumpDetail.jumpAudio(mContext, recentUpdateListItem.getListResourceId(), 1);
                    }
                } else {
                    showTouristDialog();
                }
            }
        });
        if(recentUpdateListItem.getResourceType() == 2){
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
                viewHolder.itemIcon.setImageURI("res:///" + R.mipmap.audiolist_playing);
            }else{
                viewHolder.itemIcon.setImageURI("res:///" + R.mipmap.audiolist_playall);
                if (isClickPauseAllButton)
                    viewHolder.itemTitle.setTextColor(mContext.getResources().getColor(R.color.recent_list_color));
            }
        }else{
            // 没有设置播放状态的话，就隐藏这个播放按钮
            viewHolder.itemIcon.setVisibility(View.GONE);
            viewHolder.itemTitle.setTextColor(mContext.getResources().getColor(R.color.recent_list_color));
        }

        viewHolder.itemIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recentUpdateListItem.isListIsFormUser()) {
                    if(!hasBuy){
                        Toast.makeText(mContext, "未购买课程", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(!DecorateEntityType.RECENT_UPDATE_STR.equals(AudioPlayUtil.getInstance().getFromTag())){
                        AudioPlayUtil.getInstance().setFromTag(DecorateEntityType.RECENT_UPDATE_STR);
                        AudioPlayUtil.getInstance().setAudioList(getAudioPlayList(mItemList));
                        AudioPlayUtil.getInstance().setSingleAudio(false);
                    }
                    playPosition(recentUpdateListItem.getListResourceId(), recentUpdateListItem.getColumnId(), recentUpdateListItem.getBigColumnId(), false);
                } else {
                    showTouristDialog();
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

    class RecentUpdateHolder {

        @BindView(R.id.recent_update_item_wrap)
        RelativeLayout itemWrap;
        @BindView(R.id.recent_update_item_title)
        TextView itemTitle;
        @BindView(R.id.recent_update_item_icon)
        SimpleDraweeView itemIcon;

        RecentUpdateHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
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
        isClickPauseAllButton = false;
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
            if(playEntity.getResourceId().equals(resourceId)){
                playEntity.setPlaying(true);
                playEntity.setPlay(true);
                AudioMediaPlayer.setAudio(playEntity, true);
                new AudioPresenter(null).requestDetail(playEntity.getResourceId());
                break;
            }
        }
        notifyDataSetChanged();
    }

    public void clickPlayAll() {
        isClickPauseAllButton = false;
        isPlaying = true;
        if(!DecorateEntityType.RECENT_UPDATE_STR.equals(AudioPlayUtil.getInstance().getFromTag())){
            AudioPlayUtil.getInstance().setFromTag(DecorateEntityType.RECENT_UPDATE_STR);
            AudioPlayUtil.getInstance().setAudioList(getAudioPlayList(mItemList));
            AudioPlayUtil.getInstance().setSingleAudio(false);
        }
        if(AudioPlayUtil.getInstance().getAudioList().size() > 0){
            AudioMediaPlayer.stop();
            AudioPlayEntity playEntity = AudioPlayUtil.getInstance().getAudioList().get(0);
            playEntity.setPlay(true);
            AudioMediaPlayer.setAudio(playEntity, true);
            new AudioPresenter(null).requestDetail(playEntity.getResourceId());
        }
        notifyDataSetChanged();
    }

    public void stopPlayAll() {
        isClickPauseAllButton = true;
        isPlaying = false;
        AudioMediaPlayer.stop();
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

    private void showTouristDialog() {
        final TouristDialog touristDialog = new TouristDialog(mContext);
        touristDialog.setDialogCloseClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                touristDialog.dismissDialog();
            }
        });
        touristDialog.setDialogConfirmClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                touristDialog.dismissDialog();
                JumpDetail.jumpLogin(mContext, true);
            }
        });
        touristDialog.showDialog();
    }
}
