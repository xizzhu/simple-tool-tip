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

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.Gravity;

public class ToolTip {
    @StringRes
    private final int textResourceId;
    @Nullable
    private final CharSequence text;
    private final int textGravity;
    private final int textColor;
    private final float textSize;
    private final Typeface typeface;
    private final int typefaceStyle;
    private final int backgroundColor;
    private final int leftPadding;
    private final int rightPadding;
    private final int topPadding;
    private final int bottomPadding;
    private final float radius;

    private ToolTip(@StringRes int textResourceId, @Nullable CharSequence text, int textGravity,
                    int textColor, float textSize, Typeface typeface, int typefaceStyle,
                    int backgroundColor, int leftPadding, int rightPadding, int topPadding,
                    int bottomPadding, float radius) {
        this.textResourceId = textResourceId;
        this.text = text;
        this.textGravity = textGravity;
        this.textColor = textColor;
        this.textSize = textSize;
        this.typeface = typeface;
        this.typefaceStyle = typefaceStyle;
        this.backgroundColor = backgroundColor;
        this.leftPadding = leftPadding;
        this.rightPadding = rightPadding;
        this.topPadding = topPadding;
        this.bottomPadding = bottomPadding;
        this.radius = radius;
    }

    @StringRes
    public int getTextResourceId() {
        return textResourceId;
    }

    @Nullable
    public CharSequence getText() {
        return text;
    }

    public int getTextGravity() {
        return textGravity;
    }

    @ColorInt
    public int getTextColor() {
        return textColor;
    }

    public float getTextSize() {
        return textSize;
    }

    @NonNull
    public Typeface getTypeface() {
        return typeface;
    }

    public int getTypefaceStyle() {
        return typefaceStyle;
    }

    @ColorInt
    public int getBackgroundColor() {
        return backgroundColor;
    }

    public int getLeftPadding() {
        return leftPadding;
    }

    public int getRightPadding() {
        return rightPadding;
    }

    public int getTopPadding() {
        return topPadding;
    }

    public int getBottomPadding() {
        return bottomPadding;
    }

    public float getCornerRadius() {
        return radius;
    }

    /**
     * Used to build a tool tip.
     */
    public static class Builder {
        @StringRes
        private int textResourceId = 0;
        private CharSequence text;
        private int textGravity = Gravity.NO_GRAVITY;
        private int textColor = Color.WHITE;
        private float textSize = 13.0F;
        private Typeface typeface = Typeface.DEFAULT;
        private int typefaceStyle = Typeface.NORMAL;
        private int backgroundColor = Color.BLACK;
        private int leftPadding = 0;
        private int rightPadding = 0;
        private int topPadding = 0;
        private int bottomPadding = 0;
        private float radius = 0.0F;

        /**
         * Creates a new builder.
         */
        public Builder() {
        }

        /**
         * Sets the text of the tool tip. If both the resource ID and the char sequence are set, the
         * char sequence will be used.
         */
        public Builder withText(@StringRes int text) {
            this.textResourceId = text;
            return this;
        }

        /**
         * Sets the text of the tool tip. If both the resource ID and the char sequence are set, the
         * char sequence will be used.
         */
        public Builder withText(CharSequence text) {
            this.text = text;
            return this;
        }

        /**
         * Sets the text gravity of the tool tip. The default value is {@link Gravity.NO_GRAVITY}.
         */
        public Builder withTextGravity(int gravity) {
            this.textGravity = gravity;
            return this;
        }

        /**
         * Sets the text color for the tool tip. The default color is white.
         */
        public Builder withTextColor(@ColorInt int textColor) {
            this.textColor = textColor;
            return this;
        }

        /**
         * Sets the text size in pixel for the tool tip. The default size is 13.
         */
        public Builder withTextSize(float textSize) {
            this.textSize = textSize;
            return this;
        }

        /**
         * Sets the typeface for the tool tip. The default value is {@link Typeface.DEFAULT}.
         */
        public Builder withTypeface(Typeface typeface) {
            if (typeface != null) {
                this.typeface = typeface;
            }
            return this;
        }

        /**
         * Sets the typeface style for the tool tip. The default value is {@link Typeface.NORMAL}.
         */
        public Builder withTypefaceStyle(int style) {
            this.typefaceStyle = style;
            return this;
        }

        /**
         * Sets the background color for the tool tip. The default color is black.
         */
        public Builder withBackgroundColor(@ColorInt int backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        /**
         * Sets the padding in pixel for the tool tip. The default padding is 0.
         */
        public Builder withPadding(int leftPadding, int rightPadding, int topPadding, int bottomPadding) {
            this.leftPadding = leftPadding;
            this.rightPadding = rightPadding;
            this.topPadding = topPadding;
            this.bottomPadding = bottomPadding;
            return this;
        }

        /**
         * Sets the corner radius in pixel for the tool tip. The default value is 0.
         */
        public Builder withCornerRadius(float radius) {
            this.radius = radius;
            return this;
        }

        /**
         * Creates a tool tip.
         */
        public ToolTip build() {
            return new ToolTip(textResourceId, text, textGravity, textColor, textSize, typeface,
                    typefaceStyle, backgroundColor, leftPadding, rightPadding, topPadding,
                    bottomPadding, radius);
        }
    }
}
