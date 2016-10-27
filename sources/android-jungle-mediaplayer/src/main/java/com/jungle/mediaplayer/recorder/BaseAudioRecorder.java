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

package com.jungle.mediaplayer.recorder;

import android.content.Context;

public abstract class BaseAudioRecorder {

    protected String mOutputFile;
    protected RecorderListener mListener;
    protected RecordPermissionRequester mPermissionRequester;


    public BaseAudioRecorder(RecorderListener listener, RecordPermissionRequester requester) {
        mListener = listener;
        mPermissionRequester = requester;
    }

    public void setListener(RecorderListener listener) {
        mListener = listener;
    }

    public void setOutputFile(String outputFile) {
        mOutputFile = outputFile;
    }

    public String getOutputFilePath() {
        return mOutputFile;
    }

    public abstract boolean isRecording();

    public abstract boolean startRecord(Context context);

    public abstract boolean stopRecord();

    public abstract void destroy();
}
