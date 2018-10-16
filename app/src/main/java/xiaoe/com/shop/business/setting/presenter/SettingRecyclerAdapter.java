package xiaoe.com.shop.business.setting.presenter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xiaoe.com.common.entitys.SettingItemInfo;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;

public class SettingRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private Context mContext;
    private List<SettingItemInfo> itemList;

    public SettingRecyclerAdapter(Context context, List<SettingItemInfo> itemList) {
        this.mContext = context;
        this.itemList = itemList;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class SettingItemViewHolder {

        @BindView(R.id.item_title)
        TextView itemTitle;
        @BindView(R.id.item_icon)
        SimpleDraweeView itemIcon;
        @BindView(R.id.item_content)
        TextView itemContent;

        SettingItemViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }
}
