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

package com.jungle.mediaplayer.base;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.provider.Settings;
import android.view.OrientationEventListener;

import java.lang.ref.WeakReference;

public class ScreenOrientationSwitcher extends OrientationEventListener {

    private static final long MAX_CHECK_INTERVAL = 3000;


    public interface OnChangeListener {
        void onChanged(int requestedOrientation);
    }


    private WeakReference<Context> mContextRef;
    private boolean mIsSupportGravity = false;
    private int mCurrOrientation = ORIENTATION_UNKNOWN;
    private long mLastCheckTimestamp = 0;
    private boolean mEnableAutoRotation = true;
    private OnChangeListener mChangeListener;


    public ScreenOrientationSwitcher(Context context) {
        super(context);
        mContextRef = new WeakReference<Context>(context);
    }

    public ScreenOrientationSwitcher(Context context, int rate) {
        super(context, rate);
        mContextRef = new WeakReference<Context>(context);
    }

    public void setChangeListener(OnChangeListener listener) {
        mChangeListener = listener;
    }

    public int getCurrOrientation() {
        return mCurrOrientation;
    }

    public void setEnableAutoRotation(boolean enable) {
        mEnableAutoRotation = enable;
    }

    private boolean isScreenAutoRotate() {
        Context context = mContextRef.get();
        if (context == null) {
            return false;
        }

        int gravity = 0;
        try {
            gravity = Settings.System.getInt(context.getContentResolver(),
                    Settings.System.ACCELEROMETER_ROTATION);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        return gravity == 1;
    }

    @Override
    public void onOrientationChanged(int orientation) {
        if (!mEnableAutoRotation) {
            return;
        }

        Context context = mContextRef.get();
        if (context == null || !(context instanceof Activity)) {
            return;
        }

        long currTimestamp = System.currentTimeMillis();
        if (currTimestamp - mLastCheckTimestamp > MAX_CHECK_INTERVAL) {
            mIsSupportGravity = isScreenAutoRotate();
            mLastCheckTimestamp = currTimestamp;
        }

        if (!mIsSupportGravity) {
            return;
        }

        if (orientation == ORIENTATION_UNKNOWN) {
            return;
        }

        int requestOrientation = ORIENTATION_UNKNOWN;
        if (orientation > 350 || orientation < 10) {
            requestOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        } else if (orientation > 80 && orientation < 100) {
            requestOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
        } else if (orientation > 260 && orientation < 280) {
            requestOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
        } else {
            return;
        }

        if (requestOrientation == mCurrOrientation) {
            return;
        }

        boolean needNotify = mCurrOrientation != ORIENTATION_UNKNOWN;
        mCurrOrientation = requestOrientation;

        if (needNotify) {
            if (mChangeListener != null) {
                mChangeListener.onChanged(requestOrientation);
            } else {
                Activity activity = (Activity) context;
                activity.setRequestedOrientation(requestOrientation);
            }
        }
    }
}
