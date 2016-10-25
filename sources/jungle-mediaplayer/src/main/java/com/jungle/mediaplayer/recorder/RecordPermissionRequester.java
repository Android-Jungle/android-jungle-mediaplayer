package com.jungle.mediaplayer.recorder;

public interface RecordPermissionRequester {

    public interface Callback {
        void onPermissionRequested();
    }


    void requestRecordPermission(String[] permissions, Callback callback);
}
