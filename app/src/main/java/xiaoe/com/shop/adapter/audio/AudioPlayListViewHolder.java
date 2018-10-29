package xiaoe.com.shop.adapter.audio;

import android.content.Context;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import xiaoe.com.common.entitys.AudioPlayEntity;
import xiaoe.com.common.utils.DateFormat;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;
import xiaoe.com.shop.business.audio.presenter.AudioMediaPlayer;

public class AudioPlayListViewHolder extends BaseViewHolder {
    private static final String TAG = "AudioPlayListViewHolder";
    private Context mContext;
    private View rootView;
    private TextView title;
    private TextView duration;
    private final RelativeLayout btnItem;

    public AudioPlayListViewHolder(View itemView, Context context) {
        super(itemView);
        this.mContext = context;
        rootView = itemView;
        title = (TextView) rootView.findViewById(R.id.item_title);
        duration = (TextView) rootView.findViewById(R.id.item_duration);
        btnItem = (RelativeLayout) rootView.findViewById(R.id.item_play_list);
    }


    public void bindView(AudioPlayEntity playEntity, final int position){
        title.setText(playEntity.getTitle());
        duration.setText(DateFormat.longToString(playEntity.getTotalDuration() * 1000));
        final String resourceId = playEntity.getResourceId();
        setState(resourceId);
        btnItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击的item和现在播放的资源不相同才播放，如果相同则放弃点击事件
                if(!resourceId.equals(AudioMediaPlayer.getAudio().getResourceId())){
                    AudioMediaPlayer.playAppoint(position);
                }
            }
        });
    }
    private void setState(String resourceId){
        if(resourceId.equals(AudioMediaPlayer.getAudio().getResourceId())){
            int highColor = mContext.getResources().getColor(R.color.high_title_color);
            title.setTextColor(highColor);
            duration.setTextColor(highColor);
        }else{
            title.setTextColor(mContext.getResources().getColor(R.color.main_title_color));
            duration.setTextColor(mContext.getResources().getColor(R.color.secondary_title_color));
        }
    }
}
