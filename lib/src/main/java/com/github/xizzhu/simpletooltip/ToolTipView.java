/*
 * Copyright (C) 2015 Xizhi Zhu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.xizzhu.simpletooltip;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("ViewConstructor")
public class ToolTipView extends LinearLayout implements ViewTreeObserver.OnPreDrawListener,
        View.OnClickListener {
    public interface OnToolTipClickedListener {
        void onToolTipClicked(ToolTipView toolTipView);
    }

    private static final int GRAVITY_START = 0x00800003;
    private static final int GRAVITY_END = 0x00800005;

    private static final long ANIMATION_DURATION = 300L;

    private final View anchorView;
    private final ViewGroup parentView;
    private final TextView text;
    private final ImageView arrow;
    private final int gravity;

    private OnToolTipClickedListener listener;
    private float pivotX;
    private float pivotY;

    private ToolTipView(Context context, View anchorView, @Nullable ViewGroup parentView,
                        int gravity, ToolTip toolTip) {
        super(context);

        this.anchorView = anchorView;
        this.parentView = parentView != null ? parentView : (ViewGroup) anchorView.getParent();
        this.gravity = gravity;

        setOnClickListener(this);

        int backgroundColor = toolTip.getBackgroundColor();

        text = new TextView(context);
        text.setPadding(toolTip.getLeftPadding(), toolTip.getTopPadding(),
                toolTip.getRightPadding(), toolTip.getBottomPadding());
        text.setGravity(toolTip.getTextGravity());
        text.setTextColor(toolTip.getTextColor());
        text.setTextSize(TypedValue.COMPLEX_UNIT_PX, toolTip.getTextSize());
        text.setTypeface(toolTip.getTypeface(), toolTip.getTypefaceStyle());

        CharSequence txt = toolTip.getText();
        if (TextUtils.isEmpty(txt)) {
            text.setText(toolTip.getTextResourceId());
        } else {
            text.setText(txt);
        }

        float radius = toolTip.getCornerRadius();
        if (radius > 0.0F) {
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(backgroundColor);
            drawable.setGradientType(GradientDrawable.RECTANGLE);
            drawable.setCornerRadius(radius);

            //noinspection deprecation
            text.setBackgroundDrawable(drawable);
        } else {
            text.setBackgroundColor(backgroundColor);
        }

        arrow = new ImageView(context);
        arrow.setColorFilter(new PorterDuffColorFilter(backgroundColor, PorterDuff.Mode.MULTIPLY));

        // TODO supports Gravity.NO_GRAVITY
        switch (gravity) {
            case Gravity.LEFT:
                setOrientation(HORIZONTAL);
                addView(text, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                arrow.setImageResource(R.drawable.ic_arrow_right);
                addView(arrow, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                break;
            case Gravity.RIGHT:
                setOrientation(HORIZONTAL);
                arrow.setImageResource(R.drawable.ic_arrow_left);
                addView(arrow, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                addView(text, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                break;
            case Gravity.TOP:
                setOrientation(VERTICAL);
                addView(text, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                arrow.setImageResource(R.drawable.ic_arrow_down);
                addView(arrow, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                break;
            case Gravity.BOTTOM:
                setOrientation(VERTICAL);
                arrow.setImageResource(R.drawable.ic_arrow_up);
                addView(arrow, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                addView(text, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                break;
        }
    }

    /**
     * Sets a listener that will be called when the tool tip view is clicked.
     */
    public void setOnToolTipClickedListener(OnToolTipClickedListener listener) {
        this.listener = listener;
    }

    /**
     * Shows the tool tip.
     */
    @UiThread
    public void show() {
        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        parentView.addView(this, layoutParams);

        getViewTreeObserver().addOnPreDrawListener(this);
    }

    /**
     * Shows the tool tip with the specified delay.
     */
    public void showDelayed(long milliSeconds) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                show();
            }
        }, milliSeconds);
    }

    /**
     * Removes the tool tip view from the view hierarchy.
     */
    @UiThread
    public void remove() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            setPivotX(pivotX);
            setPivotY(pivotY);
            animate().setDuration(ANIMATION_DURATION).alpha(0.0F).scaleX(0.0F).scaleY(0.0F)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            removeFromParent();
                        }
                    });
        } else {
            AnimationSet animationSet = new AnimationSet(true);
            animationSet.setDuration(ANIMATION_DURATION);
            animationSet.addAnimation(new AlphaAnimation(1.0F, 0.0F));
            animationSet.addAnimation(new ScaleAnimation(1.0F, 0.0F, 1.0F, 0.0F, pivotX, pivotY));
            animationSet.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    // do nothing
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    removeFromParent();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    // do nothing
                }
            });
            startAnimation(animationSet);
        }
    }

    private void removeFromParent() {
        ViewParent parent = getParent();
        if (parent != null) {
            ((ViewGroup) parent).removeView(this);
        }
    }

    @Override
    public boolean onPreDraw() {
        getViewTreeObserver().removeOnPreDrawListener(this);

        View parent = (View) getParent();
        if (parent == null) {
            return false;
        }
        int parentWidth = parent.getWidth();
        int parentHeight = parent.getHeight();

        int anchorTop = anchorView.getTop();
        int anchorLeft = anchorView.getLeft();
        int anchorWidth = anchorView.getWidth();
        int anchorHeight = anchorView.getHeight();

        int width = getWidth();
        int height = getHeight();

        // TODO supports Gravity.NO_GRAVITY
        if (gravity == Gravity.TOP || gravity == Gravity.BOTTOM) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) getLayoutParams();
            if (gravity == Gravity.TOP) {
                layoutParams.topMargin = anchorTop - height;
            } else {
                // gravity == Gravity.BOTTOM
                layoutParams.topMargin = anchorTop + anchorHeight;
            }

            // align the horizontal center of the anchor view and the tool tip
            int anchorHorizontalCenter = anchorLeft + anchorWidth / 2;
            int left = anchorHorizontalCenter - width / 2;
            int right = left + width;
            int leftMargin = Math.max(0, right > parentWidth ? parentWidth - width : left);
            layoutParams.leftMargin = leftMargin;

            setLayoutParams(layoutParams);

            // align the arrow to the horizontal center of the anchor view
            layoutParams = (ViewGroup.MarginLayoutParams) arrow.getLayoutParams();
            layoutParams.leftMargin = anchorHorizontalCenter - leftMargin - arrow.getWidth() / 2;
            arrow.setLayoutParams(layoutParams);

            pivotX = anchorHorizontalCenter - leftMargin;
            pivotY = gravity == Gravity.TOP ? height - arrow.getHeight() : 0.0F;
        } else {
            // gravity == Gravity.LEFT || gravity == Gravity.RIGHT
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) getLayoutParams();
            if (gravity == Gravity.LEFT) {
                layoutParams.rightMargin = parentWidth - anchorLeft;
                layoutParams.leftMargin = Math.max(0, anchorLeft - width);
                text.setMaxWidth(anchorLeft - layoutParams.leftMargin - arrow.getWidth());
            } else {
                // gravity == Gravity.RIGHT
                layoutParams.leftMargin = anchorLeft + anchorWidth;
                text.setMaxWidth(parentWidth - layoutParams.leftMargin - arrow.getWidth());
            }

            // align the vertical center of the anchor view and the tool tip
            int anchorVerticalCenter = anchorTop + anchorHeight / 2;
            int top = anchorVerticalCenter - height / 2;
            int bottom = top + height;
            int topMargin = Math.max(0, bottom > parentHeight ? parentHeight - height : top);
            layoutParams.topMargin = topMargin;

            setLayoutParams(layoutParams);

            // align the arrow to the vertical center of the anchor view
            layoutParams = (ViewGroup.MarginLayoutParams) arrow.getLayoutParams();
            layoutParams.topMargin = anchorVerticalCenter - topMargin - arrow.getHeight() / 2;
            arrow.setLayoutParams(layoutParams);

            pivotX = gravity == Gravity.LEFT ? width - arrow.getWidth() : 0.0F;
            pivotY = anchorVerticalCenter - topMargin;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            setAlpha(0.0F);
            setPivotX(pivotX);
            setPivotY(pivotY);
            setScaleX(0.0F);
            setScaleY(0.0F);
            animate().setDuration(ANIMATION_DURATION).alpha(1.0F).scaleX(1.0F).scaleY(1.0F);
        } else {
            AnimationSet animationSet = new AnimationSet(true);
            animationSet.setDuration(ANIMATION_DURATION);
            animationSet.addAnimation(new AlphaAnimation(0.0F, 1.0F));
            animationSet.addAnimation(new ScaleAnimation(0.0F, 1.0F, 0.0F, 1.0F, pivotX, pivotY));
            startAnimation(animationSet);
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            listener.onToolTipClicked(this);
        }

        remove();
    }

    /**
     * Used to build a tool tip view.
     */
    public static class Builder {
        private final Context context;
        private View anchorView;
        private ViewGroup parentView;
        private ToolTip toolTip;
        private int gravity = Gravity.BOTTOM;

        /**
         * Creates a new builder.
         */
        public Builder(Context context) {
            this.context = context;
        }

        /**
         * Sets the view that the tool tip view will try to anchor.
         */
        public Builder withAnchor(View anchorView) {
            this.anchorView = anchorView;
            return this;
        }

        /**
         * Sets the parent view for the tool tip view. Otherwise, the tool tip view will added to
         * the parent view of the anchor view.
         * <p/>
         * This is useful when the anchor view is a sub view of e.g. a LinearLayout. Note that it
         * assumes the parent view has the same size as the parent view of the anchor view.
         */
        public Builder withParent(ViewGroup parentView) {
            this.parentView = parentView;
            return this;
        }

        /**
         * Sets the tool tip that will be shown.
         */
        public Builder withToolTip(ToolTip toolTip) {
            this.toolTip = toolTip;
            return this;
        }

        /**
         * Sets the tool tip gravity. By default, it will be anchored to bottom of the anchor view.
         * <p/>
         * Only the following are supported: Gravity.TOP, Gravity.BOTTOM, Gravity.LEFT, Gravity.RIGHT,
         * Gravity.START, and Gravity.END.
         */
        public Builder withGravity(int gravity) {
            this.gravity = gravity;
            return this;
        }

        /**
         * Creates a tool tip view.
         */
        @UiThread
        public ToolTipView build() {
            if (gravity == GRAVITY_START || gravity == GRAVITY_END) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1
                        && anchorView.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
                    gravity = gravity == GRAVITY_START ? Gravity.RIGHT : Gravity.LEFT;
                } else {
                    gravity &= Gravity.HORIZONTAL_GRAVITY_MASK;
                }
            }
            if (gravity != Gravity.TOP && gravity != Gravity.BOTTOM
                    && gravity != Gravity.LEFT && gravity != Gravity.RIGHT) {
                throw new IllegalArgumentException("Unsupported gravity - " + gravity);
            }

            return new ToolTipView(context, anchorView, parentView, gravity, toolTip);
        }
    }
}
