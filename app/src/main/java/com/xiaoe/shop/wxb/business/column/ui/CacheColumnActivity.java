package com.xiaoe.shop.wxb.business.column.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xiaoe.common.utils.Dp2Px2SpUtil;
import com.xiaoe.shop.wxb.R;
import com.xiaoe.shop.wxb.adapter.column.CacheColumnListAdapter;
import com.xiaoe.shop.wxb.base.XiaoeActivity;

public class CacheColumnActivity extends XiaoeActivity implements View.OnClickListener {
    private static final String TAG = "CacheColumnActivity";
    private RecyclerView recyclerView;
    private TextView cacheColumnTitle;
    private CacheColumnListAdapter columnListAdapter;
    private TextView btnBatchDelete;
    public static boolean isBatchDelete = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cache_column);
        initViews();
    }

    private void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setAutoMeasureEnabled(true);
        recyclerView.setLayoutManager(layoutManager);
        columnListAdapter = new CacheColumnListAdapter(this);
        recyclerView.setAdapter(columnListAdapter);

        cacheColumnTitle = (TextView) findViewById(R.id.cache_column_title);
        cacheColumnTitle.setText("我的财富计划");

        //批量删除按钮
        btnBatchDelete = (TextView) findViewById(R.id.btn_batch_delete);
        btnBatchDelete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_batch_delete:
                clickBatchDelete();
                break;
            default:
                break;
        }
    }

    private void clickBatchDelete() {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) recyclerView.getLayoutParams();
        isBatchDelete = !isBatchDelete;
        if(isBatchDelete){
            columnListAdapter.setMarginLeft(0);
            layoutParams.setMargins(0, 0, 0 ,0);
        }else{
            columnListAdapter.setMarginLeft(Dp2Px2SpUtil.dp2px(this, 20));
            columnListAdapter.setChildPaddingLeft(Dp2Px2SpUtil.dp2px(this, 52));
            layoutParams.setMargins(0, Dp2Px2SpUtil.dp2px(this, 16), 0 ,0);
        }
        columnListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isBatchDelete = false;
    }
}
