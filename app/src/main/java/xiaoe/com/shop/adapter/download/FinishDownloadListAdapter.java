package xiaoe.com.shop.adapter.download;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import xiaoe.com.common.entitys.DownloadResourceTableInfo;
import xiaoe.com.shop.R;
import xiaoe.com.shop.interfaces.IonSlidingButtonListener;
import xiaoe.com.shop.interfaces.IonSlidingViewClickListener;
import xiaoe.com.shop.widget.SlidingButtonView;

public class FinishDownloadListAdapter extends RecyclerView.Adapter<FinishDownloadListHolder> implements IonSlidingButtonListener {
    private static final String TAG = "FinishDownloadListAdapt";

    private Context mContext;
    private SlidingButtonView bntDelete;
    private IonSlidingViewClickListener mSlidingViewClickListener;
    private List<DownloadResourceTableInfo> mDatas = new ArrayList<DownloadResourceTableInfo>();

    public FinishDownloadListAdapter(Context context, IonSlidingViewClickListener slidingViewClickListener){
        mContext = context;
        mSlidingViewClickListener = slidingViewClickListener;

    }

    @Override
    public FinishDownloadListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_finish_download, parent, false);
        return new FinishDownloadListHolder(mContext, view, this, mSlidingViewClickListener);
    }

    @Override
    public void onBindViewHolder(FinishDownloadListHolder holder, int position) {
        holder.bindView(mDatas.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    @Override
    public void onMenuIsOpen(View view) {
        bntDelete = (SlidingButtonView) view;
    }

    @Override
    public void onDownOrMove(SlidingButtonView slidingButtonView) {
        if(menuIsOpen()){
            if(bntDelete != slidingButtonView){
                closeMenu();
            }
        }
    }
    /**
     * 关闭菜单
     */
    public void closeMenu() {
        bntDelete.closeMenu();
        bntDelete = null;

    }
    /**
     * 判断是否有菜单打开
     */
    public Boolean menuIsOpen() {
        if(bntDelete != null){
            return true;
        }
        return false;
    }

    public void setData(List<DownloadResourceTableInfo> list){
        mDatas.clear();
        mDatas.addAll(list);
        notifyDataSetChanged();
    }

    public List<DownloadResourceTableInfo> getDataList(){
        return mDatas;
    }
    public DownloadResourceTableInfo getPositionData(int position){
        if(mDatas == null || position >= mDatas.size()){
            return null;
        }
        return mDatas.get(position);
    }

    public DownloadResourceTableInfo removeData(int position){
        DownloadResourceTableInfo remove = mDatas.remove(position);
        notifyDataSetChanged();
        return remove;
    }
}
