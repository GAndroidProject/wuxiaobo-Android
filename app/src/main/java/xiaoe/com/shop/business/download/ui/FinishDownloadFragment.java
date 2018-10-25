package xiaoe.com.shop.business.download.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import xiaoe.com.shop.R;
import xiaoe.com.shop.adapter.download.FinishDownloadListAdapter;
import xiaoe.com.shop.base.BaseFragment;
import xiaoe.com.shop.business.column.ui.CacheColumnActivity;
import xiaoe.com.shop.interfaces.IonSlidingViewClickListener;

public class FinishDownloadFragment extends BaseFragment implements IonSlidingViewClickListener {
    private static final String TAG = "FinishDownloadFragment";
    private View rootView;
    private RecyclerView finishDownloadRecyclerView;
    private FinishDownloadListAdapter finishDownloadListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_download_finish, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
    }

    private void initViews() {
        finishDownloadRecyclerView = (RecyclerView) rootView.findViewById(R.id.finish_download_recycler_view);
        finishDownloadRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        finishDownloadListAdapter = new FinishDownloadListAdapter(getContext(), this);
        finishDownloadRecyclerView.setAdapter(finishDownloadListAdapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        Log.d(TAG, "onNavItemClick: "+position);
        if(finishDownloadListAdapter.menuIsOpen()){
            finishDownloadListAdapter.closeMenu();
        }else{
            Intent intent = new Intent(getActivity(), CacheColumnActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onDeleteBtnCilck(View view, int position) {
        Log.d(TAG, "onDeleteBtnCilck: "+position);
        finishDownloadListAdapter.removeData(position);
    }
}
