package xiaoe.com.shop.test;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import xiaoe.com.shop.R;
import xiaoe.com.shop.adapter.tree.TreeRecyclerAdapter;
import xiaoe.com.shop.base.XiaoeActivity;

public class TreeActivity extends XiaoeActivity {
    private String TAG = "TreeActivity";
    private RecyclerView treeRecyclerView;
    private TreeRecyclerAdapter treeAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tree);
        treeRecyclerView = (RecyclerView) findViewById(R.id.tree_recycler_view);
        LinearLayoutManager treeLinearLayoutManager = new LinearLayoutManager(this);
        treeLinearLayoutManager.setAutoMeasureEnabled(true);
        treeRecyclerView.setLayoutManager(treeLinearLayoutManager);
        treeAdapter = new TreeRecyclerAdapter(this);
        treeRecyclerView.setAdapter(treeAdapter);
        initData();
    }

    private void initData() {
        treeAdapter.addAll(treeAdapter.getData(),0);
    }
}
