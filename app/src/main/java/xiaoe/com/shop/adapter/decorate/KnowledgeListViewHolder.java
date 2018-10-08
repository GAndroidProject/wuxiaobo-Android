package xiaoe.com.shop.adapter.decorate;

import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;

public class KnowledgeListViewHolder extends BaseViewHolder {

    @BindView(R.id.knowledge_list_icon)
    SimpleDraweeView knowledgeIcon;
    @BindView(R.id.knowledge_list_title)
    TextView knowledgeTitle;
    @BindView(R.id.knowledge_list_price)
    TextView knowledgePrice;
    @BindView(R.id.knowledge_list_desc)
    TextView knowledgeDesc;

    public KnowledgeListViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
