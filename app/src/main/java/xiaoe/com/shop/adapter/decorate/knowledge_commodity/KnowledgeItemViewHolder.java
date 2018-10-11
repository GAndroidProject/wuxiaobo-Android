package xiaoe.com.shop.adapter.decorate.knowledge_commodity;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;

public class KnowledgeItemViewHolder extends BaseViewHolder {

    @BindView(R.id.knowledge_group_item_wrap)
    FrameLayout itemWrap;
    @BindView(R.id.knowledge_group_item_icon)
    SimpleDraweeView itemIcon;
    @BindView(R.id.knowledge_group_item_title)
    TextView itemTitle;
    @BindView(R.id.knowledge_group_item_price)
    TextView itemPrice;
    @BindView(R.id.knowledge_group_item_desc)
    TextView itemDesc;

    KnowledgeItemViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
