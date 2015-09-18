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

public class ToolTip {
    private final CharSequence text;
    private final int textColor;
    private final float textSize;
    private final int backgroundColor;

    private ToolTip(CharSequence text, int textColor, float textSize, int backgroundColor) {
        this.text = text;
        this.textColor = textColor;
        this.textSize = textSize;
        this.backgroundColor = backgroundColor;
    }

    public CharSequence getText() {
        return text;
    }

    public int getTextColor() {
        return textColor;
    }

    public float getTextSize() {
        return textSize;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public static class Builder {
        private CharSequence text;
        private int textColor = Color.WHITE;
        private float textSize = 13.0F;

        private int backgroundColor = Color.BLACK;

        /**
         * Creates a new builder.
         */
        public Builder() {
        }

        /**
         * Sets the text of the tool tip.
         */
        public Builder withText(CharSequence text) {
            this.text = text;
            return this;
        }

        /**
         * Sets the text color for the tool tip. The default color is white.
         */
        public Builder withTextColor(int textColor) {
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
         * Sets the background color for the tool tip. The default color is black.
         */
        public Builder withBackgroundColor(int backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        /**
         * Creates a tool tip.
         */
        public ToolTip build() {
            return new ToolTip(text, textColor, textSize, backgroundColor);
        }
    }
}
