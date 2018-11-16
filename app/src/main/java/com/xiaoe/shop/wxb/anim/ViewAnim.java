package com.xiaoe.shop.wxb.anim;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;


public class ViewAnim {
	private static final String TAG = "ViewAnim";
	private View targetView;// 目标view，这里用于在没有传入toView，但需要动画结束后将某个view可见的情况
	
	private long mAnimTime = 250;// 默认
	private long mStartDelay = 0;
	//private Animator currentAnimator = null;// 当前的动画对象
	private TimeInterpolator mInterpolator = new AccelerateDecelerateInterpolator();

	public void startViewSimpleAnim(final View fromView,float startScaleX, float finaScaleX,
									float startScaleY, float finaScaleY,
									float startAlpha, float finalAlpha) {

		//设定拉伸或者旋转动画的中心位置，这里是相对于自身左上角
//		fromView.setPivotX(1f);
//		fromView.setPivotY(1f);
		
		AnimatorSet set = new AnimatorSet();
		ObjectAnimator alphaAnim = ObjectAnimator.ofFloat(fromView, "alpha", startAlpha, finalAlpha);
		ObjectAnimator scaleXAnim = ObjectAnimator.ofFloat(fromView, View.SCALE_X, startScaleX, finaScaleX);
		ObjectAnimator scaleYAnim = ObjectAnimator.ofFloat(fromView, View.SCALE_Y, startScaleY, finaScaleY);
		
		set.play(alphaAnim).with(scaleXAnim).with(scaleYAnim);
		
		set.setStartDelay(mStartDelay);
		set.setDuration(mAnimTime);
		set.setInterpolator(mInterpolator);
		set.addListener(new AnimListener(fromView,null));

		set.start();
	}
	
	/**
	 * @author:Jack Tony
	 * @tips  :动画在执行时触发的方法，在这里自己重新设定view的参数，让内部布局重新绘制
	 * @date  :2014-11-24
	 */
	private class AnimUpdateListener implements AnimatorUpdateListener{
		View mView;
		Rect mStartBounds, mFinalBounds;
		
		public AnimUpdateListener(View view, Rect startBounds, Rect finalBounds) {
			// TODO 自动生成的构造函数存根
			mView = view;
			mStartBounds = startBounds;
			mFinalBounds = finalBounds;
		}
		
		@Override
		public void onAnimationUpdate(ValueAnimator valueAnimator) {
			
			if (valueAnimator.getCurrentPlayTime() <= valueAnimator.getDuration()) {
				float fraction = valueAnimator.getAnimatedFraction();// 动画进度
				
				float scaleXDuration = mFinalBounds.width() - mStartBounds.width();
				float scaleYDuration = mFinalBounds.height() - mStartBounds.height();
				mView.getLayoutParams().width = (int)(mStartBounds.width() + (scaleXDuration * fraction));
				mView.getLayoutParams().height = (int)(mStartBounds.height() + (scaleYDuration * fraction));
			}
			mView.requestLayout();
		}
	}
	
	/**
	 * @author:Jack Tony
	 * @tips  :动画执行的监听器，结束时进行图片的隐藏操作
	 * @date  :2014-11-24
	 */
	private class AnimListener implements AnimatorListener{
		private View mFromView,mToView;
		
		public AnimListener(View fromView, View toView) {
			// TODO 自动生成的构造函数存根
			mFromView = fromView;
			mToView = toView;
		}
		
		@Override
		public void onAnimationStart(Animator animator) {
			// TODO 自动生成的方法存根
			mFromView.setVisibility(View.VISIBLE);
			Log.d(TAG, "onAnimationStart: ");
		}
		
		@Override
		public void onAnimationEnd(Animator animator) {
			Log.d(TAG, "onAnimationEnd: ");
			// 动画结束后开始动画的那个view变为不可见并且从父控件中移除，目标的view可见（如果有的话）
			mFromView.setVisibility(View.INVISIBLE);
			((ViewGroup)mFromView.getParent()).removeView(mFromView);
			
			if (mToView != null) {
				mToView.setVisibility(View.VISIBLE);
			}
			// targetView是在没有传入toView时，用来做目标view可见性改变的
			if (targetView != null) {
				targetView.setVisibility(View.VISIBLE);
			}
		}
		
		@Override
		public void onAnimationCancel(Animator animator) {
			Log.d(TAG, "onAnimationCancel: ");
			// TODO 自动生成的方法存根
		}

		@Override
		public void onAnimationRepeat(Animator animation) {
			Log.d(TAG, "onAnimationRepeat: ");
			// TODO 自动生成的方法存根
		}
		
	}
	
	/**
	 * 设置动画差值器
	 * @param interpolator
	 */
	public void setTimeInterpolator(TimeInterpolator interpolator) {
		mInterpolator = interpolator;
	}
	

	public void setDuration(long time) {
		mAnimTime = time;
	}
	
	public long getDuration() {
		return mAnimTime;
	}
	
	public void setStartDelay(long delay) {
		mStartDelay = delay;
	}
	
	public long getStartDelay() {
		return mStartDelay;
	}
	
	/**
	 * 设定目标图片，这个图片仅仅会在动画结束后可见。没有其他作用
	 * @param view
	 */
	public void setTargetView(View view) {
		targetView = view;
	}
	
}
