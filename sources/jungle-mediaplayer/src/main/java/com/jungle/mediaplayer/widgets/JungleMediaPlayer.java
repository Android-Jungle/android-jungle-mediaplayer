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

package com.jungle.mediaplayer.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import com.jungle.mediaplayer.R;
import com.jungle.mediaplayer.base.BaseMediaPlayerListener;
import com.jungle.mediaplayer.base.VideoInfo;
import com.jungle.mediaplayer.player.BaseMediaPlayer;
import com.jungle.mediaplayer.player.SystemImplMediaPlayer;
import com.jungle.mediaplayer.player.render.SurfaceViewMediaRender;

public class JungleMediaPlayer extends MediaPlayerFrame {

    private static final String TAG = "JungleMediaPlayer";


    public interface Listener extends BaseMediaPlayerListener {
        void onTitleBackClicked();

        void onToggleFullscreen(boolean isFullScreen, boolean reverseOrientation);

        void onAuditionCompleted();

        void onNextSection();

        void onReplayMedia(int startMillSeconds);

        void onReloadFromPosition(int playPosition);
    }


    private static class PlayVideoInfo {
        public VideoInfo mVideoInfo;
        public int mAuditionTimeSeconds;

        public PlayVideoInfo(VideoInfo info, int auditionTimeSeconds) {
            mVideoInfo = info;
            mAuditionTimeSeconds = auditionTimeSeconds;
        }
    }


    private BaseMediaPlayer mMediaPlayer;
    private Listener mPlayerListener;
    private boolean mAutoReloadWhenError = false;
    private boolean mIsReload = false;
    private PlayVideoInfo mSavedVideoInfo;


    public JungleMediaPlayer(Context context) {
        this(context, (AttributeSet) null, 0);
    }

    public JungleMediaPlayer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JungleMediaPlayer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        if (isInEditMode()) {
            return;
        }

        View.inflate(context, R.layout.layout_jungle_media_player, getMediaRootLayout());

        initView();
        initMediaPlayer();
        requestFocus();
    }

    private void initView() {
        mTopControl.createDefault();
        mBottomControl.createDefault();

        findViewById(R.id.refresh_video).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = mBottomControl.getPlayPosition();
                Log.e(TAG, String.format("Will Replay Media From Position: %d.", position));
                mPlayerListener.onReplayMedia(position);
            }
        });
    }

    private void initMediaPlayer() {
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.player_surface);
        mMediaPlayer = new SystemImplMediaPlayer(getContext(), new SurfaceViewMediaRender(surfaceView));
        mMediaPlayer.addPlayerListener(mBasePlayerListener);
        mBottomControl.setMediaPlayer(this);
    }

    public void setPlayerListener(Listener listener) {
        mPlayerListener = listener;
        mMediaPlayer.addPlayerListener(listener);
    }

    public void setAutoReloadWhenError(boolean autoReload) {
        mAutoReloadWhenError = autoReload;
    }

    private Runnable mAutoReloadWaitingRunnable = new Runnable() {
        @Override
        public void run() {
            Log.e(TAG, "Auto-Reload Failed, Will Show Error!");
            showError(true);
        }
    };

    private BaseMediaPlayerListener mBasePlayerListener = new BaseMediaPlayerListener() {
        @Override
        public void onLoading() {
            if (mIsReload) {
                showLoading(false);
                mIsReload = false;
            } else {
                showLoading(true);
            }
        }

        @Override
        public void onLoadFailed() {
            showError(true);
        }

        @Override
        public void onFinishLoading() {
            hideLoading();
            mBottomControl.switchViewState(mIsFullscreen);
        }

        @Override
        public void onError(int what, boolean canReload, String message) {
            if (mAutoReloadWhenError && canReload) {
                mIsReload = true;
                showLoading(false);

                int playPosition = mBottomControl.getPlayPosition();
                Log.e(TAG, String.format("Error! But Will Auto-Reload, playPosition = %d!!!", playPosition));
                mPlayerListener.onReloadFromPosition(playPosition);

                postDelayed(mAutoReloadWaitingRunnable, 20 * 1000);
            } else {
                Log.e(TAG, "Error! But Not Auto-Reload, Will Show Error!");
                showError(true);
            }
        }

        @Override
        public void onStartPlay() {
        }

        @Override
        public void onPlayComplete() {
        }

        @Override
        public void onStartSeek() {
            showLoading(false);
        }

        @Override
        public void onSeekComplete() {
            hideLoading();
        }

        @Override
        public void onResumed() {
        }

        @Override
        public void onPaused() {
        }

        @Override
        public void onStopped() {
        }
    };

    @Override
    public void destroy() {
        super.destroy();

        removeCallbacks(mAutoReloadWaitingRunnable);
        mMediaPlayer.destroy();
    }

    public void playMedia(VideoInfo info) {
        playMedia(info, 0, 0);
    }

    public void playMedia(VideoInfo info, int startMillSeconds) {
        playMedia(info, startMillSeconds, 0);
    }

    public void playMedia(VideoInfo info, int startMillSeconds, int auditionTimeSeconds) {
        info.setCurrentPosition(startMillSeconds);
        mSavedVideoInfo = new PlayVideoInfo(info, auditionTimeSeconds);
        playMediaInternal();
    }

    private void playMediaInternal() {
        unScheduleAuditionCheck();
        mBottomControl.prepareForPlay();
        mGestureController.prepareForPlay();
        removeCallbacks(mAutoReloadWaitingRunnable);

        if (!VideoInfo.validate(mSavedVideoInfo.mVideoInfo)) {
            showError(true);
            return;
        }

        showError(false);
        mMediaPlayer.play(mSavedVideoInfo.mVideoInfo);
        if (mSavedVideoInfo.mAuditionTimeSeconds > 0) {
            scheduleAuditionCheck();
        }
    }

    public boolean isPaused() {
        return mMediaPlayer.isPaused();
    }

    public boolean hasVideoPlay() {
        return mMediaPlayer != null && mMediaPlayer.hasVideoPlay();
    }

    @Override
    public void setVolume(float volume) {
        mMediaPlayer.setVolume(volume);
    }

    @Override
    public int getDuration() {
        return mMediaPlayer.getDuration();
    }

    @Override
    public void seekTo(int position) {
        mMediaPlayer.seekTo(position);
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    @Override
    public boolean isPlayCompleted() {
        return mMediaPlayer.isPlayCompleted();
    }

    @Override
    public int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    @Override
    public int getBufferPercent() {
        return mMediaPlayer.getBufferPercent();
    }

    @Override
    public void pause() {
        if (isLoading()) {
            return;
        }

        mMediaPlayer.pause();
    }

    @Override
    public void resume() {
        if (isLoading()) {
            return;
        }

        mMediaPlayer.resume();
    }

    @Override
    public void stop() {
        unScheduleAuditionCheck();
        mMediaPlayer.stop();
    }

    @Override
    public boolean isLoading() {
        return mMediaPlayer.isLoading();
    }

    @Override
    public boolean isLoadingFailed() {
        return mMediaPlayer.isLoadingFailed();
    }

    @Override
    public void addPlayerListener(BaseMediaPlayerListener listener) {
        mMediaPlayer.addPlayerListener(listener);
    }

    @Override
    protected void toggleFullScreen(boolean isFullscreen, boolean reverseOrientation) {
        mPlayerListener.onToggleFullscreen(mIsFullscreen, reverseOrientation);
    }

    @Override
    protected void updateMediaSize(int width, int height) {
        mMediaPlayer.updateMediaRenderSize(width, height, false);
    }

    public void setAutoResume(boolean autoResume) {
        mMediaPlayer.setAutoResume(autoResume);
    }

    @Override
    protected boolean checkAudition() {
        if (mMediaPlayer.getCurrentPosition() >= mSavedVideoInfo.mAuditionTimeSeconds * 1000) {
            stop();
            mPlayerListener.onAuditionCompleted();
            return true;
        }

        return false;
    }

    @Override
    protected void handleDragProgress(int startDragPos, float distanceX) {
        float percent = distanceX / (float) mRootView.getMeasuredWidth();
        int total = getDuration();
        int currPosition = startDragPos;
        int seekOffset = (int) (total * percent);

        currPosition += seekOffset;
        if (currPosition < 0) {
            currPosition = 0;
        } else if (currPosition > total) {
            currPosition = total;
        }

        mGestureController.showAdjustProgress(seekOffset > 0, currPosition, total);
        mBottomControl.dragProgress(currPosition);
    }

    @Override
    protected void handleEndDragProgress(int dragPosition, float totalDistanceX) {
        seekTo(dragPosition);
        if (!isPlaying()) {
            resume();
        }
    }

    @Override
    public void onBackBtnClicked() {
        mPlayerListener.onTitleBackClicked();
    }

    @Override
    public Bitmap captureMedia() {
        return null;
    }
}
