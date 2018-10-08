package xiaoe.com.shop.adapter.decorate;

import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;

/**
 * 图文 ViewHolder
 */
class FlowInfoImgTextViewHolder extends BaseViewHolder {

    @BindView(R.id.flow_info_img_text_bg)
    SimpleDraweeView flowInfoBg;
    @BindView(R.id.flow_info_img_text_title)
    TextView flowInfoTitle;
    @BindView(R.id.flow_info_img_text_desc)
    TextView flowInfoDesc;
    @BindView(R.id.flow_info_img_text_price)
    TextView flowInfoPrice;

    FlowInfoImgTextViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

}
