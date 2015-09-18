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

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.github.xizzhu.simpletooltip.ToolTip;
import com.github.xizzhu.simpletooltip.ToolTipView;

public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        findViewById(R.id.top_left_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToolTipView(v, "It is a very simple tool tip!", getResources().getColor(R.color.blue));
            }
        });
        findViewById(R.id.top_right_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToolTipView(v, "It is yet another very simple tool tip!", getResources().getColor(R.color.green));
            }
        });
        findViewById(R.id.central_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToolTipView(v, "It is a very simple tool tip in the center!", getResources().getColor(R.color.magenta));
            }
        });
        findViewById(R.id.bottom_left_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToolTipView(v, "Tool tip, once more!", getResources().getColor(R.color.maroon));
            }
        });
        findViewById(R.id.bottom_right_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToolTipView(v, "Magical tool tip!", getResources().getColor(R.color.navy));
            }
        });
    }

    private void showToolTipView(View anchorView, CharSequence text, int backgroundColor) {
        if (anchorView.getTag() != null) {
            return;
        }

        ToolTip toolTip = new ToolTip.Builder()
                .withText(text)
                .withTextColor(Color.WHITE)
                .withTextSize(26.0F)
                .withBackgroundColor(backgroundColor)
                .build();
        ToolTipView toolTipView = new ToolTipView.Builder(this)
                .withAnchor(anchorView)
                .withToolTip(toolTip)
                .build();
        toolTipView.show();
        anchorView.setTag(toolTipView);
    }
}
