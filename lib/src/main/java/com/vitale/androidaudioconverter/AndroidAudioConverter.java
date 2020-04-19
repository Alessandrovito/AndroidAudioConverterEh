package com.vitale.androidaudioconverter;

import android.content.Context;
import android.provider.MediaStore;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpegLoadBinaryResponseHandler;
import com.vitale.androidaudioconverter.callback.IConvertCallback;
import com.vitale.androidaudioconverter.callback.ILoadCallback;
import com.vitale.androidaudioconverter.model.AudioFormat;
import com.vitale.androidaudioconverter.model.VideoFormat;

import java.io.File;
import java.io.IOException;

public class AndroidAudioConverter {

    private static boolean loaded;

    private Context context;
    private File audioFile;
    private AudioFormat format = null;
    private VideoFormat videoFormat = null;
    private String videoArtist;
    private String videoTitle;
    private String videoAlbum;
    private String videoDescription;

    private IConvertCallback callback;

    private AndroidAudioConverter(Context context){
        this.context = context;
    }

    public static boolean isLoaded(){
        return loaded;
    }

    public static void load(Context context, final ILoadCallback callback){
        try {
            FFmpeg.getInstance(context).loadBinary(new FFmpegLoadBinaryResponseHandler() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onSuccess() {
                            loaded = true;
                            callback.onSuccess();
                        }

                        @Override
                        public void onFailure() {
                            loaded = false;
                            callback.onFailure(new Exception("Failed to loaded FFmpeg lib"));
                        }

                        @Override
                        public void onFinish() {

                        }
                    });
        } catch (Exception e){
            loaded = false;
            callback.onFailure(e);
        }
    }

    public static AndroidAudioConverter with(Context context) {
        return new AndroidAudioConverter(context);
    }

    public AndroidAudioConverter setFile(File originalFile) {
        this.audioFile = originalFile;
        return this;
    }

    public AndroidAudioConverter setFormat(AudioFormat format) {
        this.format = format;
        return this;
    }

    public AndroidAudioConverter setVideoFormat(VideoFormat videoFormat) {
        this.videoFormat = videoFormat;
        return this;
    }

    public AndroidAudioConverter setVideoArtist(String videoArtist) {
        this.videoArtist = videoArtist;
        return this;
    }

    public AndroidAudioConverter setVideoAlbum(String videoAlbum) {
        this.videoAlbum = videoAlbum;
        return this;
    }

    public AndroidAudioConverter setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
        return this;
    }

    public AndroidAudioConverter setVideoDescription(String videoDescription) {
        this.videoDescription = videoDescription;
        return this;
    }



    public AndroidAudioConverter setCallback(IConvertCallback callback) {
        this.callback = callback;
        return this;
    }

    public void convert() {
        if(!isLoaded()){
            callback.onFailure(new Exception("FFmpeg not loaded"));
            return;
        }
        if(audioFile == null || !audioFile.exists()){
            callback.onFailure(new IOException("File not exists"));
            return;
        }
        if(!audioFile.canRead()){
            callback.onFailure(new IOException("Can't read the file. Missing permission?"));
            return;
        }


        if (format == null && videoFormat == null) {
            callback.onFailure(new IOException("File audio or video not provided"));
            return;
        }

        final File convertedFile = (format != null) ? getConvertedFile(audioFile, format) : getConvertedFile(audioFile, videoFormat);
        String[] cmd = null;

        /*
        * 5.4 Main options
        * The format is normally auto detected for input files and guessed from the file extension for output files,
        *  so this option is not needed in most cases.
        *
        * -i url (input)
        *    input file url
        *
        * For example audio
        *   ffmpeg -y -i INPUT OUTPUT
        *
        * For example video
        *   ffmpeg - y -i INPUT OUTPUT
        *
        *
        * */

        if (format != null) {
            cmd = new String[]{"-y", "-i", audioFile.getPath(), convertedFile.getPath()};
        } else if (videoFormat != null) {
            String metadata = "-metadata";
            String metadataArtist = "artist=\""+videoArtist+"\"";
            String metadataAlbum = "album=\""+ videoAlbum+"\"";
            String metadataTile= "title=\""+ videoTitle+"\"";
            String metadataDescription = "description=\""+ videoDescription+"\"";


            System.out.println("Adding metadata to video encoding");

            cmd = new String[]{"-y", "-i", audioFile.getPath(),
                    metadata,metadataArtist,
                    metadata,metadataAlbum,
                    metadata,metadataTile,
                    metadata,metadataDescription,
                    convertedFile.getPath()};
        }

        try {
            FFmpeg.getInstance(context).execute(cmd, new FFmpegExecuteResponseHandler() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onProgress(String message) {

                        }

                        @Override
                        public void onSuccess(String message) {
                            callback.onSuccess(convertedFile);
                        }

                        @Override
                        public void onFailure(String message) {
                            callback.onFailure(new IOException(message));
                        }

                        @Override
                        public void onFinish() {

                        }
                    });
        } catch (Exception e){
            callback.onFailure(e);
        }
    }

    private static File getConvertedFile(File originalFile, AudioFormat format){
        String[] f = originalFile.getPath().split("\\.");
        String filePath = originalFile.getPath().replace(f[f.length - 1], format.getFormat());
        return new File(filePath);
    }

    private static File getConvertedFile(File originalFile, VideoFormat format){
        String[] f = originalFile.getPath().split("\\.");
        String filePath = originalFile.getPath().replace(f[f.length - 1], format.getFormat());
        return new File(filePath);
    }
}