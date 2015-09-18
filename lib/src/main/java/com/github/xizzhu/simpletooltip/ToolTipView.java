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
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ToolTipView extends LinearLayout implements ViewTreeObserver.OnPreDrawListener {
    private final View anchorView;

    private ToolTipView(Context context, View anchorView, ToolTip toolTip) {
        super(context);

        this.anchorView = anchorView;

        setOrientation(VERTICAL);
        inflate(context, R.layout.tool_tip, this);

        TextView text = (TextView) findViewById(R.id.text);
        text.setText(toolTip.getText());
        text.setTextColor(toolTip.getTextColor());
        text.setTextSize(TypedValue.COMPLEX_UNIT_PX, toolTip.getTextSize());
    }

    public void show() {
        ViewGroup parentOfAnchorView = (ViewGroup) anchorView.getParent();

        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        parentOfAnchorView.addView(this, layoutParams);

        getViewTreeObserver().addOnPreDrawListener(this);
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
        if (parent.getHeight() < anchorTop + anchorHeight + height) {
            layoutParams.topMargin = anchorTop - height;
        } else {
            layoutParams.topMargin = anchorTop + anchorHeight;
        }

        // we try to align the horizontal center of the anchor view and the tool tip
        int anchorHorizontalCenter = anchorLeft + anchorWidth / 2;
        int left = anchorHorizontalCenter - width / 2;
        int right = left + width;
        layoutParams.leftMargin = Math.max(0, right > parentWidth ? parentWidth - width : left);

        setLayoutParams(layoutParams);

        return false;
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
