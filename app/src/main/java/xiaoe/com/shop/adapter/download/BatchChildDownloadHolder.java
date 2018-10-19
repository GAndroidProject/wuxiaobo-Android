package xiaoe.com.shop.adapter.download;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import xiaoe.com.common.entitys.ItemData;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;
import xiaoe.com.shop.interfaces.OnSelectListener;

public class BatchChildDownloadHolder extends BaseViewHolder implements View.OnClickListener {
    private static final String TAG = "BatchDownloadHolder";
    private final View mItemView;
    private RelativeLayout btnSelect;
    private TextView title;
    private View line;
    private TextView time;
    private ImageView selectIcon;
    private ItemData mData = null;
    private int mParentPosition;
    private OnSelectListener mSelectListener;

    public BatchChildDownloadHolder(View itemView, int parentPosition, OnSelectListener selectListener) {
        super(itemView);
        mItemView = itemView;
        mParentPosition = parentPosition;
        mSelectListener = selectListener;
        initView();
    }

    private void initView() {
        line = mItemView.findViewById(R.id.line);
        btnSelect = (RelativeLayout) mItemView.findViewById(R.id.btn_select);
        btnSelect.setOnClickListener(this);
        selectIcon = (ImageView) mItemView.findViewById(R.id.select_icon);
        title = (TextView) mItemView.findViewById(R.id.text);
        time = (TextView) mItemView.findViewById(R.id.item_time);
    }

    public void bindView(ItemData itemData, int position){
        mData = itemData;
        if(itemData.isSelect()){
            selectIcon.setImageResource(R.mipmap.download_checking);
        }else{
            selectIcon.setImageResource(R.mipmap.download_tocheck);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_select:
                clickSelect();
                break;
            default:
                break;
        }
    }
    private void setSelect(boolean select){
        if(select){
            selectIcon.setImageResource(R.mipmap.download_checking);
        }else{
            selectIcon.setImageResource(R.mipmap.download_tocheck);
        }
    }

    private void clickSelect() {
        mData.setSelect(!mData.isSelect());
        setSelect(mData.isSelect());
        if(mSelectListener != null){
            mSelectListener.onSelect(mParentPosition);
        }

    }
}
