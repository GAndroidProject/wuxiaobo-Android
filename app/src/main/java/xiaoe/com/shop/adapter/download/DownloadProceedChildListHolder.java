package xiaoe.com.shop.adapter.download;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import xiaoe.com.common.app.Global;
import xiaoe.com.common.entitys.DownloadTableInfo;
import xiaoe.com.network.downloadUtil.DownloadManager;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;
import xiaoe.com.shop.interfaces.IonSlidingButtonListener;
import xiaoe.com.shop.widget.SlidingButtonView;

public class DownloadProceedChildListHolder extends BaseViewHolder implements View.OnClickListener {
    private static final String TAG = "DownloadProceedListHold";
    private final View rootView;
    private RelativeLayout btnDelete;
    private RelativeLayout layoutContent;
    private TextView childTitle;
    private TextView downloadSpeed;
    private RelativeLayout btnDownloadStart;
    private final int displayWidth;
    private DownloadTableInfo downloadInfo;
    private TextView downloadWait;
    private ImageView downloadStartIcon;

    public DownloadProceedChildListHolder(View itemView, IonSlidingButtonListener buttonListener) {
        super(itemView);
        rootView = itemView;
        ((SlidingButtonView)itemView).setSlidingButtonListener(buttonListener);
        displayWidth = Global.g().getDisplayPixel().x;
        initViews();
    }

    private void initViews() {
        btnDelete = (RelativeLayout) rootView.findViewById(R.id.btn_delete);
        btnDelete.setOnClickListener(this);
        layoutContent = (RelativeLayout) rootView.findViewById(R.id.layout_content);
        childTitle = (TextView) rootView.findViewById(R.id.child_title);
        downloadSpeed = (TextView) rootView.findViewById(R.id.download_speed);
        btnDownloadStart = (RelativeLayout) rootView.findViewById(R.id.btn_download_start);
        btnDownloadStart.setOnClickListener(this);
        downloadWait = (TextView) rootView.findViewById(R.id.download_wait_text);
        downloadStartIcon = (ImageView) rootView.findViewById(R.id.btn_download_start_icon);
    }

    public void bindView(DownloadTableInfo info, int position){
        downloadInfo = info;
        layoutContent.getLayoutParams().width = displayWidth;
        childTitle.setText(info.getTitle());
        downloadWait.setVisibility(View.GONE);
        downloadStartIcon.setVisibility(View.VISIBLE);
        //0-等待，1-下载中，2-暂停，3-完成
        int state = info.getDownloadState();
        if(state == 0){
            downloadStartIcon.setVisibility(View.GONE);
            downloadWait.setVisibility(View.VISIBLE);
        }else if(state == 1){
            downloadStartIcon.setImageResource(R.mipmap.download_stop);
        }else if(state == 2){
            downloadStartIcon.setImageResource(R.mipmap.download_play);
        }
        long progress = info.getProgress() / (1024 * 1024);
        long totalSize = info.getTotalSize() / (1024 * 1024);
        downloadSpeed.setText(progress+"MB/"+totalSize+"MB");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_download_start:
                if(downloadInfo.getDownloadState() == 0 || downloadInfo.getDownloadState() == 1){
                    DownloadManager.getInstance().pause(downloadInfo);
                }else if(downloadInfo.getDownloadState() == 2){
                    DownloadManager.getInstance().start(downloadInfo);
                }
                break;
            default:
                break;
        }
    }
}
