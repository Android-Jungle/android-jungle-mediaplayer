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

import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

@RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class TextureViewMediaRender extends MediaRender
        implements TextureView.SurfaceTextureListener {

    private TextureView mTextureView;


    public TextureViewMediaRender(TextureView textureView) {
        mTextureView = textureView;
    }

    @Override
    public void initRender() {
        mTextureView.setSurfaceTextureListener(this);
        if (mTextureView.getSurfaceTexture() != null) {
            mListener.onRenderCreated();
        }
    }

    public TextureView getTextureView() {
        return mTextureView;
    }

    @Override
    public View getRenderView() {
        return getTextureView();
    }

    @Override
    public void prepareMediaRender(MediaPlayer mediaPlayer) {
        mediaPlayer.setSurface(new Surface(mTextureView.getSurfaceTexture()));
    }

    @Override
    public void mediaRenderChanged(MediaPlayer mediaPlayer) {
        mediaPlayer.setSurface(new Surface(mTextureView.getSurfaceTexture()));
    }

    @Override
    public boolean isRenderCreating() {
        return mTextureView.getSurfaceTexture() == null;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mIsRenderValid = true;
        mListener.onRenderCreated();
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        mIsRenderValid = false;
        mListener.onRenderDestroyed();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }
}
