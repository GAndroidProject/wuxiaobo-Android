package xiaoe.com.shop.adapter.download;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import xiaoe.com.common.entitys.DownloadResourceTableInfo;
import xiaoe.com.common.utils.Dp2Px2SpUtil;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;
import xiaoe.com.shop.widget.DashlineItemDivider;

public class DownloadProceedListHolder extends BaseViewHolder implements View.OnClickListener {
    private static final String TAG = "DownloadProceedListHold";
    private final View rootView;
    private TextView title;
    private LinearLayout btnExpandDown;
    private RecyclerView childRecyclerView;
    private RelativeLayout btnExpandTop;
    private Context mContext;
    private DownloadProceedChildListAdapter childListAdapter;
    private final int padding;

    public DownloadProceedListHolder(Context context, View itemView) {
        super(itemView);
        rootView = itemView;
        mContext = context;
        padding = Dp2Px2SpUtil.dp2px(mContext,20);
        initViews();
    }

    private void initViews() {
        title = (TextView) rootView.findViewById(R.id.title);
        btnExpandDown = (LinearLayout) rootView.findViewById(R.id.btn_expand_down);
        btnExpandDown.setOnClickListener(this);

        childRecyclerView = (RecyclerView) rootView.findViewById(R.id.child_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
        layoutManager.setAutoMeasureEnabled(true);
        childRecyclerView.setLayoutManager(layoutManager);
        childRecyclerView.setNestedScrollingEnabled(false);
        childRecyclerView.addItemDecoration(new DashlineItemDivider(padding, padding));
        childListAdapter = new DownloadProceedChildListAdapter(mContext, null);
        childRecyclerView.setAdapter(childListAdapter);
        btnExpandTop = (RelativeLayout) rootView.findViewById(R.id.btn_expand_top);
        btnExpandTop.setOnClickListener(this);
    }

    public void bindView(DownloadResourceTableInfo info, int position){
        title.setText(info.getTitle());
        childListAdapter.setData(info.getChildResourceList());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_expand_down:
                Log.d(TAG, "onClick: btn_expand_down");
                childRecyclerView.setVisibility(View.VISIBLE);
                btnExpandTop.setVisibility(View.VISIBLE);
                btnExpandDown.setVisibility(View.GONE);
                break;
            case R.id.btn_expand_top:
                Log.d(TAG, "onClick: btn_expand_top");
                childRecyclerView.setVisibility(View.GONE);
                btnExpandTop.setVisibility(View.GONE);
                btnExpandDown.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }
}
