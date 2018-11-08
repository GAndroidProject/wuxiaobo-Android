package xiaoe.com.shop.adapter.download;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import xiaoe.com.common.entitys.ColumnDirectoryEntity;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;
import xiaoe.com.shop.interfaces.OnSelectListener;

public class BatchDownloadAdapter extends RecyclerView.Adapter<BaseViewHolder>  {
    private static final String TAG = "BatchDownloadRecycler";
    private final Context mContext;
    private List<ColumnDirectoryEntity> itemData = new ArrayList<ColumnDirectoryEntity>();
    private OnSelectListener selectListener;

    public BatchDownloadAdapter(Context context) {
        mContext = context;
        itemData = new ArrayList<ColumnDirectoryEntity>();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.view_item_batch_download_recycler, parent, false);
        return new BatchDownloadHolder(mContext, view, selectListener);
    }

    @Override
    public void onBindViewHolder(final BaseViewHolder holder, int position) {
        final BatchDownloadHolder batchDownloadViewHolder = (BatchDownloadHolder) holder;
        batchDownloadViewHolder.bindView(itemData.get(position), position);
    }

    @Override
    public int getItemCount() {
        return itemData.size();
    }

//    public void setItemData(List<ColumnDirectoryEntity> date){
//        itemData = date;
//        notifyDataSetChanged();
//    }

    public void addAllData(List<ColumnDirectoryEntity> list){
        itemData.addAll(list);
        notifyDataSetChanged();
    }

    public List<ColumnDirectoryEntity> getDate(){
        return itemData;
    }

    public void setSelectListener(OnSelectListener selectListener) {
        this.selectListener = selectListener;
    }
}
