/*
 * Copyright (C) 2019 Xizhi Zhu
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

package com.github.xizzhu.simpletooltip.sample;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.github.xizzhu.simpletooltip.ToolTip;
import com.github.xizzhu.simpletooltip.ToolTipView;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        findViewById(R.id.top_left_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToolTipView(v, Gravity.RIGHT, "Simple tool tip!",
                        ContextCompat.getColor(MainActivity.this, R.color.blue));
            }
        });
        findViewById(R.id.top_right_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToolTipView(v, Gravity.BOTTOM, "It is yet another very simple tool tip!",
                        ContextCompat.getColor(MainActivity.this, R.color.green));
            }
        });
        findViewById(R.id.central_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToolTipView(v, Gravity.END, "It is a very simple tool tip in the center!",
                        ContextCompat.getColor(MainActivity.this, R.color.magenta));
            }
        });
        findViewById(R.id.bottom_left_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToolTipView(v, Gravity.TOP, "Tool tip, once more!",
                        ContextCompat.getColor(MainActivity.this, R.color.maroon));
            }
        });
        findViewById(R.id.bottom_right_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToolTipView(v, Gravity.LEFT, "Magical tool tip!",
                        ContextCompat.getColor(MainActivity.this, R.color.navy));
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.inflateMenu(R.menu.menu_main);
        View menuItem = findViewById(R.id.menu_item);
        showToolTipView(menuItem, Gravity.BOTTOM, "A simple but considerably long tool tip for menu item!",
                ContextCompat.getColor(this, R.color.magenta), 750L);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToolTipViewWithParent((Button) v,
                        v.getId() == R.id.button1 || v.getId() == R.id.button7 ? Gravity.TOP : Gravity.BOTTOM);
            }
        };
        findViewById(R.id.button1).setOnClickListener(listener);
        findViewById(R.id.button2).setOnClickListener(listener);
        findViewById(R.id.button3).setOnClickListener(listener);
        findViewById(R.id.button4).setOnClickListener(listener);
        findViewById(R.id.button5).setOnClickListener(listener);
        findViewById(R.id.button6).setOnClickListener(listener);
        findViewById(R.id.button7).setOnClickListener(listener);
    }

    private void showToolTipView(View anchorView, int gravity, CharSequence text, int backgroundColor) {
        showToolTipView(anchorView, gravity, text, backgroundColor, 0L);
    }

    private void showToolTipViewWithParent(final Button anchorView, int gravity) {
        showToolTipView(anchorView, gravity, "Tool tip for " + anchorView.getText(), Color.BLACK, 0L);
    }

    private void showToolTipView(final View anchorView, int gravity, CharSequence text, int backgroundColor, long delay) {
        if (anchorView.getTag() != null) {
            ((ToolTipView) anchorView.getTag()).remove();
            anchorView.setTag(null);
            return;
        }

        ToolTip toolTip = createToolTip(text, backgroundColor);
        ToolTipView toolTipView = createToolTipView(toolTip, anchorView, gravity);
        if (delay > 0L) {
            toolTipView.showDelayed(delay);
        } else {
            toolTipView.show();
        }
        anchorView.setTag(toolTipView);

        toolTipView.setOnToolTipClickedListener(new ToolTipView.OnToolTipClickedListener() {
            @Override
            public void onToolTipClicked(ToolTipView toolTipView) {
                anchorView.setTag(null);
            }
        });
    }

    private ToolTip createToolTip(CharSequence text, int backgroundColor) {
        Resources resources = getResources();
        int padding = resources.getDimensionPixelSize(R.dimen.padding);
        int textSize = resources.getDimensionPixelSize(R.dimen.text_size);
        int radius = resources.getDimensionPixelSize(R.dimen.radius);
        return new ToolTip.Builder()
                .withText(text)
                .withTextColor(Color.WHITE)
                .withTextSize(textSize)
                .withBackgroundColor(backgroundColor)
                .withPadding(padding, padding, padding, padding)
                .withCornerRadius(radius)
                .build();
    }

    private ToolTipView createToolTipView(ToolTip toolTip, View anchorView, int gravity) {
        return new ToolTipView.Builder(this)
                .withAnchor(anchorView)
                .withToolTip(toolTip)
                .withGravity(gravity)
                .build();
    }
}
