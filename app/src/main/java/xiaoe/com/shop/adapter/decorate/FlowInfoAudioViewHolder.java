package xiaoe.com.shop.adapter.decorate;

import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;

public class FlowInfoAudioViewHolder extends BaseViewHolder {

    @BindView(R.id.flow_info_audio_bg)
    SimpleDraweeView flowInfoBg;
    @BindView(R.id.flow_info_audio_avatar)
    SimpleDraweeView flowInfoAvatar;
    @BindView(R.id.flow_info_audio_joined_desc)
    TextView flowInfoJoinedDesc;
    @BindView(R.id.flow_info_audio_title)
    TextView flowInfoTitle;
    @BindView(R.id.flow_info_audio_desc)
    TextView flowInfoDesc;
    @BindView(R.id.flow_info_audio_price)
    TextView flowInfoPrice;

    FlowInfoAudioViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
