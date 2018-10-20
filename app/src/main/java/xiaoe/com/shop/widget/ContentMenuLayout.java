package xiaoe.com.shop.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import xiaoe.com.shop.R;

public class ContentMenuLayout extends FrameLayout implements View.OnClickListener {
    private static final String TAG = "ContentMenuLayout";
    private View rootView;
    private LinearLayout btnShare;
    private LinearLayout btnCollect;
    private LinearLayout btnDownload;
    private LinearLayout btnComment;
    private View menuBG;
    private TextView btnCancel;

    public ContentMenuLayout(@NonNull Context context) {
        this(context,null);
    }

    public ContentMenuLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        rootView = View.inflate(context, R.layout.layout_content_menu, this);
        menuBG = rootView.findViewById(R.id.menu_bg);
        menuBG.setOnClickListener(this);
        btnShare = (LinearLayout) rootView.findViewById(R.id.btn_share);
        btnCollect = (LinearLayout) rootView.findViewById(R.id.btn_collect);
        btnDownload = (LinearLayout) rootView.findViewById(R.id.btn_download);
        btnComment = (LinearLayout) rootView.findViewById(R.id.btn_comment);
        btnCancel = (TextView) rootView.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(this);
    }

    public void setButtonClickListener(OnClickListener listener){
        if(listener == null){
            return;
        }
        btnComment.setOnClickListener(listener);
        btnDownload.setOnClickListener(listener);
        btnCollect.setOnClickListener(listener);
        btnShare.setOnClickListener(listener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.menu_bg:
            case R.id.btn_cancel:
                setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }
}
