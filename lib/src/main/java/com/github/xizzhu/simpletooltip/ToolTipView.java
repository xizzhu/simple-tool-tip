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

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class ToolTipView extends LinearLayout implements ViewTreeObserver.OnPreDrawListener,
        View.OnClickListener {
    public interface OnToolTipClickedListener {
        void onToolTipClicked(ToolTipView toolTipView);
    }

    private final View anchorView;
    private final ImageView arrowUp;
    private final ImageView arrowDown;
    private WeakReference<OnToolTipClickedListener> listener;

    private ToolTipView(Context context, View anchorView, ToolTip toolTip) {
        super(context);

        this.anchorView = anchorView;

        setOrientation(VERTICAL);
        inflate(context, R.layout.tool_tip, this);
        setOnClickListener(this);

        TextView text = (TextView) findViewById(R.id.text);
        text.setPadding(toolTip.getLeftPadding(), toolTip.getTopPadding(),
                toolTip.getRightPadding(), toolTip.getBottomPadding());
        text.setText(toolTip.getText());
        text.setTextColor(toolTip.getTextColor());
        text.setTextSize(TypedValue.COMPLEX_UNIT_PX, toolTip.getTextSize());

        arrowUp = (ImageView) findViewById(R.id.arrow_up);
        arrowDown = (ImageView) findViewById(R.id.arrow_down);

        int backgroundColor = toolTip.getBackgroundColor();
        text.setBackgroundColor(backgroundColor);
        arrowUp.setColorFilter(backgroundColor, PorterDuff.Mode.MULTIPLY);
        arrowDown.setColorFilter(backgroundColor, PorterDuff.Mode.MULTIPLY);
    }

    public void setOnToolTipClickedListener(OnToolTipClickedListener listener) {
        if (listener == null) {
            this.listener = null;
        } else {
            this.listener = new WeakReference<>(listener);
        }
    }

    public void show() {
        ViewGroup parentOfAnchorView = (ViewGroup) anchorView.getParent();

        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        parentOfAnchorView.addView(this, layoutParams);

        getViewTreeObserver().addOnPreDrawListener(this);
    }

    public void remove() {
        ((ViewGroup) getParent()).removeView(this);
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
        ImageView arrow;
        if (parent.getHeight() < anchorTop + anchorHeight + height) {
            layoutParams.topMargin = anchorTop - height + arrowDown.getHeight();
            arrowUp.setVisibility(View.GONE);
            arrowDown.setVisibility(View.VISIBLE);
            arrow = arrowDown;
        } else {
            layoutParams.topMargin = anchorTop + anchorHeight;
            arrowDown.setVisibility(View.GONE);
            arrow = arrowUp;
        }

        // we try to align the horizontal center of the anchor view and the tool tip
        int anchorHorizontalCenter = anchorLeft + anchorWidth / 2;
        int left = anchorHorizontalCenter - width / 2;
        int right = left + width;
        int leftMargin = Math.max(0, right > parentWidth ? parentWidth - width : left);
        layoutParams.leftMargin = leftMargin;

        setLayoutParams(layoutParams);

        layoutParams = (ViewGroup.MarginLayoutParams) arrow.getLayoutParams();
        layoutParams.leftMargin = anchorHorizontalCenter - leftMargin - arrow.getWidth() / 2;
        arrow.setLayoutParams(layoutParams);

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

    public static class Builder {
        private final Context context;
        private View anchorView;
        private ToolTip toolTip;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder withAnchor(View anchorView) {
            this.anchorView = anchorView;
            return this;
        }

        public Builder withToolTip(ToolTip toolTip) {
            this.toolTip = toolTip;
            return this;
        }

        public ToolTipView build() {
            return new ToolTipView(context, anchorView, toolTip);
        }
    }
}
