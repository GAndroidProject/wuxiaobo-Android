package xiaoe.com.shop.adapter.decorate.graphic_navigation;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;

class GraphicNavItemViewHolder extends BaseViewHolder {

    @BindView(R.id.graphic_item_wrap)
    LinearLayout itemWrap;
    @BindView(R.id.graphic_item_icon)
    SimpleDraweeView itemIcon;
    @BindView(R.id.graphic_item_content)
    TextView itemContent;

    GraphicNavItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
