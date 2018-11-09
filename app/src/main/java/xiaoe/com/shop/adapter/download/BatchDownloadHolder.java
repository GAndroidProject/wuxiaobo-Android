package xiaoe.com.shop.adapter.download;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import xiaoe.com.common.entitys.ColumnDirectoryEntity;
import xiaoe.com.common.entitys.ColumnSecondDirectoryEntity;
import xiaoe.com.common.utils.Dp2Px2SpUtil;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;
import xiaoe.com.shop.interfaces.OnSelectListener;
import xiaoe.com.shop.widget.DashlineItemDivider;

public class BatchDownloadHolder extends BaseViewHolder implements View.OnClickListener,OnSelectListener {
    private static final String TAG = "BatchDownloadHolder";
    private final View mItemView;
    private RelativeLayout btnAllSelect;
    private TextView title;
    private LinearLayout btnExpandDown;
    private View line01;
    private RecyclerView childRecyclerView;
    private RelativeLayout btnExpandTop;
    private final Context mContext;
    private BatchChildDownloadAdapter childDownloadAdapter;
    private ColumnDirectoryEntity mItemData = null;
    private ImageView allSelectIcon;
    private OnSelectListener parentSelectListener;
    private int mPosition;

    public BatchDownloadHolder(Context context, View itemView, OnSelectListener selectListener) {
        super(itemView);
        mItemView = itemView;
        mContext = context;
        parentSelectListener = selectListener;
        initView();
    }

    private void initView() {
        btnAllSelect = (RelativeLayout) mItemView.findViewById(R.id.btn_little_all_select);
        btnAllSelect.setOnClickListener(this);
        allSelectIcon = (ImageView) mItemView.findViewById(R.id.little_all_select_icon);

        title = (TextView) mItemView.findViewById(R.id.text);
        btnExpandDown = (LinearLayout) mItemView.findViewById(R.id.expand_down_btn);
        line01 = mItemView.findViewById(R.id.line_1);

        childRecyclerView = (RecyclerView) mItemView.findViewById(R.id.tree_child_recycler_view);
        childRecyclerView.setVisibility(View.GONE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        childRecyclerView.setLayoutManager(layoutManager);
        int padding = Dp2Px2SpUtil.dp2px(mContext, 30);
        childRecyclerView.addItemDecoration(new DashlineItemDivider(padding, padding));
        childDownloadAdapter = new BatchChildDownloadAdapter(mContext);
        childRecyclerView.setAdapter(childDownloadAdapter);

        btnExpandTop = (RelativeLayout) mItemView.findViewById(R.id.expand_top_btn);
    }

    public void bindView(ColumnDirectoryEntity itemData, int position){
        mItemData = itemData;
        mPosition = position;
        //展开事件
        btnExpandDown.setOnClickListener(this);
        //收起事件
        btnExpandTop.setOnClickListener(this);
        if(itemData.isExpand()){
            setExpandShow();
        }else{
            setExpandHide();
        }
        setAllSelect(itemData.isSelect());
        childDownloadAdapter.setSelectListener(this);
        childDownloadAdapter.setParentPosition(position);
        childDownloadAdapter.setItemData(itemData.getResource_list());
        title.setText(mItemData.getTitle());
    }
    private void setExpandShow(){
        btnExpandDown.setVisibility(View.GONE);
        line01.setVisibility(View.VISIBLE);
        childRecyclerView.setVisibility(View.VISIBLE);
        btnExpandTop.setVisibility(View.VISIBLE);
    }
    private void setExpandHide(){
        btnExpandDown.setVisibility(View.VISIBLE);
        line01.setVisibility(View.GONE);
        childRecyclerView.setVisibility(View.GONE);
        btnExpandTop.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.expand_down_btn:
                //展开
                setExpandShow();
                mItemData.setExpand(true);
                break;
            case R.id.expand_top_btn:
                //收起
                setExpandHide();
                mItemData.setExpand(false);
                break;
            case R.id.btn_little_all_select:
                clickAllSelect();
                break;
            default:
                break;
        }
    }
    private void setAllSelect(boolean allSelect){
        if(allSelect){
            allSelectIcon.setImageResource(R.mipmap.download_checking);
        }else{
            allSelectIcon.setImageResource(R.mipmap.download_tocheck);
        }
        mItemData.setSelect(allSelect);
        if(parentSelectListener != null){
            parentSelectListener.onSelect(mPosition);
        }
    }
    private void clickAllSelect() {
        if(mItemData.isSelect()){
            for (ColumnSecondDirectoryEntity item: mItemData.getResource_list()) {
                item.setSelect(false);
            }
            mItemData.setSelect(false);
        }else{
            for (ColumnSecondDirectoryEntity item: mItemData.getResource_list()) {
                item.setSelect(true);
            }
            mItemData.setSelect(true);
        }
        setAllSelect(mItemData.isSelect());
        childDownloadAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSelect(int positiont) {
        int select = 0;
        for (ColumnSecondDirectoryEntity item : mItemData.getResource_list()) {
            if(item.isSelect() || !item.isEnable()){
                select++;
            }
        }
        setAllSelect(select == mItemData.getResource_list().size());
    }
}
