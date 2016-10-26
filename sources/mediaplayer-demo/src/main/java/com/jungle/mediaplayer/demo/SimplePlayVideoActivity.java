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
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.SurfaceView;
import android.view.ViewTreeObserver;
import android.widget.TextView;
import android.widget.Toast;
import com.jungle.mediaplayer.base.SimpleMediaPlayerListener;
import com.jungle.mediaplayer.base.VideoInfo;
import com.jungle.mediaplayer.player.BaseMediaPlayer;
import com.jungle.mediaplayer.player.SystemImplMediaPlayer;
import com.jungle.mediaplayer.player.render.SurfaceViewMediaRender;

public class SimplePlayVideoActivity extends AppCompatActivity {

    private static final String EXTRA_VIDEO_URL = "extra_video_url";


    public static void start(Context context, String url) {
        Intent intent = new Intent(context, SimplePlayVideoActivity.class);
        intent.putExtra(EXTRA_VIDEO_URL, url);
        context.startActivity(intent);
    }


    private BaseMediaPlayer mMediaPlayer;
    private SurfaceView mSurfaceView;
    private String mVideoUrl;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.simple_play_video);
        setContentView(R.layout.activity_simple_play_video);

        initMediaPlayer();

        mVideoUrl = getIntent().getStringExtra(EXTRA_VIDEO_URL);
        TextView urlView = (TextView) findViewById(R.id.video_url);
        if (!TextUtils.isEmpty(mVideoUrl)) {
            urlView.setText(mVideoUrl);
            mMediaPlayer.play(new VideoInfo(mVideoUrl));
        } else {
            urlView.setText(R.string.media_url_error);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mMediaPlayer.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaPlayer.destroy();
    }

    private void initMediaPlayer() {
        mSurfaceView = (SurfaceView) findViewById(R.id.surface_view);
        mSurfaceView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mMediaPlayer.updateMediaRenderSize(
                                mSurfaceView.getMeasuredWidth(),
                                mSurfaceView.getMeasuredHeight(),
                                false);
                    }
                });

        mMediaPlayer = new SystemImplMediaPlayer(this, new SurfaceViewMediaRender(mSurfaceView));
        mMediaPlayer.addPlayerListener(new SimpleMediaPlayerListener() {
            @Override
            public void onPlayComplete() {
                Toast.makeText(SimplePlayVideoActivity.this,
                        R.string.play_complete, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
