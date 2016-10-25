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
import android.view.View;

public abstract class MediaRender {

    public interface Listener {
        void onRenderCreated();

        void onRenderDestroyed();
    }


    protected boolean mIsRenderValid;
    protected Listener mListener;


    public abstract void initRender();

    public abstract View getRenderView();

    public abstract void prepareMediaRender(MediaPlayer mediaPlayer);

    public abstract void mediaRenderChanged(MediaPlayer mediaPlayer);

    public abstract boolean isRenderCreating();

    public boolean isRenderValid() {
        return mIsRenderValid;
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }
}
