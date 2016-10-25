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
import android.widget.FrameLayout;
import android.widget.TextView;
import com.jungle.mediaplayer.R;
import com.jungle.mediaplayer.base.MediaPlayerUtils;

public class ProgressAdjustPanel extends FrameLayout {

    public ProgressAdjustPanel(Context context) {
        super(context);
        initLayout(context);
    }

    public ProgressAdjustPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public ProgressAdjustPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context);
    }

    private void initLayout(Context context) {
        View.inflate(context, R.layout.layout_progress_adjust_panel, this);
    }

    public void adjustForward(int currProgressMS, int totalDurationMS) {
        adjustInternal(R.drawable.fast_forward, currProgressMS, totalDurationMS);
    }

    public void adjustBackward(int currProgressMS, int totalDurationMS) {
        adjustInternal(R.drawable.fast_backward, currProgressMS, totalDurationMS);
    }

    private void adjustInternal(int iconResId, int currProgressMS, int totalDurationMS) {
        View adjustIconView = findViewById(R.id.adjust_icon);
        adjustIconView.setBackgroundResource(iconResId);

        TextView currProgress = (TextView) findViewById(R.id.curr_progress);
        TextView totalDuration = (TextView) findViewById(R.id.total_duration);

        currProgress.setText(MediaPlayerUtils.formatTime(currProgressMS));
        totalDuration.setText(MediaPlayerUtils.formatTime(totalDurationMS));

        setVisibility(View.VISIBLE);
    }

    public void hidePanel() {
        setVisibility(View.GONE);
    }
}
