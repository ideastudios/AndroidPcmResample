# AndroidPcmResample
an pcm resample library for Android based on [hutm/JSSRC](https://github.com/hutm/JSSRC)

安卓上pcm数据重新采样的库
如果是wav文件舍弃前44个字节之后 就是pcm数据


## 使用
```java

          File file = new File(getExternalFilesDir(null), "50waystosaygoodbye.pcm");
          //重新采样后生成的文件路径
          File targetWaveFile = new File(getExternalFilesDir(null), "50waystosaygoodbye_8.pcm");
          //源文件的采样率、通道数、采样位数
          PcmResample.AudioFileConfig srcFileConfig = new PcmResample.AudioFileConfig(file.getAbsolutePath(), 44100, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);
          //生成文件的采样率、通道数(必须和源文件一样)、采样位数（必须和源文件一样）
          PcmResample.AudioFileConfig targetFileConfig = new PcmResample.AudioFileConfig(targetWaveFile.getAbsolutePath(), 8000, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);
          //开始重新采样 采样回调在工作线程 非UI线程
          PcmResample.resample(srcFileConfig, targetFileConfig, reSampleListener);
```

## Gradle
[![](https://jitpack.io/v/ideastudios/AndroidPcmResample.svg)](https://jitpack.io/#ideastudios/AndroidPcmResample)
1. Add it in your root build.gradle at the end of repositories:
```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
2. Add the dependency
```
	dependencies {
	        implementation 'com.github.ideastudios:AndroidPcmResample:v1.1.1'
	}

```

## 感谢
[hutm/JSSRC](https://github.com/hutm/JSSRC)