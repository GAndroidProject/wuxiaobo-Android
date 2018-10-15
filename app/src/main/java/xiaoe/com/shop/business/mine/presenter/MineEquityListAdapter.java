package xiaoe.com.shop.business.mine.presenter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import xiaoe.com.shop.R;

public class MineEquityListAdapter extends BaseAdapter {

    private List<String> mEquityList;
    private Context mContext;
    private LayoutInflater mInflater;

    public MineEquityListAdapter(Context context, List<String> equityList) {
        this.mContext = context;
        this.mEquityList = equityList;
        mInflater = LayoutInflater.from(mContext);
    }

    public MineEquityListAdapter(Activity activity, List<String> equityList) {
        this.mContext = activity;
        this.mEquityList = equityList;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        // 先只返回两个
        return mEquityList == null ? 0 : Math.min(2, mEquityList.size());
    }

    @Override
    public String getItem(int position) {
        return mEquityList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EquityItemViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.mine_vip_equity_item, parent,false);
            viewHolder = new EquityItemViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (EquityItemViewHolder) convertView.getTag();
        }
        viewHolder.vip_item_content.setText(mEquityList.get(position));
        return convertView;
    }

    class EquityItemViewHolder {

        @BindView(R.id.vip_item_content)
        TextView vip_item_content;

        EquityItemViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
        }
    }
}
