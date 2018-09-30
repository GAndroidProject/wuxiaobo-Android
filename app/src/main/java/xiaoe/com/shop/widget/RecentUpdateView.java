package xiaoe.com.shop.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import xiaoe.com.shop.R;

public class RecentUpdateView extends LinearLayout {

    private Context mContext;

    private LinearLayout mContainer;
    private LinearLayout mRecentUpdateSubList;

    private SimpleDraweeView mRecentUpdateAvatar;
    private TextView mRecentUpdateSubTitle;
    private TextView mRecentUpdateSubDesc;
    private Button mRecentUpdateSubBtn;

    public RecentUpdateView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    public RecentUpdateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    public RecentUpdateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    public RecentUpdateView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        initView();
    }

    private void initView() {
        mContainer = (LinearLayout) View.inflate(mContext, R.layout.recent_update, null);
        mRecentUpdateSubList = (LinearLayout) mContainer.findViewById(R.id.recent_update_sub_list);
        mRecentUpdateAvatar = (SimpleDraweeView) mContainer.findViewById(R.id.recent_update_avatar);
        mRecentUpdateSubTitle = (TextView) mContainer.findViewById(R.id.recent_update_sub_title);
        mRecentUpdateSubDesc = (TextView) mContainer.findViewById(R.id.recent_update_sub_desc);
        mRecentUpdateSubBtn = (Button) mContainer.findViewById(R.id.recent_update_sub_btn);
    }
}
