package xiaoe.com.shop.business.main.presenter;

import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import butterknife.BindView;
import butterknife.ButterKnife;
import xiaoe.com.shop.R;

public class ScholarshipViewHolder {

    @BindView(R.id.item_range)
    public TextView itemRange;
    @BindView(R.id.item_avatar)
    public SimpleDraweeView itemAvatar;
    @BindView(R.id.item_name)
    public TextView itemName;
    @BindView(R.id.item_scholarship)
    public TextView itemScholarship;

    public ScholarshipViewHolder(View itemView) {
        ButterKnife.bind(this, itemView);
    }
}
