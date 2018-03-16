package com.game.sdk.util;

import android.animation.ValueAnimator;
import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;

/**
 * Created by zhanglei on 2017/11/20.
 */
public class AnimationUtils {
    public static ValueAnimator runToRight(final View view,final float rotationCircleNum) {
        ValueAnimator anim = ValueAnimator.ofFloat(0, rotationCircleNum).setDuration(300);
        final int rightDistance =view.getWidth() / 2;
        anim.setInterpolator(new BounceInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float value = (float) animation.getAnimatedValue();
                view.setTranslationX(rightDistance * (value / rotationCircleNum));
                view.setRotation(value * 360);
                if (value == 0) {
                    view.setEnabled(false);
                } else if (value == rotationCircleNum) {
                    view.setEnabled(true);
                }
            }
        });
        return anim;
    }

    public static ValueAnimator runToLeft(final View writeComment, final float rotationCircleNum) {
        ValueAnimator anim = ValueAnimator.ofFloat(0, rotationCircleNum).setDuration(300);
        final int rightDistance = writeComment.getLeft() + writeComment.getWidth() / 2;
        anim.setInterpolator(new BounceInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float value = (float) animation.getAnimatedValue();
                writeComment.setTranslationX(-rightDistance * (value / rotationCircleNum));
                writeComment.setRotation(value * 360);
                if (value == 0) {
                    writeComment.setEnabled(false);
                } else if (value == rotationCircleNum) {
                    writeComment.setEnabled(true);
                }
            }
        });
        return anim;
    }

    public static ValueAnimator runBackFromBeside(final View view, final float rotationCircleNum) {
        ValueAnimator anim = ValueAnimator.ofFloat(0, rotationCircleNum).setDuration(300);
        final int distance = (DisplayUtils.getScreenWidthInPx(view.getContext())
                - view.getRight())
                + view.getWidth() / 2;
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final float value = rotationCircleNum - (float) animation.getAnimatedValue();
                view.setTranslationX(distance * (value / rotationCircleNum));
                view.setRotation(value * 360);
                if (value == 0) {
                    view.setEnabled(true);
                } else if (value == rotationCircleNum) {
                    view.setEnabled(false);
                }
            }
        });
        return anim;
    }
}
