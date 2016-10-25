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

public class LockOrientationPanel extends FrameLayout
        implements View.OnClickListener {

    public interface OnLockChangedListener {
        void onChanged(boolean isLocked);
    }


    private View mLockIconView;
    private TextView mLockTipsView;
    private OnLockChangedListener mChangedListener;
    private boolean mIsLocked = false;


    public LockOrientationPanel(Context context) {
        super(context);
        initLayout(context);
    }

    public LockOrientationPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout(context);
    }

    public LockOrientationPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context);
    }

    private void initLayout(Context context) {
        View.inflate(context, R.layout.layout_lock_orientation_panel, this);
        mLockIconView = findViewById(R.id.lock_icon);
        mLockTipsView = (TextView) findViewById(R.id.lock_tips);

        setClickable(true);
        setOnClickListener(this);
    }

    public void setLockChangedListener(OnLockChangedListener listener) {
        mChangedListener = listener;
    }

    public void doDestroy() {
        removeCallbacks(mHidePanelRunnable);
    }

    private void showLock() {
        mLockIconView.setBackgroundResource(R.drawable.lock_screen_icon);
        mLockTipsView.setText(R.string.lock_orientation);
        showAndHideInternal();
    }

    private void showUnlock() {
        mLockIconView.setBackgroundResource(R.drawable.unlock_screen_icon);
        mLockTipsView.setText(R.string.unlock_orientation);
        showAndHideInternal();
    }

    public boolean isLocked() {
        return mIsLocked;
    }

    public void showAndHide() {
        if (!mIsLocked) {
            showLock();
        } else {
            showUnlock();
        }
    }

    private void showAndHideInternal() {
        removeCallbacks(mHidePanelRunnable);
        postDelayed(mHidePanelRunnable, 2000);

        setVisibility(View.VISIBLE);
    }

    private Runnable mHidePanelRunnable = new Runnable() {
        @Override
        public void run() {
            setVisibility(View.GONE);
        }
    };

    @Override
    public void onClick(View v) {
        mIsLocked = !mIsLocked;
        showAndHide();

        if (mChangedListener != null) {
            mChangedListener.onChanged(mIsLocked);
        }
    }
}
