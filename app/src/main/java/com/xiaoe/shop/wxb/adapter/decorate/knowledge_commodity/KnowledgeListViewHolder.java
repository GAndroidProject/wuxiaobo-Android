package com.xiaoe.shop.wxb.adapter.decorate.knowledge_commodity;

import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseViewHolder;

// RecyclerView 的 ViewHolder
public class KnowledgeListViewHolder extends BaseViewHolder {

    @BindView(R.id.knowledge_list_title)
    public TextView knowledgeListTitle;
    @BindView(R.id.knowledge_list_list_view)
    public ListView knowledgeListView;

    public KnowledgeListViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
