package xiaoe.com.shop.adapter.decorate.knowledge_commodity;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;

public class KnowledgeGroupViewHolder extends BaseViewHolder {

    @BindView(R.id.knowledge_group_title)
    public
    TextView groupTitle;
    @BindView(R.id.knowledge_group_more)
    public
    TextView groupMore;
    @BindView(R.id.knowledge_group_recycler)
    public
    RecyclerView groupRecyclerView;

    public KnowledgeGroupViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
