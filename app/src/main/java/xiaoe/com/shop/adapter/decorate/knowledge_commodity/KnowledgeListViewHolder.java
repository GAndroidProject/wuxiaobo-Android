package xiaoe.com.shop.adapter.decorate.knowledge_commodity;

import android.view.View;
import android.widget.ListView;

import butterknife.BindView;
import butterknife.ButterKnife;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;

// RecyclerView 的 ViewHolder
public class KnowledgeListViewHolder extends BaseViewHolder {

    @BindView(R.id.knowledge_list_list_view)
    public ListView knowledgeListView;

    public KnowledgeListViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
