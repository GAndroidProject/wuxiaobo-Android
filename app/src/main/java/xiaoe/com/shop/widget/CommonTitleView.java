package xiaoe.com.shop.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import xiaoe.com.shop.R;

public class CommonTitleView extends Toolbar {

    private Context mContext;

    private ImageView titleBack;
    private TextView titleContent;
//    private ImageView titleCollect;
//    private ImageView titleShare;
    private TextView titleEnd;

    public CommonTitleView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public CommonTitleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CommonTitleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init (Context context) {
        mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.common_title_view, this);
        titleBack = (ImageView) view.findViewById(R.id.title_back);
        titleContent = (TextView) view.findViewById(R.id.title_content);
//        titleCollect = (ImageView) view.findViewById(R.id.title_collect);
//        titleShare = (ImageView) view.findViewById(R.id.title_share);
        titleEnd = (TextView) view.findViewById(R.id.title_end);
    }

    public void setTitleBackClickListener(OnClickListener listener) {
        titleBack.setOnClickListener(listener);
    }

    public void setTitleContentText(String content) {
        titleContent.setText(content);
    }

//    public void setTitleCollectClickListener(OnClickListener listener) {
//        titleCollect.setOnClickListener(listener);
//    }
//
//    public void setTitleShareClickListener(OnClickListener listener) {
//        titleShare.setOnClickListener(listener);
//    }

    public void setTitleBackVisibility(int visibility) {
        titleBack.setVisibility(visibility);
    }

//    public void setTitleContentVisibility(int visibility) {
//        titleShare.setVisibility(visibility);
//    }
//
//    public void setTitleCollectVisibility(int visibility) {
//        titleCollect.setVisibility(visibility);
//    }

//    public void setTitleShareVisibility(int visibility) {
//        titleShare.setVisibility(visibility);
//    }
//
//    public void setTitleCollectDrawable(int drawable) {
//        titleCollect.setImageDrawable(mContext.getResources().getDrawable(drawable));
//    }

    public void setTitleEndText(String content) {
        titleEnd.setText(content);
    }

    public void setTitleEndVisibility(int visibility) {
        titleEnd.setVisibility(visibility);
    }

    public void setTitleEndClickListener(OnClickListener listener) {
        titleEnd.setOnClickListener(listener);
    }
}
