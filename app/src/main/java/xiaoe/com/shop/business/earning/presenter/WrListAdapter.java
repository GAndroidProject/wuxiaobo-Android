package xiaoe.com.shop.business.earning.presenter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

import xiaoe.com.common.entitys.WrItem;
import xiaoe.com.shop.R;

public class WrListAdapter extends BaseAdapter {

    private Context mContext;
    private List<WrItem> wrItemList;
    private LayoutInflater mLayoutInflater;

    public WrListAdapter(Context context, List<WrItem> itemList) {
        this.mContext = context;
        this.wrItemList = itemList;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return wrItemList == null ? 0 : wrItemList.size();
    }

    @Override
    public WrItem getItem(int position) {
        return wrItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        WrListViewHolder wrListViewHolder = null;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.wr_list_item, parent, false);
            wrListViewHolder = new WrListViewHolder(convertView);
            convertView.setTag(wrListViewHolder);
        } else {
            wrListViewHolder = (WrListViewHolder) convertView.getTag();
        }

        wrListViewHolder.wrTitle.setText(wrItemList.get(position).getWrTitle());
        String price = "￥" + (wrItemList.get(position).getWrMoney() / 100);
        wrListViewHolder.wrMoney.setText(price);
        wrListViewHolder.wrTime.setText(wrItemList.get(position).getWrTime());
        wrListViewHolder.wrState.setText(wrItemList.get(position).getWrState());

        return convertView;
    }
}
