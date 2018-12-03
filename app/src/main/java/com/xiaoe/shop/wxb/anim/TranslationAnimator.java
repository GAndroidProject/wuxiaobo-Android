package com.xiaoe.shop.wxb.anim;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
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
//    private TranslationAnimator ta;
    private ObjectAnimator animator;
    private View mAnimatorView;
    private boolean show = true;

    public TranslationAnimator() {
    }


    public TranslationAnimator setAnimator(View av){
        mAnimatorView = av;
        return this;
    }
    public void remove(int to){
        if(mRmove || !mBreakFinish){
            return;
        }
        mBreak =false;
        mRmove = true;
        show = false;
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
        show = true;
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

    public void initState(){
        if(show){
            mBreak = false;
        }else{
            mRmove = false;
        }
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }
}
