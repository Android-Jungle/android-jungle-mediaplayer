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

package com.jungle.mediaplayer.widgets.control;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.jungle.mediaplayer.R;

public class PlayerLoadingControl extends FrameLayout {

    private TextView mLoadingText;
    private View mLoadingIcon;
    private View mLoadingContainer;
    private View mLoadingErrorContainer;


    public PlayerLoadingControl(Context context) {
        super(context);
        initLayout(context);
    }

    public PlayerLoadingControl(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public PlayerLoadingControl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context);
    }

    private void initLayout(Context context) {
        View.inflate(context, R.layout.layout_player_loading, this);

        mLoadingText = (TextView) findViewById(R.id.loading_text);
        mLoadingIcon = findViewById(R.id.loading_icon);
        mLoadingContainer = findViewById(R.id.loading_container);
        mLoadingErrorContainer = findViewById(R.id.loading_error_container);
    }

    public void showError(String errorMsg) {
        View generalContainer = findViewById(R.id.general_msg_container);
        TextView errorMsgView = (TextView) findViewById(R.id.error_msg);

        if (TextUtils.isEmpty(errorMsg)) {
            errorMsgView.setVisibility(View.GONE);
            generalContainer.setVisibility(View.VISIBLE);
            showError(false);
        } else {
            errorMsgView.setText(errorMsg);
            errorMsgView.setVisibility(View.VISIBLE);
            generalContainer.setVisibility(View.GONE);
            showError(true);
        }
    }

    public void showLoading(boolean hasBackground) {
        AnimationDrawable drawable = (AnimationDrawable) mLoadingIcon.getBackground();
        drawable.start();

        mLoadingContainer.setBackgroundResource(
                hasBackground ? android.R.color.black : R.color.seek_loading_bkg);
        mLoadingContainer.setVisibility(View.VISIBLE);
    }

    public void showError(boolean show) {
        AnimationDrawable drawable = (AnimationDrawable) mLoadingIcon.getBackground();
        drawable.stop();

        mLoadingContainer.setVisibility(View.GONE);
        mLoadingErrorContainer.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
