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
import android.media.AudioManager;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.jungle.mediaplayer.base.MediaPlayerUtils;
import com.jungle.mediaplayer.widgets.panel.AdjustPanel;
import com.jungle.mediaplayer.widgets.panel.ProgressAdjustPanel;

public class MediaPlayerGestureController {

    public interface GestureOperationHelper {
        void onSingleTap();

        void onDoubleTap();

        void onStartDragProgress();

        void onDragProgress(int startDragPos, float distanceX);

        void onEndDragProgress(int dragPosition, float totalDistanceX);

        boolean onCanHandleGesture();

        int getCurrentPosition();
    }


    private static enum AdjustType {
        None,
        Volume,
        Brightness,
        FastBackwardOrForward,
    }


    private static final int INVALID_DRAG_PROGRESS = -1;

    private Context mContext;
    private View mPlayerRootView;
    private AdjustType mAdjustType = AdjustType.None;
    private AdjustPanel mAdjustPanel;
    private ProgressAdjustPanel mProgressAdjustPanel;
    private FrameLayout mAdjustPanelContainer;
    private FrameLayout mProgressAdjustPanelContainer;
    private GestureDetector mGestureDetector;
    private GestureOperationHelper mOperateHelper;
    private float mCurrentBrightness = 0;
    private int mCurrentVolume = 0;
    private boolean mIsBrightnessUsed = false;
    private float mStartDragX = 0;
    private int mStartDragProgressPosition = INVALID_DRAG_PROGRESS;
    private int mDragProgressPosition = 0;


    public MediaPlayerGestureController(
            Context context, View playerRootView, GestureOperationHelper helper) {

        mContext = context;
        mOperateHelper = helper;
        mPlayerRootView = playerRootView;
        initGestureDetector();

        mCurrentBrightness = MediaPlayerUtils.getSystemBrightnessPercent(context);
    }

    public void handleTouchEvent(MotionEvent event) {
        if (!mOperateHelper.onCanHandleGesture()) {
            reset();
            return;
        }

        int action = event.getActionMasked();
        if (action == MotionEvent.ACTION_DOWN) {
            mStartDragX = event.getRawX();
            updateCurrentInfo();
        }

        mGestureDetector.onTouchEvent(event);
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            if (mAdjustType == AdjustType.FastBackwardOrForward) {
                mOperateHelper.onEndDragProgress(mDragProgressPosition, event.getRawX() - mStartDragX);

                mStartDragProgressPosition = INVALID_DRAG_PROGRESS;
                mDragProgressPosition = 0;
                mStartDragX = 0;
            }

            reset();
        }
    }

    private void reset() {
        mAdjustType = AdjustType.None;
        mProgressAdjustPanel.hidePanel();
        if (mAdjustPanel != null) {
            mAdjustPanel.hidePanel();
        }
    }

    public void setAdjustPanelContainer(FrameLayout layout) {
        mAdjustPanelContainer = layout;

        mAdjustPanel = new AdjustPanel(mContext);
        mAdjustPanelContainer.removeAllViews();
        mAdjustPanelContainer.addView(mAdjustPanel, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public void setProgressAdjustPanelContainer(FrameLayout layout) {
        mProgressAdjustPanelContainer = layout;

        mProgressAdjustPanel = new ProgressAdjustPanel(mContext);
        mProgressAdjustPanel.setVisibility(View.GONE);
        mProgressAdjustPanelContainer.addView(mProgressAdjustPanel, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
    }

    public void prepareForPlay() {
        mStartDragProgressPosition = INVALID_DRAG_PROGRESS;
        mDragProgressPosition = 0;
        mStartDragX = 0;
    }

    private Runnable mDoubleTapRunnable = new Runnable() {
        @Override
        public void run() {
            mOperateHelper.onSingleTap();
        }
    };

    private void initGestureDetector() {
        mGestureDetector = new GestureDetector(mContext,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        mPlayerRootView.postDelayed(mDoubleTapRunnable, 200);
                        return true;
                    }

                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        mPlayerRootView.removeCallbacks(mDoubleTapRunnable);
                        mOperateHelper.onDoubleTap();
                        return true;
                    }

                    @Override
                    public boolean onScroll(MotionEvent e1, MotionEvent e2,
                            float distanceX, float distanceY) {

                        if (e1 == null || e2 == null) {
                            return true;
                        }

                        if (mAdjustType == AdjustType.None) {
                            if (Math.abs(distanceX) > Math.abs(distanceY)) {
                                mAdjustType = AdjustType.FastBackwardOrForward;
                            } else {
                                if (e1.getX() < mPlayerRootView.getMeasuredWidth() / 2) {
                                    mAdjustType = AdjustType.Brightness;
                                } else {
                                    mAdjustType = AdjustType.Volume;
                                }
                            }
                        }

                        distanceX = e2.getX() - e1.getX();
                        distanceY = e2.getY() - e1.getY();
                        return adjustInternal(e1, e2, distanceX, distanceY);
                    }
                });
    }

    private void updateCurrentInfo() {
        AudioManager manager = (AudioManager)
                mContext.getSystemService(Context.AUDIO_SERVICE);
        mCurrentVolume = manager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (mIsBrightnessUsed) {
            mCurrentBrightness = MediaPlayerUtils.getBrightnessPercent(mContext);
        } else {
            mCurrentBrightness = MediaPlayerUtils.getSystemBrightnessPercent(mContext);
        }
    }

    private boolean adjustInternal(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (mAdjustType == AdjustType.FastBackwardOrForward) {
            // Adjust Progress.
            return adjustProgress(distanceX);
        } else if (mAdjustType == AdjustType.Brightness) {
            // Adjust Brightness.
            return adjustBrightness(distanceY);
        } else if (mAdjustType == AdjustType.Volume) {
            // Adjust Volume.
            return adjustVolume(distanceY);
        }

        return true;
    }

    private boolean adjustVolume(float distanceY) {
        if (mAdjustPanel == null) {
            return true;
        }

        distanceY *= -1;
        float percent = distanceY / (float) mPlayerRootView.getMeasuredHeight();

        AudioManager manager = (AudioManager)
                mContext.getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volumeOffsetAccurate = maxVolume * percent * 1.2f;
        int volumeOffset = (int) volumeOffsetAccurate;

        if (volumeOffset == 0 && Math.abs(volumeOffsetAccurate) > 0.2f) {
            if (distanceY > 0) {
                volumeOffset = 1;
            } else if (distanceY < 0) {
                volumeOffset = -1;
            }
        }

        int currVolume = mCurrentVolume + volumeOffset;
        if (currVolume < 0) {
            currVolume = 0;
        } else if (currVolume >= maxVolume) {
            currVolume = maxVolume;
        }

        manager.setStreamVolume(AudioManager.STREAM_MUSIC, currVolume, 0);

        float volumePercent = (float) currVolume / (float) maxVolume;
        mAdjustPanel.adjustVolume(volumePercent);

        return true;
    }

    private boolean adjustBrightness(float distanceY) {
        if (mAdjustPanel == null) {
            return true;
        }

        distanceY *= -1;
        float percent = distanceY / (float) mPlayerRootView.getMeasuredHeight();
        float brightnessOffset = percent * 1.2f;
        float brightness = mCurrentBrightness + brightnessOffset;

        if (brightness < 0) {
            brightness = 0;
        } else if (brightness > 1) {
            brightness = 1;
        }

        MediaPlayerUtils.setBrightness(mContext, brightness);
        mAdjustPanel.adjustBrightness(brightness);
        mIsBrightnessUsed = true;

        return true;
    }

    private boolean adjustProgress(float distanceX) {
        mOperateHelper.onStartDragProgress();
        if (mStartDragProgressPosition == INVALID_DRAG_PROGRESS) {
            mStartDragProgressPosition = mOperateHelper.getCurrentPosition();
        }

        mOperateHelper.onDragProgress(mStartDragProgressPosition, distanceX);
        return true;
    }

    public void showAdjustProgress(boolean forward, int currPosition, int total) {
        mDragProgressPosition = currPosition;

        if (forward) {
            mProgressAdjustPanel.adjustForward(currPosition, total);
        } else {
            mProgressAdjustPanel.adjustBackward(currPosition, total);
        }
    }
}
