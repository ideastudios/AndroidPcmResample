package tech.oom.resample;

/**
 * 重新采样的监听接口 该回调在工作线程中，非UI线程，不能刷新UI
 */
public interface ReSampleListener {
    /**
     * 重新采样错误
     *
     * @param error 错误信息，仅供开发人员定位问题
     */
    void onResampleError(String error);

    /**
     * 重新采样开始
     */
    void onResampleStart();

    /**
     * 重新采样完成
     *
     * @param targetConfig 重新采样后的语音文件配置信息
     */
    void onResampleFinish(PcmResample.AudioFileConfig targetConfig);

}
