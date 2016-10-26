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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private static final String MEDIA_URL =
            "http://200000594.vod.myqcloud.com/200000594_1617cc56708f11e596723b988fc18469.f20.mp4";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.main_activity_title);
        setContentView(R.layout.activity_main);

        findViewById(R.id.play_video_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayVideoActivity.start(MainActivity.this, MEDIA_URL);
            }
        });

        findViewById(R.id.play_audio_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayAudioActivity.start(MainActivity.this, MEDIA_URL);
            }
        });

        findViewById(R.id.record_audio_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(RecordAudioActivity.class);
            }
        });
    }

    private void startActivity(Class<? extends Activity> clazz) {
        startActivity(new Intent(this, clazz));
    }
}
