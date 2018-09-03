package tech.oom.androidpcmresample;

import android.app.ProgressDialog;
import android.media.AudioFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import tech.oom.resample.PcmResample;
import tech.oom.resample.PcmToWavUtil;
import tech.oom.resample.ReSampleListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ProgressDialog dialog;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        copyAssetsToFile();
        findViewById(R.id.resample_btn).setOnClickListener(this);
        dialog = new ProgressDialog(this);
        dialog.setMessage("resample in progress,please waite for a while");
    }

    private void copyAssetsToFile() {
        try {
            InputStream open = getAssets().open("50waystosaygoodbye.pcm");
            FileOutputStream fileOutputStream = new FileOutputStream(new File(getExternalFilesDir(null), "50waystosaygoodbye.pcm"));
            byte[] aar = new byte[1024 * 3];
            int len = 0;
            while ((len = open.read(aar)) != -1) {
                fileOutputStream.write(aar, 0, len);
            }
            open.close();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void resample() {
        //需要重新采样的源文件路径
        File file = new File(getExternalFilesDir(null), "50waystosaygoodbye.pcm");
        //重新采样后生成的文件路径
        File targetWaveFile = new File(getExternalFilesDir(null), "50waystosaygoodbye_8.pcm");
        //源文件的采样率、通道数、采样位数
        PcmResample.AudioFileConfig srcFileConfig = new PcmResample.AudioFileConfig(file.getAbsolutePath(), 44100, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);
        //生成文件的采样率、通道数(必须和源文件一样)、采样位数（必须和源文件一样）
        PcmResample.AudioFileConfig targetFileConfig = new PcmResample.AudioFileConfig(targetWaveFile.getAbsolutePath(), 8000, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);
        //开始重新采样 采样回调在工作线程 非UI线程
        PcmResample.resample(srcFileConfig, targetFileConfig, new ReSampleListener() {
            @Override
            public void onResampleError(String error) {
                System.out.println("error");
            }

            @Override
            public void onResampleStart() {
                System.out.println("start");

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Resample start", Toast.LENGTH_SHORT).show();
                        dialog.show();
                    }
                });
            }

            @Override
            public void onResampleFinish(final PcmResample.AudioFileConfig targetConfig) {
                System.out.println("finish");
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Finish !The saved file path  " + targetConfig.getFilePath(), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    private void transferPcmToWav() {
        File file = new File(getExternalFilesDir(null), "news.pcm");
        File targetWaveFile = new File(getExternalFilesDir(null), "news.wav");
        PcmResample.AudioFileConfig audioFileConfig = new PcmResample.AudioFileConfig(file.getAbsolutePath(), 44100, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        PcmToWavUtil.transferPcmToWav(audioFileConfig, targetWaveFile.getAbsolutePath());
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.resample_btn:
                resample();
                break;
        }
    }
}
