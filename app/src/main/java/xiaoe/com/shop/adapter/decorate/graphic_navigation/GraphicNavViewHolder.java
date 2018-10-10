package xiaoe.com.shop.adapter.decorate.graphic_navigation;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;

public class GraphicNavViewHolder extends BaseViewHolder {

    @BindView(R.id.graphic_nav_recycler)
    public RecyclerView graphicNavRecycler;

    public GraphicNavViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
