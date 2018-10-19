package xiaoe.com.shop.adapter.download;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import xiaoe.com.common.app.Global;
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
    }

    public void bindView(){
        childTitle.setText("2019买房还是投资的最佳选择吗");
        downloadSpeed.setText("18.8/30.8M");
        layoutContent.getLayoutParams().width = displayWidth;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            default:
                break;
        }
    }
}
