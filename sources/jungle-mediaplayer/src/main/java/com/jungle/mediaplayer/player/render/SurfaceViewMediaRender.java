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

package com.jungle.mediaplayer.player.render;

import android.media.MediaPlayer;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class SurfaceViewMediaRender extends MediaRender
        implements SurfaceHolder.Callback {

    private SurfaceView mSurfaceView;


    public SurfaceViewMediaRender(SurfaceView surfaceView) {
        mSurfaceView = surfaceView;
    }

    @Override
    public void initRender() {
        SurfaceHolder videoHolder = mSurfaceView.getHolder();
        videoHolder.addCallback(this);
        mIsRenderValid = !videoHolder.isCreating();
    }

    public SurfaceView getSurfaceView() {
        return mSurfaceView;
    }

    @Override
    public View getRenderView() {
        return getSurfaceView();
    }

    @Override
    public void prepareMediaRender(MediaPlayer mediaPlayer) {
        mediaPlayer.setDisplay(mSurfaceView.getHolder());
    }

    @Override
    public void mediaRenderChanged(MediaPlayer mediaPlayer) {
        mediaPlayer.setDisplay(mSurfaceView.getHolder());
    }

    @Override
    public boolean isRenderCreating() {
        SurfaceHolder holder = mSurfaceView.getHolder();
        return holder == null || holder.isCreating();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mIsRenderValid = true;
        mListener.onRenderCreated();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mIsRenderValid = false;
        mListener.onRenderDestroyed();
    }
}
