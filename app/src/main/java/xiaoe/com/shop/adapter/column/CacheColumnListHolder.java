package xiaoe.com.shop.adapter.column;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;
import xiaoe.com.shop.business.column.ui.CacheColumnActivity;
import xiaoe.com.shop.widget.DashlineItemDivider;

public class CacheColumnListHolder extends BaseViewHolder implements View.OnClickListener {
    private static final String TAG = "CacheColumnListHolder";
    private final View rootView;
    private RelativeLayout itemContentWrap;
    private RelativeLayout btnSelect;
    private TextView title;
    private LinearLayout btnExpandDown;
    private LinearLayout btnPlayAll;
    private LinearLayout itemChildContentWrap;
    private RelativeLayout btnExpandTop;
    private final Context mContext;
    private RecyclerView childClassRecyclerView;
    private CacheColumnChildListAdapter childListAdapter;
    private int childPaddingLeft = 0;
    private int childPaddingRight = 0;


    public CacheColumnListHolder(Context context, View itemView, int left, int right) {
        super(itemView);
        rootView = itemView;
        mContext = context;
        childPaddingLeft = left;
        childPaddingRight = right;
        initViews();
    }

    private void initViews() {
        itemContentWrap = (RelativeLayout) rootView.findViewById(R.id.item_content_wrap);
        btnSelect = (RelativeLayout) rootView.findViewById(R.id.btn_select);
        title = (TextView) rootView.findViewById(R.id.column_title);
        title.setText("【初级】我的房产计划");
        //展开按钮
        btnExpandDown = (LinearLayout) rootView.findViewById(R.id.btn_expand_down);
        btnExpandDown.setOnClickListener(this);
        //播放全部按钮
        btnPlayAll = (LinearLayout) rootView.findViewById(R.id.btn_play_all);
        btnPlayAll.setOnClickListener(this);
        //子列表内容
        itemChildContentWrap = (LinearLayout) rootView.findViewById(R.id.item_child_content_wrap);
        itemChildContentWrap.setVisibility(View.GONE);
        childClassRecyclerView = (RecyclerView) rootView.findViewById(R.id.child_class_recycler_view);
        RecyclerView.LayoutManager childLayoutManager = new LinearLayoutManager(mContext);
        childLayoutManager.setAutoMeasureEnabled(true);
        childClassRecyclerView.setLayoutManager(childLayoutManager);
        childClassRecyclerView.addItemDecoration(new DashlineItemDivider(childPaddingLeft, childPaddingRight));
        childClassRecyclerView.setNestedScrollingEnabled(false);
        childListAdapter = new CacheColumnChildListAdapter(mContext);
        childClassRecyclerView.setAdapter(childListAdapter);

        //收起按钮
        btnExpandTop = (RelativeLayout) rootView.findViewById(R.id.btn_expand_top);
        btnExpandTop.setOnClickListener(this);
    }

    public void bindView(){
        if(CacheColumnActivity.isBatchDelete){
            itemContentWrap.setBackgroundResource(R.drawable.top_bottom_border);
            btnSelect.setVisibility(View.VISIBLE);
            btnPlayAll.setVisibility(View.GONE);
        }else{
            itemContentWrap.setBackgroundResource(R.drawable.border);
            btnSelect.setVisibility(View.GONE);
        }
        childListAdapter.notifyDataSetChanged();
        setLayoutMargin();
    }
    private void setLayoutMargin(){
        RecyclerView.LayoutParams itemContentLayoutParams = (RecyclerView.LayoutParams) itemContentWrap.getLayoutParams();
        itemContentLayoutParams.setMargins(CacheColumnListAdapter.marginLeft, 0 , CacheColumnListAdapter.marginLeft , CacheColumnListAdapter.marginBottom);
        itemContentWrap.setLayoutParams(itemContentLayoutParams);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_expand_down:
                setExpandState(true);
                break;
            case R.id.btn_expand_top:
                setExpandState(false);
                break;
            case R.id.btn_play_all:

                break;
            default:
                break;
        }
    }

    private void setExpandState(boolean expand){
        itemChildContentWrap.setVisibility(expand ? View.VISIBLE : View.GONE);
        if(!CacheColumnActivity.isBatchDelete){
            btnPlayAll.setVisibility(expand ? View.VISIBLE : View.GONE);
        }
        btnExpandDown.setVisibility(expand ? View.GONE : View.VISIBLE);
    }

    public CacheColumnChildListAdapter getChildListAdapter(){
        return  childListAdapter;
    }
}
