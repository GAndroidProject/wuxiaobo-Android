package xiaoe.com.shop.business.download.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import xiaoe.com.common.entitys.DownloadTableInfo;
import xiaoe.com.network.downloadUtil.DownloadEvent;
import xiaoe.com.network.downloadUtil.DownloadManager;
import xiaoe.com.shop.R;
import xiaoe.com.shop.adapter.download.DownloadProceedChildListAdapter;
import xiaoe.com.shop.base.BaseFragment;

public class DownloadProceedFragment extends BaseFragment {
    private static final String TAG = "FinishDownloadFragment";
    private View rootView;
    private RecyclerView downloadProceedRecyclerView;
//    private DownloadProceedListAdapter adapter;
    private DownloadProceedChildListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        rootView = inflater.inflate(R.layout.fragment_download_proceed, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews();
        initData();
    }

    private void initViews() {
        downloadProceedRecyclerView = (RecyclerView) rootView.findViewById(R.id.download_proceed_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setAutoMeasureEnabled(true);
        downloadProceedRecyclerView.setLayoutManager(layoutManager);
//        adapter = new DownloadProceedListAdapter(getContext());
        adapter = new DownloadProceedChildListAdapter(getContext());
        downloadProceedRecyclerView.setAdapter(adapter);
    }
    private void initData() {
        adapter.addAllData(DownloadManager.getInstance().getDownloadingList());
    }

    @Subscribe
    public void onEventMainThread(DownloadEvent event) {
        if(event.getStatus() == 3){
//            DownloadTableInfo downloadTableInfo = event.getDownloadInfo();
//            int type = downloadTableInfo.getResourceType();
//            String resourceId = downloadTableInfo.getResourceId();
//            String columnId = downloadTableInfo.getResourceId();
//            for (DownloadResourceTableInfo item : adapter.getData()){
//                if(item.getResourceType() == 1 || item.getResourceType() == 2){
//                    //说明是单品
//                    if(resourceId.equals(item.getResource().getResourceId())){
//                        adapter.getData().remove(item);
//                    }
//                }else{
//                    //一个专栏
//                    for (DownloadTableInfo childItem : item.getChildResourceList()){
//                        if(resourceId.equals(childItem.getResourceId())){
//                            item.getChildResourceList().remove(childItem);
//                        }
//                        if(item.getChildResourceList().size() <= 0){
//                            //说明专栏全部下载完成
//                            adapter.getData().remove(item);
//                            break;
//                        }
//                    }
//                }
//            }
        }
        Log.d(TAG, "onEventMainThread: -------------------");
        int index = -1;
        for (DownloadTableInfo item : adapter.getData()){
            index++;
            if(item.getResourceId().equals(event.getDownloadInfo().getResourceId())){
                adapter.notifyItemChanged(index);
                break;
            }
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}
