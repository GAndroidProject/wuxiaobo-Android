package xiaoe.com.shop.adapter.decorate;

import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;

/**
 * 视频 ViewHolder
 */
class FlowInfoVideoViewHolder extends BaseViewHolder {

    @BindView(R.id.flow_info_video_bg)
    SimpleDraweeView flowInfoBg;
    @BindView(R.id.flow_info_video_title)
    TextView flowInfoTitle;
    @BindView(R.id.flow_info_video_desc)
    TextView flowInfoDesc;
    @BindView(R.id.flow_info_video_price)
    TextView flowInfoPrice;

    FlowInfoVideoViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
