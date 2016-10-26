/**
 * Android Jungle-MediaPlayer-Demo project.
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

package com.jungle.mediaplayer.demo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.jungle.mediaplayer.base.MediaPlayerUtils;
import com.jungle.mediaplayer.base.SimpleMediaPlayerListener;
import com.jungle.mediaplayer.base.VideoInfo;
import com.jungle.mediaplayer.player.BaseMediaPlayer;
import com.jungle.mediaplayer.player.SystemImplMediaPlayer;

public class PlayAudioActivity extends AppCompatActivity {

    private static final String AUDIO_URL =
            "http://200000594.vod.myqcloud.com/200000594_1617cc56708f11e596723b988fc18469.f20.mp4";


    private BaseMediaPlayer mMediaPlayer;
    private Button mPlayBtn;
    private SeekBar mProgressBar;
    private TextView mPlayProgressView;
    private Handler mUIHandler = new Handler(Looper.getMainLooper());
    private boolean mHasAudioPlay = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.play_audio);
        setContentView(R.layout.activity_play_audio);

        initAudioPlayer();
        mPlayBtn = (Button) findViewById(R.id.play_audio_btn);
        mProgressBar = (SeekBar) findViewById(R.id.audio_progress);
        mPlayProgressView = (TextView) findViewById(R.id.play_progress_view);
        mProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && (mMediaPlayer.isPlaying() || mMediaPlayer.isPaused())) {
                    mMediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        mPlayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playAudio();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaPlayer.stop();
        unScheduleUpdateProgress();
    }

    private void initAudioPlayer() {
        mMediaPlayer = new SystemImplMediaPlayer(this);
        mMediaPlayer.addPlayerListener(new SimpleMediaPlayerListener() {

            @Override
            public void onStartPlay() {
                mProgressBar.setProgress(0);
                updateProgressView(0, 0);
                mPlayBtn.setText(R.string.loading_media);
            }

            @Override
            public void onFinishLoading() {
                mHasAudioPlay = true;
                mPlayBtn.setText(R.string.pause_audio);
                mProgressBar.setMax(mMediaPlayer.getDuration());
                scheduleUpdateProgress();
            }

            @Override
            public void onLoadFailed() {
                mHasAudioPlay = false;
                showToast(R.string.load_media_error);
                mPlayBtn.setText(R.string.play_audio);
                updateProgressView(0, 0);
            }

            @Override
            public void onResumed() {
                mPlayBtn.setText(R.string.pause_audio);
            }

            @Override
            public void onPaused() {
                mPlayBtn.setText(R.string.play_audio);
            }

            @Override
            public void onPlayComplete() {
                mHasAudioPlay = false;
                showToast(R.string.play_complete);
                mPlayBtn.setText(R.string.play_audio);

                updateProgressView(0, 0);
                unScheduleUpdateProgress();
            }

            @Override
            public void onStopped() {
                mHasAudioPlay = false;
                mPlayBtn.setText(R.string.play_audio);

                updateProgressView(0, 0);
                unScheduleUpdateProgress();
            }
        });
    }

    private void showToast(int msgId) {
        Toast.makeText(this, msgId, Toast.LENGTH_SHORT).show();
    }

    private void playAudio() {
        if (!mHasAudioPlay) {
            mMediaPlayer.play(new VideoInfo(AUDIO_URL));
        }

        if (mMediaPlayer.isPaused()) {
            mMediaPlayer.resume();
        } else {
            mMediaPlayer.pause();
        }
    }

    private void scheduleUpdateProgress() {
        mUIHandler.postDelayed(mUpdateProgressRunnable, 1000);
    }

    private void unScheduleUpdateProgress() {
        mUIHandler.removeCallbacks(mUpdateProgressRunnable);
    }

    private Runnable mUpdateProgressRunnable = new Runnable() {
        @Override
        public void run() {
            int progress = mMediaPlayer.getCurrentPosition();
            int max = mMediaPlayer.getDuration();

            updateProgressView(progress, max);
            scheduleUpdateProgress();
        }
    };

    private void updateProgressView(int progress, int max) {
        mProgressBar.setProgress(progress);
        mPlayProgressView.setText(getString(R.string.progress_format,
                MediaPlayerUtils.formatTime(progress),
                MediaPlayerUtils.formatTime(max)));
    }
}
