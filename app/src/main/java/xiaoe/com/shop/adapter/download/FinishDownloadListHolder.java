package xiaoe.com.shop.adapter.download;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import xiaoe.com.common.app.Global;
import xiaoe.com.common.entitys.DownloadResourceTableInfo;
import xiaoe.com.shop.R;
import xiaoe.com.shop.interfaces.IonSlidingButtonListener;
import xiaoe.com.shop.interfaces.IonSlidingViewClickListener;
import xiaoe.com.shop.widget.SlidingButtonView;

public class FinishDownloadListHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    private static final String TAG = "FinishDownloadListHolde";
    private final View rootView;
    private final Context mContext;
    private TextView title;
    private LinearLayout layoutContent;
    private final int displayWidth;
    private SimpleDraweeView image;
    private TextView desc;
    private TextView count;
    private TextView totalSize;
    private IonSlidingViewClickListener mIonSlidingViewClickListener;
    private int mPosition;
    private RelativeLayout btnDelete;

    public FinishDownloadListHolder(Context context, View itemView, IonSlidingButtonListener slidingButtonListener, IonSlidingViewClickListener slidingViewClickListener) {
        super(itemView);
        rootView = itemView;
        mContext = context;
        ((SlidingButtonView)rootView).setSlidingButtonListener(slidingButtonListener);
        displayWidth = Global.g().getDisplayPixel().x;
        mIonSlidingViewClickListener = slidingViewClickListener;
        initViews();
    }

    private void initViews() {
        title = (TextView) rootView.findViewById(R.id.item_title);
        layoutContent = (LinearLayout) rootView.findViewById(R.id.layout_content);
        image = (SimpleDraweeView) rootView.findViewById(R.id.item_image);
        desc = (TextView) rootView.findViewById(R.id.item_desc);
        count = (TextView) rootView.findViewById(R.id.item_count);
        totalSize = (TextView) rootView.findViewById(R.id.item_total_size);
        btnDelete = (RelativeLayout) rootView.findViewById(R.id.btn_delete);
        btnDelete.setOnClickListener(this);
    }

    public void bindView(DownloadResourceTableInfo info, int position){
        mPosition = position;
        title.setText(info.getTitle());
        layoutContent.getLayoutParams().width = displayWidth;
//        count.setText("12/15集");
//        totalSize.setText("共"+(302* (position+1))+"M");
        image.setImageURI(info.getImgUrl());
        layoutContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIonSlidingViewClickListener.onItemClick(v, mPosition);
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIonSlidingViewClickListener.onDeleteBtnCilck(v, mPosition);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_content:
                mIonSlidingViewClickListener.onItemClick(v, mPosition);
                break;
            case R.id.btn_delete:
                mIonSlidingViewClickListener.onDeleteBtnCilck(v, mPosition);
                break;
            default:
                break;
        }
    }
}
