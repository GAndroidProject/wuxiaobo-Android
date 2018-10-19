package xiaoe.com.shop.adapter.download;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;
import xiaoe.com.shop.interfaces.IonSlidingButtonListener;
import xiaoe.com.shop.widget.SlidingButtonView;

public class DownloadProceedChildListAdapter extends RecyclerView.Adapter<BaseViewHolder>  implements IonSlidingButtonListener {
    private static final String TAG = "DownloadProceedListAdap";
    private final Context mContext;
    private SlidingButtonView bntDelete;

    public DownloadProceedChildListAdapter(Context context) {
        mContext = context;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_child_download_proceed, parent, false);
        return new DownloadProceedChildListHolder(view, this);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        DownloadProceedChildListHolder childListHolder = (DownloadProceedChildListHolder) holder;
        childListHolder.bindView();
    }

    @Override
    public int getItemCount() {
        return 3;
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
}
