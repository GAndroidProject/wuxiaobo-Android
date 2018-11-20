package com.xiaoe.common.widget;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshKernel;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.RefreshState;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.xiaoe.common.R;
import com.xiaoe.common.utils.Dp2Px2SpUtil;

public class CommonRefreshFooter extends LinearLayout implements RefreshFooter {

    TextView refreshContent;
    SimpleDraweeView refreshImg;

    public CommonRefreshFooter(Context context) {
        super(context);
        initView(context);
    }

    public CommonRefreshFooter(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public CommonRefreshFooter(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    /**
     * 获取真实视图（必须返回，不能为null）
     */
    @NonNull
    @Override
    public View getView() {
        return this;
    }

    /**
     * 获取变换方式（必须指定一个：平移、拉伸、固定、全屏）
     */
    @NonNull
    @Override
    public SpinnerStyle getSpinnerStyle() {
        // 指定变化方式为平移
        return SpinnerStyle.Translate;
    }

    /**
     * 设置主题颜色 （如果自定义的Header没有注意颜色，本方法可以什么都不处理）
     * @param colors 对应Xml中配置的 srlPrimaryColor srlAccentColor
     */
    @Override
    public void setPrimaryColors(int... colors) {

    }

    /**
     * 尺寸定义初始化完成 （如果高度不改变（代码修改：setHeader），只调用一次, 在RefreshLayout#onMeasure中调用）
     * @param kernel RefreshKernel 核心接口（用于完成高级Header功能）
     * @param height HeaderHeight or FooterHeight
     * @param maxDragHeight 最大拖动高度
     */
    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight) {

    }

    /**
     * 开始动画（开始刷新或者开始加载动画）
     * @param refreshLayout RefreshLayout
     * @param height HeaderHeight or FooterHeight
     * @param maxDragHeight 最大拖动高度
     */
    @Override
    public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {

    }

    /**
     * 动画结束
     * @param refreshLayout RefreshLayout
     * @param success 数据是否成功刷新或加载
     * @return 完成动画所需时间 如果返回 Integer.MAX_VALUE 将取消本次完成事件，继续保持原有状态
     */
    @Override
    public int onFinish(@NonNull RefreshLayout refreshLayout, boolean success) {
        return 0;
    }

    // 状态变化
    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState, @NonNull RefreshState newState) {
        switch (newState) {
            case None:
            case PullUpToLoad: // 上拉开始刷新
                refreshContent.setText("上拉开始刷新");
                break;
            case Loading: // 正在刷新
                refreshContent.setText("加载中...");
                break;
            case ReleaseToLoad: // 释放立即刷新
                refreshContent.setText("释放立即刷新");
                break;
        }
    }


    @Override
    public void onMoving(boolean b, float v, int i, int i1, int i2) {

    }

    @Override
    public void onReleased(@NonNull RefreshLayout refreshLayout, int i, int i1) {

    }

    @Override
    public void onHorizontalDrag(float v, int i, int i1) {

    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }

    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.common_refresh_layout, this);
        refreshContent = (TextView) view.findViewById(R.id.refresh_content);
        refreshImg = (SimpleDraweeView) view.findViewById(R.id.refresh_img);

        String url = "res:///" + R.drawable.loading;

        Uri uri = Uri.parse(url);

        ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                .setResizeOptions(new ResizeOptions(Dp2Px2SpUtil.dp2px(context, 14), Dp2Px2SpUtil.dp2px(context, 14)))
                .build();

        DraweeController draweeController = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setUri(uri)
                .setAutoPlayAnimations(true)
                .build();

        refreshImg.setController(draweeController);
    }

    @Override
    public boolean setNoMoreData(boolean noMoreData) {
        return noMoreData;
    }
}
