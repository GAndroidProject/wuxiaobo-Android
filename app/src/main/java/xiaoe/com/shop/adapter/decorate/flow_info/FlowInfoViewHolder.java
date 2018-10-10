package xiaoe.com.shop.adapter.decorate.flow_info;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;

public class FlowInfoViewHolder extends BaseViewHolder {

    @BindView(R.id.flow_info_head_title)
    public TextView flowInfoTitle;
    @BindView(R.id.flow_info_head_desc)
    public TextView flowInfoDesc;
    @BindView(R.id.flow_info_head_icon)
    public SimpleDraweeView flowInfoIcon;
    @BindView(R.id.flow_info_head_icon_desc)
    public TextView flowInfoIconDesc;
    @BindView(R.id.flow_info_recycler)
    public RecyclerView flowInfoRecycler;

    public FlowInfoViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
