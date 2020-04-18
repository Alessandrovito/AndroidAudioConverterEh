# AndroidAudioConverterEh

> Convert audio and video files inside your Android app easily with enhanced support of latest SDk and with different architectures
. This is a wrapper of [FFmpeg-Android-Java](https://github.com/WritingMinds/ffmpeg-android-java) lib.

Supported Audio formats:
* AAC
* MP3
* M4A
* WMA
* WAV
* FLAC

Supported Video formats:
* MOW
* MP4

## Supported architecture
* arm64-v8a
* armeabi-v7a
* x86
* x86_64

## How To Use

1 - Add this permission into your `AndroidManifest.xml` and [request in Android 8.0+](https://developer.android.com/training/permissions/requesting.html)
```xml
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
```

2 - Load the lib inside your `Application` class
```java
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AndroidAudioConverter.load(this, new ILoadCallback() {
            @Override
            public void onSuccess() {
                // Great!
            }
            @Override
            public void onFailure(Exception error) {
                // FFmpeg is not supported by device
            }
        });
    }
}
```

3 - Convert audio files async
```java
File flacFile = new File(Environment.getExternalStorageDirectory(), "my_audio.flac");
IConvertCallback callback = new IConvertCallback() {
    @Override
    public void onSuccess(File convertedFile) {
        // So fast? Love it!
    }
    @Override
    public void onFailure(Exception error) {
        // Oops! Something went wrong
    }
};
AndroidAudioConverter.with(this)
    // Your current audio file
    .setFile(flacFile)  
    
    // Your desired audio format 
    .setFormat(AudioFormat.M4A)
    
    // An callback to know when conversion is finished
    .setCallback(callback)
    
    // Start conversion
    .convert();
```

## Import to your project
Put this into your `app/build.gradle`:
```
repositories {
  maven {
    url "https://jitpack.io"
  }
}

dependencies {
  compile 'com.github.Alessandrovito:AndroidAudioConverterEh:1.3'
}
```

## Dependencies
* [FFmpeg-Android-Java](https://github.com/WritingMinds/ffmpeg-android-java) This dependency is submitted as a module inside the library.

## License
```
The MIT License (MIT)

Copyright (c) 2019

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
```
