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

import xiaoe.com.common.app.Global;
import xiaoe.com.common.entitys.DownloadTableInfo;
import xiaoe.com.common.utils.DateFormat;
import xiaoe.com.network.downloadUtil.DownloadManager;
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
            DownloadManager  downloadManager = DownloadManager.getInstance();
            DownloadTableInfo downloadTableInfo = new DownloadTableInfo();
            downloadTableInfo.setAppId("123");
            downloadTableInfo.setResourceId("1234");
            downloadTableInfo.setColumnId("12345");
            downloadTableInfo.setBigColumnId("123456");
            downloadTableInfo.setFileName("test.mp3");
            downloadTableInfo.setTotalSize(0);
            downloadTableInfo.setFileType(1);
            downloadTableInfo.setResourceInfo("9999999");
            downloadTableInfo.setDownloadState(0);
            downloadTableInfo.setLocalFilePath(Global.g().getDefaultDirectory());
            downloadTableInfo.setCreateAt(DateFormat.currentTime());
            downloadTableInfo.setUpdateAt(DateFormat.currentTime());
            downloadTableInfo.setFileDownloadUrl("http://wechatapppro-1252524126.file.myqcloud.com/appCLMHoHyA1464/audio/b96ec47d26b1480e010827566ba91e29.mp3");
            downloadTableInfo.setProgress(0);

//            downloadManager.start(downloadTableInfo);
//            DownloadFileConfig.getInstance().getDownloadInfo();



            DownloadManager  downloadManager2 = DownloadManager.getInstance();
            DownloadTableInfo downloadTableInfo2 = new DownloadTableInfo();
            downloadTableInfo2.setAppId("sunny");
            downloadTableInfo2.setResourceId("1234");
            downloadTableInfo2.setColumnId("12345");
            downloadTableInfo2.setBigColumnId("123456");
            downloadTableInfo2.setFileName("test.mp3");
            downloadTableInfo2.setTotalSize(0);
            downloadTableInfo2.setFileType(1);
            downloadTableInfo2.setResourceInfo("9999999");
            downloadTableInfo2.setDownloadState(0);
            downloadTableInfo2.setLocalFilePath(Global.g().getDefaultDirectory());
            downloadTableInfo2.setCreateAt(DateFormat.currentTime());
            downloadTableInfo2.setUpdateAt(DateFormat.currentTime());
            downloadTableInfo2.setFileDownloadUrl("http://wechatapppro-1252524126.file.myqcloud.com/appCLMHoHyA1464/audio/b96ec47d26b1480e010827566ba91e29.mp3");
            downloadTableInfo2.setProgress(0);

//            downloadManager2.start(downloadTableInfo2);

        }
    }

    @Override
    public void onDeleteBtnCilck(View view, int position) {
        Log.d(TAG, "onDeleteBtnCilck: "+position);
        finishDownloadListAdapter.removeData(position);
    }
}
