package xiaoe.com.shop.business.download.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xiaoe.com.shop.R;
import xiaoe.com.shop.adapter.download.DownloadProceedListAdapter;
import xiaoe.com.shop.base.BaseFragment;

public class DownloadProceedFragment extends BaseFragment {
    private static final String TAG = "FinishDownloadFragment";
    private View rootView;
    private RecyclerView downloadProceedRecyclerView;
    private DownloadProceedListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_download_proceed, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
    }

    private void initViews() {
        downloadProceedRecyclerView = (RecyclerView) rootView.findViewById(R.id.download_proceed_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setAutoMeasureEnabled(true);
        downloadProceedRecyclerView.setLayoutManager(layoutManager);
        adapter = new DownloadProceedListAdapter(getContext());
        downloadProceedRecyclerView.setAdapter(adapter);
    }
}
