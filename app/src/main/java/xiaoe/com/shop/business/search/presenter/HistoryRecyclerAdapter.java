package xiaoe.com.shop.business.search.presenter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import xiaoe.com.common.entitys.SearchHistory;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;

public class HistoryRecyclerAdapter extends RecyclerView.Adapter<BaseViewHolder> {

    private Context mContext;
    private List<SearchHistory> historyList;

    private OnTabClickListener onTabClickListener;

    public HistoryRecyclerAdapter(Context context, List<SearchHistory> itemList) {
        this.mContext = context;
        this.historyList = itemList;
    }

    public void setOnTabClickListener(OnTabClickListener onTabClickListener) {
        this.onTabClickListener = onTabClickListener;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.search_main_item, null);
        return new SearchMainContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        SearchMainContentViewHolder searchMainContentViewHolder = (SearchMainContentViewHolder) holder;
        String content = historyList.get(position).getmContent();
        searchMainContentViewHolder.content.setText(content);
        final int tempPos = searchMainContentViewHolder.getAdapterPosition();
        searchMainContentViewHolder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onTabClickListener != null) {
                    onTabClickListener.onItemClick(v, tempPos);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return historyList == null ? 0 : historyList.size();
    }
}
