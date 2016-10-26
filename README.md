# android-jungle-mediaplayer 简介

### 1、简介

`android-jungle-mediaplayer` 是 Android 平台上一款多媒体播放/语音录制的库。使用它你可以很方便的实现语音、视频播放功能。有以下优点：

- 功能齐备，可自定制播放器、播放组件；
- 内置实现了一个播放器，可以不加修改使用；
- 视频播放支持 `SurfaceView/TextureView` 渲染；
- 提供语音录制组件。

**内置播放器支持以下功能：**

- 手势（双击暂停/回复播放；水平滑动进度跳转；左侧上下滑动调节亮度；右侧上下滑动调节音量）
- 支持全屏/半屏播放；
- 支持屏幕旋转自动识别，并自动切换全屏/半屏；
- 可定制。

**具体实现原理请参考我的系列博文：**

- [【Android】 从头搭建视频播放器（1）——概述](http://blog.csdn.net/arnozhang12/article/details/48731443)
- [【Android】 从头搭建视频播放器（2）——SystemMediaPlayerImpl](http://blog.csdn.net/arnozhang12/article/details/48734139)
- [【Android】 从头搭建视频播放器（3）——手势检测 & 控制](http://blog.csdn.net/arnozhang12/article/details/48735683)
- [【Android】 从头搭建视频播放器（4）——屏幕旋转处理](http://blog.csdn.net/arnozhang12/article/details/48736147)
- [【Android】 从头搭建视频播放器（5）——将所有放在一起](http://blog.csdn.net/arnozhang12/article/details/48736363)

### 2、使用方法

```
compile "com.jungle.mediaplayer:android-jungle-mediaplayer:1.0"
```

### 3、Demo 截图

![播放截图](https://github.com/arnozhang/android-jungle-mediaplayer/blob/master/docs/demo_image.jpg?raw=true)

### 3、组件介绍

|组件|功能|
|---|---|
|BaseMediaPlayerInterface|MediaPlayer 的通用接口|
|BaseMediaPlayerListener|MediaPlayer 的 Listener|
|AudioFocusPlayerListener|具有 **AudioFocus** 功能的 MediaPlayer Listener|
|SurfaceViewMediaRender|利用 **SurfaceView** 渲染的 Render|
|TextureViewMediaRender|利用 **TextureView** 渲染的 Render。 **SDK_VERSION >= 14**|
|BaseMediaPlayer|具体的 MediaPlayer 基类，可以派生它用第三方播放器实现具体功能|
|SystemImplMediaPlayer|使用系统 **android.media.MediaPlayer** 作为实现的 MediaPlayer|
|BaseAudioRecorder|语音录制基类，可以派生它用第三方录制组件实现具体功能|
|SystemImplAudioRecorder|使用系统 **android.media.MediaRecorder** 作为实现的 AudioRecorder|
|JungleMediaPlayer|包装了 UI 等用户交互的播放器|

### 4、使用方法

这里只展示一些代码片段，**具体使用方法请参考项目中的 Demo 代码**。

> 需要注意的是，在 Activity 销毁的时候，务必调用播放器组件的 **`destroy`** 方法进行销毁播放器。
>
> 如果播放网络视频或语音，需要在 `Android.manifest` 中声明 **`android.permission.INTERNET`** 权限。

#### 5.1、视频播放

```java
JungleMediaPlayer mMediaPlayer;
// ...

mMediaPlayer.setPlayerListener(new SimpleJungleMediaPlayerListener() {
    @Override
    public void onTitleBackClicked() {
        if (mMediaPlayer.isFullscreen()) {
            mMediaPlayer.switchFullScreen(false);
            return;
        }

        finish();
    }
});

// play
String videoUrl = "http://xxx.xx/video.mp4";
mMediaPlayer.playMedia(new VideoInfo(videoUrl));
```

#### 5.2、语音播放

```java
BaseMediaPlayer mMediaPlayer = new SystemImplMediaPlayer(context);
mMediaPlayer.addPlayerListener(new SimpleMediaPlayerListener() {

    @Override
    public void onStartPlay() {
    }

    @Override
    public void onLoadFailed() {
    }

    // ...
});

// play
mMediaPlayer.play(new VideoInfo(mAudioUrl));
```

> 如果语音播放时需要处理 **`AudioFocus`** 逻辑，请使用 **`AudioFocusPlayerListener`** 这个 Listener。

#### 5.3、语音录制

```java
final static int REQUEST_CODE_AUDIO_PERMISSIONS = 100;

Callback mPermissionCallback;
BaseAudioRecorder mAudioRecorder;

private void initRecorder() {
    RecordPermissionRequester permissionRequester = new RecordPermissionRequester() {
        @Override
        public void requestRecordPermission(String[] permissions, Callback callback) {
            mPermissionCallback = callback;
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_AUDIO_PERMISSIONS);
        }
    };

    mAudioRecorder = new SystemImplAudioRecorder(new RecorderListener() {
        @Override
        public void onError(Error error) {
            showToast("Record Audio ERROR! " + error.toString());
        }

        @Override
        public void onStartRecord() {
            // do started logic.
        }

        @Override
        public void onStopRecord() {
            // do stopped logic.
        }
    }, permissionRequester);
}

private void startRecord() {
    String filePath = getAudioFilePath();
    mAudioRecorder.setOutputFile(filePath);
    mAudioRecorder.startRecord(getContext());
}

@Override
public void onRequestPermissionsResult(
        int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == REQUEST_CODE_AUDIO_PERMISSIONS && mPermissionCallback != null) {
        mPermissionCallback.onPermissionRequested();
    }
}
```

> 语音录制需要在 `Android.manifest` 中声明 **`android.permission.RECORD_AUDIO`** 权限。
>
> 高版本系统中，可能额外需要动态申请 `android.permission.RECORD_AUDIO` 权限。上面的 Demo 例子中作了示范。

<br>

## License

```
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
```