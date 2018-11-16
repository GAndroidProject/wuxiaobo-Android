package com.xiaoe.shop.wxb.anim;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by Administrator on 2017/8/10.
 */

public class TranslationAnimator {
    /**
     * 平移动画
     */
    private static final String TAG = "TranslationAnimator";
    private boolean mRemoveFinish = true;
    private boolean mRmove = false;
    private boolean mBreakFinish = true;
    private boolean mBreak = true;
    private static TranslationAnimator ta;
    private ObjectAnimator animator;
    private View mAnimatorView;

    private TranslationAnimator() {
    }

    public static TranslationAnimator getInstance() {
        if (ta == null) {
            ta = new TranslationAnimator();
        }
        return ta;
    }
    public TranslationAnimator setAnimator(View av){
        mAnimatorView = av;
        return ta;
    }
    public void remove(int to){
        if(mRmove || !mBreakFinish){
            return;
        }
        Log.d(TAG, "remove: ");
        mBreak =false;
        mRmove = true;
        mRemoveFinish = false;
        animator = ObjectAnimator.ofFloat(mAnimatorView,"translationY",0,to);
        animator.setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if(animation.getAnimatedFraction() == 1){
                    mRemoveFinish = true;
                }
            }
        });
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
    }
    public void brak(int from){
        if(mBreak || !mRemoveFinish){
            return;
        }
        Log.d(TAG, "brak: ");
        mRmove = false;
        mBreak = true;
        mBreakFinish = false;
        animator = ObjectAnimator.ofFloat(mAnimatorView,"translationY",from,0);
        animator.setDuration(500);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if(animation.getAnimatedFraction() == 1){
                    mBreakFinish = true;
                }
            }
        });
        animator.start();
    }
    public void cancel(){
        if(animator != null){
            animator.cancel();
        }
        animator = null;
    }
}
