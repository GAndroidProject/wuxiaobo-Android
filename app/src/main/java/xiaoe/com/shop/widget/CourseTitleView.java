package xiaoe.com.shop.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import xiaoe.com.shop.R;
import xiaoe.com.shop.adapter.decorate.PicViewHolder;

public class CourseTitleView extends LinearLayout {

    private static final String TAG = "CourseTitleView";

    private Context mContext;

    private LinearLayout mContainer;
    private ConvenientBanner mConvenientBanner;
    private ImageView mCourseSearch;

    // 轮播图链接集合
    private List<String> imageList;

    public CourseTitleView(Context context) {
        super(context);
        mContext = context;
        initView(context);
    }

    public CourseTitleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(context);
    }

    public CourseTitleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView(context);
    }

    public CourseTitleView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
        initView(context);
    }

    private void initView(Context context) {
        mContainer = (LinearLayout) View.inflate(context, R.layout.course_title, this);
        mConvenientBanner = (ConvenientBanner) mContainer.findViewById(R.id.convenientBanner);
        mCourseSearch = (ImageView) mContainer.findViewById(R.id.course_title_search);
        if (imageList == null) {
            mConvenientBanner.setVisibility(GONE);
        }
    }

    public void setSearchIconClickListener (OnClickListener listener) {
        mCourseSearch.setOnClickListener(listener);
    }

    /**
     * 设置轮播图链接集合
     * @param imageList
     */
    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
        initConvenientBanner();
    }

    private void initConvenientBanner() {
        if (mConvenientBanner != null) {
            if (imageList != null) {
                mConvenientBanner.setVisibility(View.VISIBLE);
                mConvenientBanner.setPages(new CBViewHolderCreator() {
                    @Override
                    public Holder createHolder(View itemView) {
                        return new PicViewHolder(itemView);
                    }

                    @Override
                    public int getLayoutId() {
                        return R.layout.sd_layout;
                    }
                }, imageList);
            } else {
                mConvenientBanner.setVisibility(View.GONE);
            }
        }
    }

    /**
     * onPause 时停止轮播
     * onResume 时开始轮播
     * 开始轮播
     */
    public void startTurning(long time) {
        if (mConvenientBanner != null) {
            mConvenientBanner.startTurning(time);
        }
    }

    /**
     * onPause 时停止轮播
     * onResume 时开始轮播
     * 停止轮播
     */
    public void stopTurning() {
        if (mConvenientBanner != null) {
            mConvenientBanner.stopTurning();
        }
    }
}
