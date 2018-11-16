package com.xiaoe.shop.wxb.widget;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.xiaoe.shop.wxb.R;

/**
 * @author Administrator
 * @date 2018/5/11
 */
public class EmptyView extends FrameLayout {

    public static final int STATUS_LOADING = 0;
    public static final int STATUS_NO_DATA = 1;
    public static final int STATUS_NETWORK_ERROR = 2;

    @IntDef({STATUS_LOADING, STATUS_NO_DATA, STATUS_NETWORK_ERROR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EmptyStatus {

    }

    public interface EmptyViewDelegate {
        void onReload();
    }

    public void setEmptyViewDelegate(EmptyViewDelegate emptyViewDelegate) {
        this.mEmptyViewDelegate = emptyViewDelegate;
    }

    Context mContext;
    View mNoDataView;
    View mLoadingView;
    View mNetworkErrorView;
    TextView mTvNoDataTip;

    @EmptyStatus
    int mStatus = STATUS_LOADING;
    EmptyViewDelegate mEmptyViewDelegate;

    public EmptyView(@NonNull Context context) {
        this(context, null);
    }

    public EmptyView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmptyView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        View rootView = View.inflate(context, R.layout.empty_view, this);
        mNoDataView = rootView.findViewById(R.id.viewNoData);
        mLoadingView = rootView.findViewById(R.id.viewLoading);
        mNetworkErrorView = rootView.findViewById(R.id.viewNetworkError);
        mTvNoDataTip = (TextView) rootView.findViewById(R.id.tvNoDataTip);
        rootView.findViewById(R.id.tvReload).setOnClickListener(v -> {
            if (mEmptyViewDelegate != null) {
                mEmptyViewDelegate.onReload();
            }
        });
    }

    public void setStatus(@EmptyStatus int status) {
        if (status == mStatus)
            return;
        mStatus = status;
        mLoadingView.setVisibility(status == STATUS_LOADING ? VISIBLE : GONE);
        mNoDataView.setVisibility(status == STATUS_NO_DATA ? VISIBLE : GONE);
        mNetworkErrorView.setVisibility(status == STATUS_NETWORK_ERROR ? VISIBLE : GONE);
    }

    public void setNoDateTip(String tip) {
        mTvNoDataTip.setText(tip);
    }

    public void setNoDataTip(@StringRes int resid) {
        mTvNoDataTip.setText(resid);
    }

    public void setNoDataTipNew(String tip, int color) {
        mNoDataView.findViewById(R.id.ivNoDataBg).setVisibility(GONE);
        mNoDataView.findViewById(R.id.tvNoDataTip).setVisibility(GONE);

        TextView textView = (TextView) mNoDataView.findViewById(R.id.tvNoDataTipNew);
        textView.setVisibility(VISIBLE);
        textView.setText(tip);
        textView.setTextColor(color);
    }
}
