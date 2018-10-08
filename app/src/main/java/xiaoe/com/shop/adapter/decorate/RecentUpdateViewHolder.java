package xiaoe.com.shop.adapter.decorate;

import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;

/**
 * 最近更新 ViewHolder
 */
class RecentUpdateViewHolder extends BaseViewHolder {

    @BindView(R.id.recent_update_sub_list)
    ListView recentUpdateSubList;
    @BindView(R.id.recent_update_avatar)
    SimpleDraweeView recentUpdateAvatar;
    @BindView(R.id.recent_update_sub_title)
    TextView recentUpdateSubTitle;
    @BindView(R.id.recent_update_sub_desc)
    TextView recentUpdateSubDesc;
    @BindView(R.id.recent_update_sub_btn)
    Button recentUpdateSubBtn;

    RecentUpdateViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
