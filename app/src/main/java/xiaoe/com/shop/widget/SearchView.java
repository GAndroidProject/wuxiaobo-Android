package xiaoe.com.shop.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.List;

import xiaoe.com.shop.R;

public class SearchView extends LinearLayout {

    private static final String TAG = "CourseTitleView";

    private Context mContext;

    private LinearLayout mContainer;
    private ImageView mCourseSearch;

    // 轮播图链接集合
    private List<String> imageList;

    public SearchView(Context context) {
        super(context);
        mContext = context;
        initView(context);
    }

    public SearchView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(context);
    }

    public SearchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView(context);
    }

    public SearchView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        initView(context);
    }

    private void initView(Context context) {
        mContainer = (LinearLayout) View.inflate(context, R.layout.search, this);
    }

    public void setSearchIconClickListener (OnClickListener listener) {
        mCourseSearch.setOnClickListener(listener);
    }

    /**
     * 设置轮播图链接集合
     * @param imageList
     */
//    public void setImageList(List<String> imageList) {
//        this.imageList = imageList;
//        initConvenientBanner();
//    }

}
