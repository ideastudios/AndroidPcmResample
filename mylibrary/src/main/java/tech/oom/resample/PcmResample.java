package tech.oom.resample;

import android.media.AudioFormat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import vavi.sound.pcm.resampling.ssrc.SSRC;

public class PcmResample {
    /**
     * 将pcm文件重新采样  只支持pcm文件 转换后的通道数和原始通道数一致
     *
     * @param srcFileConfig    源PCM文件的配置 {@link AudioFileConfig}
     * @param targetFileConfig 转换后目标PCM文件的配置 {@link AudioFileConfig}
     * @param listener         转换的监听
     */
    public static void resample(final AudioFileConfig srcFileConfig, final AudioFileConfig targetFileConfig, final ReSampleListener listener) {
        String srcPath = srcFileConfig.getFilePath();
        String objPath = targetFileConfig.getFilePath();
        File BeforeSampleChangedFile = new File(srcPath);
        File SampleChangedFile = new File(objPath);

        int srcChannel = 1;

        if (srcFileConfig.getChannelConfig() == AudioFormat.CHANNEL_IN_MONO) {
            srcChannel = 1;
        } else if (srcFileConfig.getChannelConfig() == AudioFormat.CHANNEL_IN_STEREO) {
            srcChannel = 2;
        } else {
            if (listener != null) {
                listener.onResampleError("illegal channel config");
            }
            throw new IllegalArgumentException("Error: The value AudioFormat in AudioFileConfig should be AudioFormat.CHANNEL_IN_MONO or AudioFormat.CHANNEL_IN_STEREO");
        }
        int srcByte = 1;

        if (srcFileConfig.getAudioFormat() == AudioFormat.ENCODING_PCM_8BIT) {
            srcByte = 1;
        } else if (srcFileConfig.getAudioFormat() == AudioFormat.ENCODING_PCM_16BIT) {
            srcByte = 2;
        } else {
            if (listener != null) {
                listener.onResampleError("illegal audio format");
            }
            throw new IllegalArgumentException("Error :only AudioFormat.ENCODING_PCM_8BIT and AudioFormat.ENCODING_PCM_16BIT are supported.");
        }

        try {
            final FileInputStream fis = new FileInputStream(BeforeSampleChangedFile);
            final FileOutputStream fos = new FileOutputStream(SampleChangedFile);
            final int finalSrcByte = srcByte;
            final int finalSrcChannel = srcChannel;
            Runnable resampleRunnable = new Runnable() {
                @Override
                public void run() {
                    try {

                        if (listener != null) {
                            listener.onResampleStart();
                        }
                        new SSRC(fis, fos, srcFileConfig.getSampleRate(), targetFileConfig.getSampleRate(), finalSrcByte, finalSrcByte, finalSrcChannel, Integer.MAX_VALUE, 0, 0, true);
                        if (listener != null) {
                            listener.onResampleFinish(targetFileConfig);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        if (listener != null) {
                            listener.onResampleError(e.toString());
                        }
                    }

                }
            };
            new Thread(resampleRunnable).start();
        } catch (FileNotFoundException e) {
            if (listener != null) {
                listener.onResampleError(e.toString());
            }
            e.printStackTrace();
        }
    }

    /**
     * 录音的配置信息  默认配置为8K采样率 单通道 16位
     * <pre>
     *      audioSource = MediaRecorder.AudioSource.MIC;
     *      sampleRate = AudioFileConfig.SAMPLE_RATE_8K_HZ;
     *      channelConfig = AudioFormat.CHANNEL_IN_MONO;
     *      audioFormat = AudioFormat.ENCODING_PCM_16BIT;
     * </pre>
     */
    public static class AudioFileConfig {
        public static final int SAMPLE_RATE_44K_HZ = 44100;
        public static final int SAMPLE_RATE_32K_HZ = 32000;
        public static final int SAMPLE_RATE_22K_HZ = 22050;
        public static final int SAMPLE_RATE_16K_HZ = 16000;
        public static final int SAMPLE_RATE_11K_HZ = 11025;
        public static final int SAMPLE_RATE_8K_HZ = 8000;
        private int sampleRate = SAMPLE_RATE_8K_HZ;
        private int channelConfig = AudioFormat.CHANNEL_IN_MONO;
        private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
        private String filePath;

        /**
         * 录音配置的构造方法
         *
         * @param filePath      the filePath of file
         * @param sampleRate    the sample rate expressed in Hertz. {@link AudioFileConfig#SAMPLE_RATE_44K_HZ} is Recommended ,{@link AudioFileConfig#SAMPLE_RATE_32K_HZ},
         *                      {@link AudioFileConfig#SAMPLE_RATE_22K_HZ},{@link AudioFileConfig#SAMPLE_RATE_16K_HZ},
         *                      {@link AudioFileConfig#SAMPLE_RATE_11K_HZ},{@link AudioFileConfig#SAMPLE_RATE_8K_HZ}
         * @param channelConfig describes the configuration of the audio channels.
         *                      See {@link AudioFormat#CHANNEL_IN_MONO} and
         *                      {@link AudioFormat#CHANNEL_IN_STEREO}.  {@link AudioFormat#CHANNEL_IN_MONO} is guaranteed
         *                      to work on all devices.
         * @param audioFormat   the format in which the audio data is to be returned.
         *                      See {@link AudioFormat#ENCODING_PCM_8BIT}, {@link AudioFormat#ENCODING_PCM_16BIT}
         */
        public AudioFileConfig(String filePath, int sampleRate, int channelConfig, int audioFormat) {
            this.sampleRate = sampleRate;
            this.channelConfig = channelConfig;
            this.audioFormat = audioFormat;
            this.filePath = filePath;
        }

        /**
         * 录音配置的构造方法
         */
        public AudioFileConfig() {

        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public int getSampleRate() {
            return sampleRate;
        }

        /**
         * @param sampleRate set the sample rate  it can be set设置采样率，可以是以下的任何一个  {@link AudioFileConfig#SAMPLE_RATE_44K_HZ}
         *                   {@link AudioFileConfig#SAMPLE_RATE_22K_HZ},{@link AudioFileConfig#SAMPLE_RATE_16K_HZ},
         *                   {@link AudioFileConfig#SAMPLE_RATE_11K_HZ},{@link AudioFileConfig#SAMPLE_RATE_8K_HZ}
         */
        public AudioFileConfig setSampleRate(int sampleRate) {
            this.sampleRate = sampleRate;
            return this;
        }

        public int getChannelConfig() {
            return channelConfig;
        }

        /**
         * @param channelConfig set the configuration of the audio channels.设置当前录音的通道数，可以是
         *                      {@link AudioFormat#CHANNEL_IN_MONO} 单通道也可以是
         *                      {@link AudioFormat#CHANNEL_IN_STEREO}双通道.
         */
        public AudioFileConfig setChannelConfig(int channelConfig) {
            this.channelConfig = channelConfig;
            return this;
        }

        public int getAudioFormat() {
            return audioFormat;
        }

        /**
         * @param audioFormat set the format in which the audio data is to be returned. 设置当前录音的采样位数 可以是
         *                    {@link AudioFormat#ENCODING_PCM_8BIT} 8位 或者{@link AudioFormat#ENCODING_PCM_16BIT} 16位
         */
        public AudioFileConfig setAudioFormat(int audioFormat) {
            this.audioFormat = audioFormat;
            return this;
        }


    }

}
