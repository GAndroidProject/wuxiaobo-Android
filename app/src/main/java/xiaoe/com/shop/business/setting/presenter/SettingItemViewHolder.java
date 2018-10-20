package xiaoe.com.shop.business.setting.presenter;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;

public class SettingItemViewHolder extends BaseViewHolder {

    @BindView(R.id.setting_item_container)
    FrameLayout itemContainer;
    @BindView(R.id.item_title)
    TextView itemTitle;
    @BindView(R.id.item_icon)
    SimpleDraweeView itemIcon;
    @BindView(R.id.item_content)
    TextView itemContent;
    @BindView(R.id.item_go)
    ImageView itemGo;

    private OnItemClickListener itemClickListener;

    SettingItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
