package xiaoe.com.shop.business.column.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.List;

import xiaoe.com.common.entitys.ColumnDirectoryEntity;
import xiaoe.com.shop.R;
import xiaoe.com.shop.adapter.tree.TreeRecyclerAdapter;
import xiaoe.com.shop.base.BaseFragment;
import xiaoe.com.shop.business.download.ui.DownloadActivity;

public class ColumnDirectoryFragment extends BaseFragment implements View.OnClickListener {
    private static final String TAG = "ColumnDirectoryFragment";
    private View rootView;
    private RecyclerView directoryRecyclerView;
    private LinearLayout btnBatchDownload;
    private TreeRecyclerAdapter directoryAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_column_directory, null, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        initData();
    }

    private void initView() {
        directoryRecyclerView = (RecyclerView) rootView.findViewById(R.id.directory_recycler_view);
        btnBatchDownload = (LinearLayout) rootView.findViewById(R.id.btn_batch_download);
        btnBatchDownload.setOnClickListener(this);
    }
    private void initData() {
        LinearLayoutManager treeLinearLayoutManager = new LinearLayoutManager(getContext());
        treeLinearLayoutManager.setAutoMeasureEnabled(true);
        directoryRecyclerView.setLayoutManager(treeLinearLayoutManager);
        directoryRecyclerView.setNestedScrollingEnabled(false);
        directoryAdapter = new TreeRecyclerAdapter(getContext());
        directoryRecyclerView.setAdapter(directoryAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_batch_download:
                clickBatchDownload();
                break;
            default:
                break;
        }
    }

    private void clickBatchDownload() {
        Intent intent = new Intent(getContext(), DownloadActivity.class);
        startActivity(intent);
    }

    public void addData(List<ColumnDirectoryEntity> list){
        directoryAdapter.addAll(list);
    }
    public void setData(List<ColumnDirectoryEntity> list){
        directoryAdapter.setData(list);
    }
}
