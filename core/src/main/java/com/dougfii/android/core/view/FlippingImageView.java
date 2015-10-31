package com.dougfii.android.core.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

/**
 * Created by momo on 15/10/31.
 */
public class FlippingImageView extends ImageView {
    private RotateAnimation animation;
    private boolean hasAnimation;

    public FlippingImageView(Context context) {
        super(context);
    }

    public FlippingImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlippingImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void startAnimation() {
        if (hasAnimation) super.startAnimation(animation);
    }

    private void setRotateAnimation() {
        if (!hasAnimation && getWidth() > 0 && getVisibility() == View.VISIBLE) {
            hasAnimation = true;
            animation = new RotateAnimation(0.0F, 360.0F, getWidth() / 2.0F, getHeight() / 2.0F);
            animation.setDuration(500L);
            animation.setInterpolator(new LinearInterpolator());
            animation.setRepeatCount(-1);
            animation.setRepeatMode(Animation.RESTART);
            setAnimation(animation);
        }
    }

    private void clearRotateAnimation() {
        if (hasAnimation) {
            hasAnimation = false;
            setAnimation(null);
            animation = null;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setRotateAnimation();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        clearRotateAnimation();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w > 0) setRotateAnimation();
    }

    @Override
    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == View.INVISIBLE || visibility == View.GONE) clearRotateAnimation();
        else setRotateAnimation();
    }
}
