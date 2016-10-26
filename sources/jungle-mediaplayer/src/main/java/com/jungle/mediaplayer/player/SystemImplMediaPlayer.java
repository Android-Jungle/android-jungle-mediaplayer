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

package com.jungle.mediaplayer.player;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import com.jungle.mediaplayer.base.VideoInfo;
import com.jungle.mediaplayer.player.render.MediaRender;
import com.jungle.mediaplayer.player.render.MockMediaRender;

import java.io.IOException;


/*
 * new MediaPlayer  |
 * .reset           |-> [IDLE] -> .setDataSource
 *                         |             |
 *                         |             -> [Initialized] -> .prepareAsync -> [Preparing]
 *                         |                             |                        |
 *                         |                             |-> .prepare      -> [Prepared]
 *                         |                                                      |
 *                         |                                                      |
 *                         |                        |<- .seekTo <-|               |
 *                         |                        |             |               |
 *                         |                        |             |               |
 * [END] <- .release <- [Stopped] <---  .stop  <--- [___Started___] <--- .start <-|
 *                                                    |          |
 *                                                    |          |
 *                                                 .pause      .start
 *                                                    |          |
 *                                                    |          |
 *                                                  [____Paused___]
 *
 */
public class SystemImplMediaPlayer extends BaseMediaPlayer {

    protected MediaPlayer mMediaPlayer;


    public SystemImplMediaPlayer(Context context) {
        this(context, new MockMediaRender());
    }

    public SystemImplMediaPlayer(Context context, MediaRender render) {
        super(context, render);
    }

    private void init() {
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnErrorListener(mOnErrorListener);
        mMediaPlayer.setOnPreparedListener(mOnPreparedListener);
        mMediaPlayer.setOnCompletionListener(mOnCompletionListener);
        mMediaPlayer.setOnSeekCompleteListener(mOnSeekCompletionListener);
        mMediaPlayer.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
        mMediaPlayer.setOnVideoSizeChangedListener(mOnVideoSizeChangedListener);
    }

    @Override
    public void play(VideoInfo videoInfo) {
        if (mMediaPlayer != null) {
            destroy();
        }

        init();
        super.play(videoInfo);

        // Clear old display and Reset.
        Log.e(TAG, "Reset MediaPlayer!");
        mMediaPlayer.setDisplay(null);
        mMediaPlayer.reset();

        try {
            mMediaPlayer.setDataSource(mContext, Uri.parse(videoInfo.getStreamUrl()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Prepare For Play.
        Log.e(TAG, "Prepare MediaPlayer!");
        try {
            mMediaPlayer.prepareAsync();
        } catch (Exception e) {
            e.printStackTrace();
            notifyError(-1, false, "Video PrepareAsync FAILED! Video might be damaged!!");
            return;
        }

        notifyStartPlay();
        notifyLoading();
    }

    @Override
    public void pause() {
        if (mMediaPlayer == null || !mMediaPlayerIsPrepared) {
            return;
        }

        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
            mIsPaused = true;
            notifyPaused();
        }
    }

    @Override
    public void resume() {
        if (mMediaPlayer == null || !mMediaPlayerIsPrepared || !mMediaRender.isRenderValid()) {
            return;
        }

        mIsPaused = false;
        mMediaPlayer.start();
        notifyResumed();
    }

    @Override
    public void stop() {
        if (mMediaPlayer == null) {
            return;
        }

        mIsPaused = false;
        mMediaPlayerIsPrepared = false;
        mMediaPlayer.stop();

        notifyStopped();
    }

    @Override
    public void seekTo(int millSeconds) {
        if (mMediaPlayer != null) {
            if (millSeconds < 0) {
                millSeconds = 0;
            }

            mIsPaused = false;
            mMediaPlayer.seekTo(millSeconds);
            mMainHandler.postDelayed(mSeekRunnable, 300);
        }
    }

    @Override
    public void setVolume(float volume) {
        if (mMediaPlayer != null) {
            mMediaPlayer.setVolume(volume, volume);
        }
    }

    @Override
    public int getDuration() {
        return mMediaPlayer != null ? mMediaPlayer.getDuration() : 0;
    }

    @Override
    public int getCurrentPosition() {
        return mMediaPlayer != null ? mMediaPlayer.getCurrentPosition() : 0;
    }

    @Override
    public void destroy() {
        super.destroy();

        clearLoadingFailed();
        if (mMediaPlayer != null) {
            stop();

            mMediaPlayer.setDisplay(null);
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public boolean hasVideoPlay() {
        return mMediaPlayer != null;
    }

    @Override
    public boolean isPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    @Override
    public boolean isPaused() {
        return mIsPaused;
    }

    @Override
    protected void playWithMediaRender() {
        if (!mMediaPlayerIsPrepared) {
            return;
        }

        try {
            mMediaRender.prepareMediaRender(mMediaPlayer);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mMediaPlayer.setScreenOnWhilePlaying(true);
        mMediaPlayer.start();
        mMediaPlayer.seekTo(0);

        trySeekToStartPosition();
    }

    @Override
    protected void surfaceHolderChanged() {
        if (mMediaPlayer != null) {
            mMediaRender.mediaRenderChanged(mMediaPlayer);
        }
    }

    private void clearLoadingFailed() {
        mMainHandler.removeCallbacks(mLoadingFailedRunnable);
    }

    private Runnable mSeekRunnable = new Runnable() {
        @Override
        public void run() {
            mIsLoading = true;
            notifyStartSeek();
        }
    };

    private MediaPlayer.OnPreparedListener mOnPreparedListener = new MediaPlayer.OnPreparedListener() {
        public void onPrepared(MediaPlayer player) {
            Log.e(TAG, "**SUCCESS** Video Prepared Complete!");

            mAutoPlayWhenHolderCreated = false;
            mMediaPlayerIsPrepared = true;
            mIsLoading = false;

            // Start Play.
            if (mMediaRender.isRenderCreating() || !mMediaRender.isRenderValid()) {
                mAutoPlayWhenHolderCreated = true;
            } else {
                playWithMediaRender();
            }

            clearLoadingFailed();
            notifyFinishLoading();
        }
    };

    private void trySeekToStartPosition() {
        if (mVideoInfo == null) {
            return;
        }

        int seekToPosition = mVideoInfo.getCurrentPosition();
        if (mMediaPlayer != null && mMediaPlayerIsPrepared && mVideoSizeInitialized && seekToPosition > 0) {
            mMediaPlayer.seekTo(seekToPosition);
        }
    }

    private MediaPlayer.OnSeekCompleteListener mOnSeekCompletionListener =
            new MediaPlayer.OnSeekCompleteListener() {
                @Override
                public void onSeekComplete(MediaPlayer mp) {
                    mMainHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mMainHandler.removeCallbacks(mSeekRunnable);
                            mIsLoading = false;
                            notifySeekComplete();
                        }
                    }, 100);
                }
            };

    private MediaPlayer.OnCompletionListener mOnCompletionListener =
            new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer player) {
                    Log.e(TAG, "Video Play Complete!");

                    player.seekTo(0);
                    notifyPlayComplete();
                }
            };

    private MediaPlayer.OnVideoSizeChangedListener mOnVideoSizeChangedListener =
            new MediaPlayer.OnVideoSizeChangedListener() {
                public void onVideoSizeChanged(MediaPlayer player, int width, int height) {
                    mVideoWidth = player.getVideoWidth();
                    mVideoHeight = player.getVideoHeight();

                    updateMediaRenderSize();

                    mVideoSizeInitialized = true;
                    trySeekToStartPosition();
                }
            };

    private MediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener =
            new MediaPlayer.OnBufferingUpdateListener() {
                public void onBufferingUpdate(MediaPlayer player, int percent) {
                    mBufferPercent = percent;
                }
            };

    private MediaPlayer.OnErrorListener mOnErrorListener = new MediaPlayer.OnErrorListener() {
        public boolean onError(MediaPlayer player, int what, int extra) {
            String errorWhat;
            switch (what) {
                case MediaPlayer.MEDIA_ERROR_UNKNOWN:
                    errorWhat = "MEDIA_ERROR_UNKNOWN";
                    break;
                case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                    errorWhat = "MEDIA_ERROR_SERVER_DIED";
                    break;
                default:
                    errorWhat = "!";
            }

            String errorExtra;
            switch (extra) {
                case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
                    errorExtra = "MEDIA_ERROR_UNSUPPORTED";
                    break;
                case MediaPlayer.MEDIA_ERROR_MALFORMED:
                    errorExtra = "MEDIA_ERROR_MALFORMED";
                    break;
                case MediaPlayer.MEDIA_ERROR_IO:
                    errorExtra = "MEDIA_ERROR_IO";
                    break;
                case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
                    errorExtra = "MEDIA_ERROR_TIMED_OUT";
                    break;
                default:
                    errorExtra = "!";
            }

            String msg = String.format("what = %d (%s), extra = %d (%s)",
                    what, errorWhat, extra, errorExtra);

            Log.e(TAG, msg);
            notifyError(what, msg);
            return true;
        }
    };
}
