package com.vitale.androidaudioconverter;

import android.content.Context;
import android.provider.MediaStore;

import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpegLoadBinaryResponseHandler;
import com.vitale.androidaudioconverter.callback.IConvertCallback;
import com.vitale.androidaudioconverter.callback.ILoadCallback;
import com.vitale.androidaudioconverter.model.AudioFormat;
import com.vitale.androidaudioconverter.model.VideoEncoder;
import com.vitale.androidaudioconverter.model.VideoFormat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.vitale.androidaudioconverter.model.VideoEncoder.ENCODER_H264;
import static com.vitale.androidaudioconverter.model.VideoEncoder.ENCODER_MPEG4;

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
    private String videoScaleWithFixedWidth;
    private String videoScaleWithFixedHeight;
    private String videoFramerate;
    private String videoBitrateBitPerSec; // in kbits/p

    private VideoEncoder selectEncoder;
    private String encoderOption;

    private String constantRateFactor;




    private IConvertCallback callback;

    private final static String  METADATA = "-metadata";
    private final static String  FILTER = "-filter:v";
    private final static String  SCALE = "scale=";

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

    public AndroidAudioConverter setVideoScaleWithFixedWidth(String videoScaleWithFixedWidth) {
        this.videoScaleWithFixedWidth = videoScaleWithFixedWidth;
        return this;
    }

    public AndroidAudioConverter setVideoScaleWithFixedHeight(String videoScaleWithFixedHeight) {
        this.videoScaleWithFixedHeight = videoScaleWithFixedHeight;
        return this;
    }



    public AndroidAudioConverter setVideoFramerate(String videoFramerate) {
        this.videoFramerate = videoFramerate;
        return this;
    }

    public AndroidAudioConverter setVideoBitrateBitPerSec(String videoBitrateBitPerSec) {
        this.videoBitrateBitPerSec = videoBitrateBitPerSec;
        return this;
    }

    public AndroidAudioConverter setVideoEncoder(VideoEncoder selectEncoder) {
        this.selectEncoder = selectEncoder;
        return this;
    }

    public AndroidAudioConverter setOptionVideoEncoder(String optionVideoEncode) {
        this.encoderOption = optionVideoEncode;
        return this;
    }

    public AndroidAudioConverter setConstantRateFactor(String constantRateFactor) {
        this.constantRateFactor = constantRateFactor;
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

        //Audio support
        if (format != null) {

            cmd = new String[]{"-y", "-i", audioFile.getPath(), convertedFile.getPath()};

        //Video support
        } else if (videoFormat != null) {
            String metadataArtist = "artist="+videoArtist;
            String metadataAlbum = "album="+ videoAlbum;
            String metadataTile= "title="+ videoTitle;
            String metadataDescription = "description="+ videoDescription;

            List<String> ffmpegOptionList = new ArrayList<>(100);

            ffmpegOptionList.add("-y");
            ffmpegOptionList.add("-i");
            ffmpegOptionList.add(audioFile.getPath());
            ffmpegOptionList.add(METADATA);
            ffmpegOptionList.add(metadataArtist);
            ffmpegOptionList.add(METADATA);
            ffmpegOptionList.add(metadataAlbum);
            ffmpegOptionList.add(METADATA);
            ffmpegOptionList.add(metadataTile);
            ffmpegOptionList.add(METADATA);
            ffmpegOptionList.add(metadataDescription);

            if (videoScaleWithFixedWidth != null) {
                String scaleWidth = SCALE+videoScaleWithFixedWidth + ":-2";

                ffmpegOptionList.add(FILTER);
                ffmpegOptionList.add(scaleWidth);
            } else if (videoScaleWithFixedHeight != null) {


                String scaleWidth = SCALE + "-2:"  +videoScaleWithFixedHeight;

                ffmpegOptionList.add(FILTER);
                ffmpegOptionList.add(scaleWidth);

            }


            if (videoFramerate != null) {
                ffmpegOptionList.add("-r");
                ffmpegOptionList.add(videoFramerate);
            }


            if (selectEncoder != null) {
                ffmpegOptionList.add("-c:v");
                ffmpegOptionList.add(selectEncoder.getEncoder());

                if (encoderOption != null && selectEncoder == ENCODER_MPEG4 ) {
                    ffmpegOptionList.add("-q:v");
                    ffmpegOptionList.add(encoderOption);
                }

                if (encoderOption != null && selectEncoder == ENCODER_H264 ) {
                    ffmpegOptionList.add("-preset");
                    ffmpegOptionList.add(encoderOption);

                    if (constantRateFactor != null) {
                        ffmpegOptionList.add("-crf");
                        ffmpegOptionList.add(constantRateFactor);
                    }
                }
            }

            if (videoBitrateBitPerSec != null) {
                String KiloBitRate = videoBitrateBitPerSec + "k";

                ffmpegOptionList.add("-b:v");
                ffmpegOptionList.add(KiloBitRate);
                ffmpegOptionList.add("-maxrate");
                ffmpegOptionList.add(KiloBitRate);
                ffmpegOptionList.add("-bufsize");
                ffmpegOptionList.add(KiloBitRate);
            }

            ffmpegOptionList.add(convertedFile.getPath());

            cmd = ffmpegOptionList.toArray(new String[0]);

            System.out.println("FFmpeg all options : " + Arrays.toString(cmd));

            /*
            if (videoScaleWithFixedWidth == null) {
                cmd = new String[]{"-y", "-i", audioFile.getPath(),
                        METADATA, metadataArtist,
                        METADATA, metadataAlbum,
                        METADATA, metadataTile,
                        METADATA, metadataDescription,
                        convertedFile.getPath()};

                if (videoFramerate != null) {
                            cmd = new String[]{"-y", "-i", audioFile.getPath(),
                            METADATA, metadataArtist,
                            METADATA, metadataAlbum,
                            METADATA, metadataTile,
                            METADATA, metadataDescription,
                            "-r",videoFramerate,
                            convertedFile.getPath()};
                }

            } else {

                String scaleWidth = SCALE+videoScaleWithFixedWidth + ":-2";
                cmd = new String[]{"-y", "-i", audioFile.getPath(),
                        METADATA, metadataArtist,
                        METADATA, metadataAlbum,
                        METADATA, metadataTile,
                        METADATA, metadataDescription,
                        FILTER,scaleWidth,
                        convertedFile.getPath()};

                if (videoFramerate != null) {
                    cmd = new String[]{"-y", "-i", audioFile.getPath(),
                            METADATA, metadataArtist,
                            METADATA, metadataAlbum,
                            METADATA, metadataTile,
                            METADATA, metadataDescription,
                            "-r",videoFramerate,
                            FILTER,scaleWidth,
                            convertedFile.getPath()};
                }
            }

             */


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
        String suffixFileName ="-CONV";
        filePath = filePath.replace(f[f.length - 2], f[f.length - 2] +suffixFileName);
        return new File(filePath);
    }
}