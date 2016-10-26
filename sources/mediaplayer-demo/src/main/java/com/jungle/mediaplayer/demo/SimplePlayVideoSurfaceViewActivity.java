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
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.SurfaceView;
import com.jungle.mediaplayer.player.render.MediaRender;
import com.jungle.mediaplayer.player.render.SurfaceViewMediaRender;

public class SimplePlayVideoSurfaceViewActivity extends BaseSimplePlayVideoActivity {

    public static void start(Context context, String url) {
        start(context, url, SimplePlayVideoSurfaceViewActivity.class);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.simple_play_video_surface);
    }

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_simple_play_video_surface;
    }

    @Override
    protected MediaRender createMediaRender() {
        SurfaceView surfaceView = (SurfaceView) findViewById(R.id.surface_view);
        return new SurfaceViewMediaRender(surfaceView);
    }
}
