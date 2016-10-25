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

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import java.io.File;

public class SystemImplAudioRecorder extends BaseAudioRecorder
        implements RecordPermissionRequester.Callback {

    protected MediaRecorder mMediaRecorder;
    protected boolean mIsRecording = false;


    public SystemImplAudioRecorder(RecorderListener listener) {
        super(listener, null);
    }

    public SystemImplAudioRecorder(RecorderListener listener, RecordPermissionRequester requester) {
        super(listener, requester);
    }

    @Override
    public boolean startRecord(final Context context) {
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            String permission = Manifest.permission.RECORD_AUDIO;
            if (ContextCompat.checkSelfPermission(activity, permission)
                    != PackageManager.PERMISSION_GRANTED) {

                if (mPermissionRequester != null) {
                    mPermissionRequester.requestRecordPermission(new String[]{permission}, this);
                    return true;
                }
            }
        }

        return startRecordInternal();
    }

    @Override
    public void onPermissionRequested() {
        startRecordInternal();
    }

    private void deleteFile(String filePath) {
        try {
            new File(filePath).delete();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private boolean startRecordInternal() {
        if (!TextUtils.isEmpty(mOutputFile)) {
            mListener.onError(RecorderListener.Error.NoAudioOutputFile);
            return false;
        }

        try {
            initRecorder();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        mIsRecording = false;
        deleteFile(mOutputFile);
        mMediaRecorder.setOutputFile(mOutputFile);

        try {
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            mIsRecording = true;

            if (mListener != null) {
                mListener.onStartRecord();
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();

            if (mListener != null) {
                mListener.onError(RecorderListener.Error.StartFailed);
            }
        }

        return false;
    }

    private void initRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.release();
        }

        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {
            @Override
            public void onError(MediaRecorder mr, int what, int extra) {
                mIsRecording = false;

                if (mListener != null) {
                    mListener.onError(RecorderListener.Error.RecordInternalFailed);
                }
            }
        });

        initRecorderFormat();
    }

    protected void initRecorderFormat() {
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mMediaRecorder.setAudioChannels(1);
        mMediaRecorder.setAudioSamplingRate(16000);
        mMediaRecorder.setAudioEncodingBitRate(44100);
    }

    @Override
    public boolean stopRecord() {
        if (mMediaRecorder != null) {
            try {
                mMediaRecorder.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }

            mMediaRecorder = null;
        }

        mIsRecording = false;
        if (mListener != null) {
            mListener.onStopRecord();
        }

        return true;
    }

    @Override
    public boolean isRecording() {
        return mIsRecording;
    }

    @Override
    public void destroy() {
        if (mIsRecording) {
            stopRecord();
        }

        if (mMediaRecorder != null) {
            try {
                mMediaRecorder.stop();
                mMediaRecorder.reset();
                mMediaRecorder.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
