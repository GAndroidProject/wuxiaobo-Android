package com.xiaoe.shop.wxb.adapter.tree;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoe.common.entitys.AudioPlayEntity;
import com.xiaoe.common.entitys.ColumnSecondDirectoryEntity;
import com.xiaoe.common.utils.DateFormat;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseViewHolder;
import com.xiaoe.shop.wxb.business.audio.presenter.AudioMediaPlayer;
import com.xiaoe.shop.wxb.business.audio.presenter.AudioPlayUtil;
import com.xiaoe.shop.wxb.interfaces.OnClickListPlayListener;

/**
 * @Author Zheng Haibo
 * @PersonalWebsite http://www.mobctrl.net
 * @Description
 */
public class ChildViewHolder extends BaseViewHolder {

	public TextView text;
	public RelativeLayout relativeLayout;
	private final TextView playLength;
	private final ImageView playIcon;
	private final LinearLayout btnItemPlay;
	private Context mContext = null;
	private final TextView resourceTry;

	public ChildViewHolder(Context context, View itemView) {
		super(itemView);
		mContext = context;
		text = (TextView) itemView.findViewById(R.id.text);
		relativeLayout = (RelativeLayout) itemView.findViewById(R.id.item_container);
		playLength = (TextView) itemView.findViewById(R.id.item_play_length);
		playIcon = (ImageView) itemView.findViewById(R.id.item_play_icon);
		btnItemPlay = (LinearLayout) itemView.findViewById(R.id.btn_item_play);
		resourceTry = (TextView) itemView.findViewById(R.id.resource_try);
	}

	public void bindView(final ColumnSecondDirectoryEntity itemData, final int parentPosition, final int position, final OnClickListPlayListener mListPlayListener) {
		text.setText(itemData.getTitle());
		int type = itemData.getResource_type();
		if(type == 2){
			playLength.setVisibility(View.VISIBLE);
			playLength.setText(DateFormat.longToString(itemData.getAudio_length() * 1000));
		}else if (type == 3){
			playLength.setVisibility(View.VISIBLE);
			playLength.setText(DateFormat.longToString(itemData.getVideo_length() * 1000));
		}else{
			playLength.setVisibility(View.GONE);
		}
		if(itemData.getIsTry() == 1){
			resourceTry.setVisibility(View.VISIBLE);
		}else{
			resourceTry.setVisibility(View.GONE);
		}
		setMediaType(type);
		relativeLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				if(mListPlayListener != null){
					mListPlayListener.onJumpDetail(itemData, parentPosition, position);
				}
			}
		});
		btnItemPlay.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(mListPlayListener != null){
					mListPlayListener.onPlayPosition(v,parentPosition, position, false);
				}
			}
		});
		setPlayState(itemData.getResource_id(), itemData.getColumnId(), itemData.getBigColumnId());
		btnItemPlay.setClickable(3 != type);//修复点击视频按钮不能跳转
	}
	//设置播放态
	private void setPlayState(String resourceId, String columnId, String bigColumnId) {

		AudioPlayEntity playEntity = AudioMediaPlayer.getAudio();
        boolean resourceEquals = false;
		if(playEntity != null){
            resourceEquals = AudioPlayUtil.resourceEquals(playEntity.getResourceId(), playEntity.getColumnId(), playEntity.getBigColumnId(),
                    resourceId, columnId, bigColumnId);
        }

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


	public void setMediaType(int type){
		if(type == 1){
			playIcon.setImageResource(R.mipmap.image_text_pic);
			resourceTry.setText(mContext.getString(R.string.resource_try_see));
		}else if(type == 2){
			playIcon.setImageResource(R.mipmap.audiolist_playall);
			resourceTry.setText(mContext.getString(R.string.resource_try_audition));
		}else if(type == 3){
			playIcon.setImageResource(R.mipmap.audiolist_vedio);
			resourceTry.setText(mContext.getString(R.string.resource_try_see));
		}
	}



}
