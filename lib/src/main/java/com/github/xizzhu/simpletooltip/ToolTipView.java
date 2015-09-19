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
import android.support.annotation.UiThread;
import android.util.TypedValue;
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

import java.lang.ref.WeakReference;

@SuppressLint("ViewConstructor")
public class ToolTipView extends LinearLayout implements ViewTreeObserver.OnPreDrawListener,
        View.OnClickListener {
    public interface OnToolTipClickedListener {
        void onToolTipClicked(ToolTipView toolTipView);
    }

    private static final long ANIMATION_DURATION = 300L;

    private final View anchorView;
    private final ImageView arrowUp;
    private final ImageView arrowDown;
    private WeakReference<OnToolTipClickedListener> listener;
    private float pivotX;
    private float pivotY;

    private ToolTipView(Context context, View anchorView, ToolTip toolTip) {
        super(context);

        this.anchorView = anchorView;

        setOrientation(VERTICAL);
        inflate(context, R.layout.tool_tip, this);
        setOnClickListener(this);

        int backgroundColor = toolTip.getBackgroundColor();

        TextView text = (TextView) findViewById(R.id.text);
        text.setPadding(toolTip.getLeftPadding(), toolTip.getTopPadding(),
                toolTip.getRightPadding(), toolTip.getBottomPadding());
        text.setText(toolTip.getText());
        text.setTextColor(toolTip.getTextColor());
        text.setTextSize(TypedValue.COMPLEX_UNIT_PX, toolTip.getTextSize());

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

        arrowUp = (ImageView) findViewById(R.id.arrow_up);
        arrowDown = (ImageView) findViewById(R.id.arrow_down);

        PorterDuffColorFilter colorFilter
                = new PorterDuffColorFilter(backgroundColor, PorterDuff.Mode.MULTIPLY);
        arrowUp.setColorFilter(colorFilter);
        arrowDown.setColorFilter(colorFilter);
    }

    /**
     * Sets a listener that will be called when the tool tip view is clicked. Note that the view only
     * keeps a weak reference to it.
     */
    public void setOnToolTipClickedListener(OnToolTipClickedListener listener) {
        if (listener == null) {
            this.listener = null;
        } else {
            this.listener = new WeakReference<>(listener);
        }
    }

    /**
     * Shows the tool tip.
     */
    @UiThread
    public void show() {
        ViewGroup parentOfAnchorView = (ViewGroup) anchorView.getParent();

        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        parentOfAnchorView.addView(this, layoutParams);

        getViewTreeObserver().addOnPreDrawListener(this);
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
        int parentWidth = parent.getWidth();

        int anchorTop = anchorView.getTop();
        int anchorLeft = anchorView.getLeft();
        int anchorWidth = anchorView.getWidth();
        int anchorHeight = anchorView.getHeight();

        int width = getWidth();
        int height = getHeight();

        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) getLayoutParams();

        // if the space below the anchor view is not enough, we show the tool tip above the anchor view
        // otherwise, show it below the anchor view
        boolean showAboveAnchor = parent.getHeight() < anchorTop + anchorHeight + height;
        if (showAboveAnchor) {
            layoutParams.topMargin = anchorTop - height + arrowDown.getHeight();
            arrowUp.setVisibility(View.GONE);
            arrowDown.setVisibility(View.VISIBLE);
        } else {
            layoutParams.topMargin = anchorTop + anchorHeight;
            arrowDown.setVisibility(View.GONE);
        }

        // we try to align the horizontal center of the anchor view and the tool tip
        int anchorHorizontalCenter = anchorLeft + anchorWidth / 2;
        int left = anchorHorizontalCenter - width / 2;
        int right = left + width;
        int leftMargin = Math.max(0, right > parentWidth ? parentWidth - width : left);
        layoutParams.leftMargin = leftMargin;

        setLayoutParams(layoutParams);

        ImageView arrow = showAboveAnchor ? arrowDown : arrowUp;
        layoutParams = (ViewGroup.MarginLayoutParams) arrow.getLayoutParams();
        layoutParams.leftMargin = anchorHorizontalCenter - leftMargin - arrow.getWidth() / 2;
        arrow.setLayoutParams(layoutParams);

        pivotX = anchorHorizontalCenter - leftMargin;
        pivotY = showAboveAnchor ? height - arrow.getHeight() : 0.0F;
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
        OnToolTipClickedListener listener = this.listener != null ? this.listener.get() : null;
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
        private ToolTip toolTip;

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
         * Sets the tool tip that will be shown.
         */
        public Builder withToolTip(ToolTip toolTip) {
            this.toolTip = toolTip;
            return this;
        }

        /**
         * Creates a tool tip view.
         */
        @UiThread
        public ToolTipView build() {
            return new ToolTipView(context, anchorView, toolTip);
        }
    }
}
