/**
 * Android Jungle-MediaPlayer framework project.
 *
 * Copyright 2016 Arno Zhang <zyfgood12@163.com>
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

package com.jungle.mediaplayer.widgets.panel;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import com.jungle.mediaplayer.R;

public class AdjustPanel extends FrameLayout {

    private View mAdjustIconView;
    private ProgressBar mAdjustPercentProgress;


    public AdjustPanel(Context context) {
        super(context);
        initLayout(context);
    }

    public AdjustPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public AdjustPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context);
    }

    private void initLayout(Context context) {
        View.inflate(context, R.layout.layout_adjust_panel, this);
        mAdjustIconView = findViewById(R.id.adjust_icon);
        mAdjustPercentProgress = (ProgressBar) findViewById(R.id.adjust_percent);
    }

    public void adjustVolume(float percent) {
        adjust(R.drawable.adjust_volume, percent);
    }

    public void adjustBrightness(float percent) {
        adjust(R.drawable.adjust_brightness, percent);
    }

    public void hidePanel() {
        ViewGroup parent = (ViewGroup) getParent();
        parent.setVisibility(View.GONE);
    }

    private void adjust(int iconResId, float percent) {
        mAdjustIconView.setBackgroundResource(iconResId);
        mAdjustPercentProgress.setProgress((int) (percent * 100));

        ViewGroup parent = (ViewGroup) getParent();
        parent.setVisibility(View.VISIBLE);
    }
}
