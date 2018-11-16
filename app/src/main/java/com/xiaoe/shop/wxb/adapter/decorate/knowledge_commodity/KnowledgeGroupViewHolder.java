package com.xiaoe.shop.wxb.adapter.decorate.knowledge_commodity;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.base.BaseViewHolder;

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
