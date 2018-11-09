package xiaoe.com.shop.adapter.download;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import xiaoe.com.common.app.Global;
import xiaoe.com.common.entitys.DownloadTableInfo;
import xiaoe.com.shop.R;
import xiaoe.com.shop.base.BaseViewHolder;
import xiaoe.com.shop.interfaces.IonSlidingButtonListener;
import xiaoe.com.shop.interfaces.OnDownloadListListener;

public class DownloadProceedChildListHolder extends BaseViewHolder implements View.OnClickListener, View.OnLongClickListener {
    private static final String TAG = "DownloadProceedListHold";
    private final View rootView;
//    private RelativeLayout btnDelete;
//    private RelativeLayout layoutContent;
    private TextView childTitle;
    private TextView downloadSpeed;
    private RelativeLayout btnDownloadStart;
    private final int displayWidth;
    private DownloadTableInfo downloadInfo;
    private TextView downloadWait;
    private ImageView downloadStartIcon;
    private OnDownloadListListener listListener;
    private int mPosition;

    public DownloadProceedChildListHolder(View itemView, IonSlidingButtonListener buttonListener, OnDownloadListListener listener) {
        super(itemView);
        rootView = itemView;
//        ((SlidingButtonView)itemView).setSlidingButtonListener(buttonListener);
        displayWidth = Global.g().getDisplayPixel().x;
        listListener = listener;
        initViews();
    }

    private void initViews() {
        childTitle = (TextView) rootView.findViewById(R.id.child_title);
        downloadSpeed = (TextView) rootView.findViewById(R.id.download_speed);
        btnDownloadStart = (RelativeLayout) rootView.findViewById(R.id.btn_download_start);
        btnDownloadStart.setOnClickListener(this);
        downloadWait = (TextView) rootView.findViewById(R.id.download_wait_text);
        downloadStartIcon = (ImageView) rootView.findViewById(R.id.btn_download_start_icon);
        RelativeLayout childItem = (RelativeLayout) rootView.findViewById(R.id.child_item);
        childItem.setOnLongClickListener(this);
    }

    public void bindView(DownloadTableInfo info, int position){
        mPosition = position;
        downloadInfo = info;
//        layoutContent.getLayoutParams().width = displayWidth;
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
        float progress = info.getProgress() / (1024f * 1024f);
        float totalSize = info.getTotalSize() / (1024f * 1024f);
        float strCount = (float)(Math.round(progress*10*2))/(10*2);
        float strTotal = (float)(Math.round(totalSize*10*2))/(10*2);
        downloadSpeed.setText(strCount+"MB/"+strTotal+"MB");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_download_start:
                if(listListener != null){
                    //0 开始、暂停
                    listListener.downloadItem(downloadInfo, mPosition, 0);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onLongClick(View v) {
        if(v.getId() == R.id.child_item && listListener != null){
            listListener.downloadItem(downloadInfo, mPosition, 1);
        }
        return false;
    }
}
