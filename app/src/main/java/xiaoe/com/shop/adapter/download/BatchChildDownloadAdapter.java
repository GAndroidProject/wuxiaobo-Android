package xiaoe.com.shop.adapter.download;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import xiaoe.com.common.entitys.ColumnSecondDirectoryEntity;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;
import xiaoe.com.shop.interfaces.OnSelectListener;

public class BatchChildDownloadAdapter extends RecyclerView.Adapter<BaseViewHolder>  {
    private static final String TAG = "BatchDownloadRecycler";
    private final Context mContext;
    private int mParentPosition = 0;
    private List<ColumnSecondDirectoryEntity> itemData = new ArrayList<ColumnSecondDirectoryEntity>();
    private OnSelectListener selectListener;

    public BatchChildDownloadAdapter(Context context) {
        mContext = context;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.view_item_child_batch_download, parent, false);
        return new BatchChildDownloadHolder(view, mParentPosition, selectListener);
    }

    @Override
    public void onBindViewHolder(final BaseViewHolder holder, int position) {
        final BatchChildDownloadHolder batchDownloadViewHolder = (BatchChildDownloadHolder) holder;
        batchDownloadViewHolder.bindView(itemData.get(position), position);
    }

    public void setSelectListener(OnSelectListener selectListener) {
        this.selectListener = selectListener;
    }

    public void setParentPosition(int mParentPosition) {
        this.mParentPosition = mParentPosition;
    }

    @Override
    public int getItemCount() {
        return itemData.size();
    }

    public void setItemData(List<ColumnSecondDirectoryEntity> date){
        itemData = date;
        notifyDataSetChanged();
    }

}
