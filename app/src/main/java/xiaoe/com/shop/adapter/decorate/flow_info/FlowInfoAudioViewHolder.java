package xiaoe.com.shop.adapter.decorate.flow_info;

import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;

/**
 * 音频 ViewHolder
 */
public class FlowInfoAudioViewHolder extends BaseViewHolder {

    @BindView(R.id.flow_info_audio_bg)
    public SimpleDraweeView flowInfoBg;
    @BindView(R.id.flow_info_audio_avatar)
    public SimpleDraweeView flowInfoAvatar;
    @BindView(R.id.flow_info_audio_joined_desc)
    public TextView flowInfoJoinedDesc;
    @BindView(R.id.flow_info_audio_title)
    public TextView flowInfoTitle;
    @BindView(R.id.flow_info_audio_desc)
    public TextView flowInfoDesc;
    @BindView(R.id.flow_info_audio_price)
    public TextView flowInfoPrice;

    public FlowInfoAudioViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}