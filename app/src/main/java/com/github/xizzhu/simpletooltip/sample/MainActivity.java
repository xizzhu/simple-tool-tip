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

package com.github.xizzhu.simpletooltip.sample;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.github.xizzhu.simpletooltip.ToolTip;
import com.github.xizzhu.simpletooltip.ToolTipView;

public class MainActivity extends AppCompatActivity {
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        findViewById(R.id.top_left_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToolTipView(v, "It is a very simple tool tip!",
                        ContextCompat.getColor(MainActivity.this, R.color.blue));
            }
        });
        findViewById(R.id.top_right_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToolTipView(v, "It is yet another very simple tool tip!",
                        ContextCompat.getColor(MainActivity.this, R.color.green));
            }
        });
        findViewById(R.id.central_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToolTipView(v, "It is a very simple tool tip in the center!",
                        ContextCompat.getColor(MainActivity.this, R.color.magenta));
            }
        });
        findViewById(R.id.bottom_left_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToolTipView(v, "Tool tip, once more!",
                        ContextCompat.getColor(MainActivity.this, R.color.maroon));
            }
        });
        findViewById(R.id.bottom_right_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToolTipView(v, "Magical tool tip!",
                        ContextCompat.getColor(MainActivity.this, R.color.navy));
            }
        });

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToolTipViewWithParent((Button) v);
            }
        };
        findViewById(R.id.button1).setOnClickListener(listener);
        findViewById(R.id.button2).setOnClickListener(listener);
        findViewById(R.id.button3).setOnClickListener(listener);
        findViewById(R.id.button4).setOnClickListener(listener);
        findViewById(R.id.button5).setOnClickListener(listener);
        findViewById(R.id.button6).setOnClickListener(listener);
        findViewById(R.id.button7).setOnClickListener(listener);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showToolTipView(findViewById(R.id.central_button), "A simple tool tip!",
                        ContextCompat.getColor(MainActivity.this, R.color.magenta));
            }
        }, 750L);
    }

    private void showToolTipView(final View anchorView, CharSequence text, int backgroundColor) {
        if (anchorView.getTag() != null) {
            ((ToolTipView) anchorView.getTag()).remove();
            anchorView.setTag(null);
            return;
        }

        Resources resources = getResources();
        int padding = resources.getDimensionPixelSize(R.dimen.padding);
        int textSize = resources.getDimensionPixelSize(R.dimen.text_size);
        int radius = resources.getDimensionPixelSize(R.dimen.radius);

        ToolTip toolTip = new ToolTip.Builder()
                .withText(text)
                .withTextColor(Color.WHITE)
                .withTextSize(textSize)
                .withBackgroundColor(backgroundColor)
                .withPadding(padding, padding, padding, padding)
                .withCornerRadius(radius)
                .build();
        ToolTipView toolTipView = new ToolTipView.Builder(this)
                .withAnchor(anchorView)
                .withToolTip(toolTip)
                .build();
        toolTipView.show();
        anchorView.setTag(toolTipView);

        ToolTipView.OnToolTipClickedListener listener = new ToolTipView.OnToolTipClickedListener() {
            @Override
            public void onToolTipClicked(ToolTipView toolTipView) {
                anchorView.setTag(null);
            }
        };
        toolTipView.setOnToolTipClickedListener(listener);
        toolTipView.setTag(listener); // prevent it from being GC'ed
    }

    private void showToolTipViewWithParent(final Button anchorView) {
        if (anchorView.getTag() != null) {
            ((ToolTipView) anchorView.getTag()).remove();
            anchorView.setTag(null);
            return;
        }

        Resources resources = getResources();
        int padding = resources.getDimensionPixelSize(R.dimen.padding);
        int textSize = resources.getDimensionPixelSize(R.dimen.text_size);
        int radius = resources.getDimensionPixelSize(R.dimen.radius);

        ToolTip toolTip = new ToolTip.Builder()
                .withText("Tool tip for " + anchorView.getText())
                .withTextColor(Color.WHITE)
                .withTextSize(textSize)
                .withBackgroundColor(Color.BLACK)
                .withPadding(padding, padding, padding, padding)
                .withCornerRadius(radius)
                .build();
        ToolTipView toolTipView = new ToolTipView.Builder(this)
                .withAnchor(anchorView)
                .withParent((FrameLayout) findViewById(R.id.tool_tip_view_holder))
                .withToolTip(toolTip)
                .build();
        toolTipView.show();
        anchorView.setTag(toolTipView);

        ToolTipView.OnToolTipClickedListener listener = new ToolTipView.OnToolTipClickedListener() {
            @Override
            public void onToolTipClicked(ToolTipView toolTipView) {
                anchorView.setTag(null);
            }
        };
        toolTipView.setOnToolTipClickedListener(listener);
        toolTipView.setTag(listener); // prevent it from being GC'ed
    }
}
