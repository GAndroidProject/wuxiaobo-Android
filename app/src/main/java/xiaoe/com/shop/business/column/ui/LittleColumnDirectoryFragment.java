package xiaoe.com.shop.business.column.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import xiaoe.com.common.entitys.ColumnSecondDirectoryEntity;
import xiaoe.com.common.utils.Dp2Px2SpUtil;
import xiaoe.com.shop.R;
import xiaoe.com.shop.adapter.tree.TreeChildRecyclerAdapter;
import xiaoe.com.shop.base.BaseFragment;
import xiaoe.com.shop.widget.DashlineItemDivider;

public class LittleColumnDirectoryFragment extends BaseFragment {
    private static final String TAG = "ColumnDirectoryFragment";
    private View rootView;
    private RecyclerView directoryRecyclerView;
    private TreeChildRecyclerAdapter directoryAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_little_column_directory, null, false);
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
    }
    private void initData() {
        LinearLayoutManager treeLinearLayoutManager = new LinearLayoutManager(getContext());
        treeLinearLayoutManager.setAutoMeasureEnabled(true);
        directoryRecyclerView.setLayoutManager(treeLinearLayoutManager);
        directoryRecyclerView.setNestedScrollingEnabled(false);
        int padding = Dp2Px2SpUtil.dp2px(getContext(), 16);
        directoryRecyclerView.addItemDecoration(new DashlineItemDivider(padding, padding));
        directoryAdapter = new TreeChildRecyclerAdapter(getContext());
        directoryRecyclerView.setAdapter(directoryAdapter);
    }

    public void addData(List<ColumnSecondDirectoryEntity> list){
        directoryAdapter.addAll(list);
    }
    public void setData(List<ColumnSecondDirectoryEntity> list){
        directoryAdapter.setData(list);
    }
}
