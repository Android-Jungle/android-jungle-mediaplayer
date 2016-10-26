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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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

    private static final String EXTRA_AUDIO_URL = "extra_audio_url";


    public static void start(Context context, String url) {
        Intent intent = new Intent(context, PlayAudioActivity.class);
        intent.putExtra(EXTRA_AUDIO_URL, url);
        context.startActivity(intent);
    }


    private BaseMediaPlayer mMediaPlayer;
    private Button mPlayBtn;
    private SeekBar mProgressBar;
    private TextView mPlayProgressView;
    private Handler mUIHandler = new Handler(Looper.getMainLooper());
    private boolean mHasAudioPlay = false;
    private String mAudioUrl;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.play_audio);
        setContentView(R.layout.activity_play_audio);

        mAudioUrl = getIntent().getStringExtra(EXTRA_AUDIO_URL);
        TextView urlView = (TextView) findViewById(R.id.audio_url);
        if (!TextUtils.isEmpty(mAudioUrl)) {
            urlView.setText(mAudioUrl);
        } else {
            urlView.setText(R.string.media_url_error);
        }

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
        mMediaPlayer.destroy();
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
        if (TextUtils.isEmpty(mAudioUrl)) {
            showToast(R.string.media_url_error);
            return;
        }

        if (!mHasAudioPlay) {
            mMediaPlayer.play(new VideoInfo(mAudioUrl));
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
