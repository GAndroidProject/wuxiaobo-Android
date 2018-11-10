package xiaoe.com.shop.business.download.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import xiaoe.com.common.entitys.DownloadResourceTableInfo;
import xiaoe.com.network.downloadUtil.DownloadManager;
import xiaoe.com.shop.R;
import xiaoe.com.shop.adapter.download.FinishDownloadListAdapter;
import xiaoe.com.shop.base.BaseFragment;
import xiaoe.com.shop.common.JumpDetail;
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
        List<DownloadResourceTableInfo> list = DownloadManager.getInstance().getDownloadFinishList();
        if(list !=null && list.size() > 0){
            finishDownloadListAdapter.setData(list);
        }

    }

    @Override
    public void onItemClick(View view, int position) {
        if(finishDownloadListAdapter.menuIsOpen()){
            finishDownloadListAdapter.closeMenu();
        }else{
            DownloadResourceTableInfo download = finishDownloadListAdapter.getPositionData(position);

            if(download == null){
                return;
            }else if(download.getResourceType() == 1){
                //音频
                JumpDetail.jumpAudio(getContext(), download.getResourceId(), 1);
            }else if(download.getResourceType() == 2){
                //视频
                JumpDetail.jumpVideo(getContext(), download.getResourceId(), null, true);
            }
        }
    }
    @Override
    public void onDeleteBtnCilck(View view, int position) {
        DownloadResourceTableInfo remove = finishDownloadListAdapter.removeData(position);
        DownloadManager.getInstance().removeDownloadFinish(remove);
    }
}
