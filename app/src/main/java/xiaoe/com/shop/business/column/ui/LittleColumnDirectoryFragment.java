package xiaoe.com.shop.business.column.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import xiaoe.com.common.entitys.ItemData;
import xiaoe.com.shop.R;
import xiaoe.com.shop.adapter.tree.TreeChildRecyclerAdapter;
import xiaoe.com.shop.base.BaseFragment;

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
        directoryAdapter = new TreeChildRecyclerAdapter(getContext());
        directoryRecyclerView.setAdapter(directoryAdapter);
        List<ItemData> childList = new ArrayList<ItemData>();
        for (int j = 0; j < 15; j++){
            childList.add(new ItemData(ItemData.ITEM_TYPE_CHILD, "音视频图文-"+j,"",UUID.randomUUID().toString(), 2,null));
        }
        directoryAdapter.setData(childList);
    }

    public void add(){
        List<ItemData> childList = new ArrayList<ItemData>();
        for (int j = 0; j < 5; j++){
            childList.add(new ItemData(ItemData.ITEM_TYPE_CHILD, "音视频图文-"+j,"",UUID.randomUUID().toString(), 2,null));
        }
        directoryAdapter.addAll(childList);
    }
}
