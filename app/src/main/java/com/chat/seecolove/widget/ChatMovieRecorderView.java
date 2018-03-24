package com.chat.seecolove.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.chat.seecolove.R;
import com.chat.seecolove.view.activity.MyApplication;


public class ChatMovieRecorderView extends LinearLayout implements MediaRecorder.OnErrorListener {
    private String videoPath = Environment.getExternalStorageDirectory().getPath() + "/seex_auth2.mp4";
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private TextView time_num;
    private MediaRecorder mMediaRecorder;
    private Camera mCamera;
    private Timer mTimer;// 计时器
    private OnRecordFinishListener mOnRecordFinishListener;// 录制完成回调接口

    private int mWidth;// 视频分辨率宽度
    private int mHeight;// 视频分辨率高度
    private boolean isOpenCamera;// 是否一开始就打开摄像头
    private int mRecordMaxTime;// 一次拍摄最长时间
    private int mTimeCount;// 时间计数
    private File mVecordFile = null;// 文件

    public ChatMovieRecorderView(Context context) {
        this(context, null);
    }

    public ChatMovieRecorderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    private int swidth, sheight;

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public ChatMovieRecorderView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        swidth = wm.getDefaultDisplay().getWidth();
        sheight = wm.getDefaultDisplay().getHeight();
        mWidth = 640;
        mHeight = 480;
        isOpenCamera = true;
        mRecordMaxTime = 11;

        LayoutInflater.from(context).inflate(R.layout.chat_moive_recorder_view, this);
        time_num = (TextView) findViewById(R.id.time_num);
        mSurfaceView = (SurfaceView) findViewById(R.id.surfaceview);

        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(new CustomCallBack());
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private class CustomCallBack implements SurfaceHolder.Callback {

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if (!isOpenCamera)
                return;
            try {

                initCamera();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (!isOpenCamera)
                return;
            freeCameraResource();
        }

    }

    private void initCamera() throws IOException {
        if (mCamera != null) {
            freeCameraResource();
        }
        try {
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        } catch (Exception e) {
            e.printStackTrace();
            freeCameraResource();
        }
        if (mCamera == null)
            return;
        setCameraParams();
        mCamera.setDisplayOrientation(90);
        mCamera.setPreviewDisplay(mSurfaceHolder);
        mCamera.startPreview();
        mCamera.unlock();
    }

    private void setCameraParams() {
        if (mCamera != null) {
            Camera.Parameters params = mCamera.getParameters();
            params.set("orientation", "portrait");

            List<Camera.Size> supportedPreviewSizes
                    = params.getSupportedVideoSizes();
            Camera.Size previewSize = null;
            if (supportedPreviewSizes != null &&
                    supportedPreviewSizes.size() > 0) {
                previewSize = getOptimalPreviewSize(supportedPreviewSizes);
                params.setPreviewSize(previewSize.width, previewSize.height);
                mWidth = previewSize.width;
                mHeight = previewSize.height;
                mCamera.setParameters(params);
            }

        }
        intSuffaceSize(mHeight, mWidth);
    }


    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) MyApplication.screenWidth / MyApplication.screenHeigth;
        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = MyApplication.screenHeigth;

        // Try to find an size match aspect ratio and size
        for (Camera.Size size : sizes) {
            if (size.width <= 700) {
                optimalSize = size;
                break;
            }
            if (size.width <= 600) {
                optimalSize = size;
                break;
            }
            if (size.width <= 500) {
                optimalSize = size;
                break;
            }
            if (size.width <= 400) {
                optimalSize = size;
                break;
            }
        }
        return optimalSize;
    }

    private void intSuffaceSize(float width, float height) {
        final int screenHeight = MyApplication.screenHeigth;
        final int screenWidth = MyApplication.screenWidth;
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mSurfaceView
                .getLayoutParams();
        if (screenHeight - height > screenWidth - width) {
            params.width = (int) (width * (screenHeight / height));
            params.height = screenHeight;
        } else {
            params.width = screenWidth;
            params.height = (int) (height * (screenWidth / width));
        }
        params.gravity = Gravity.TOP;
        mSurfaceView.setLayoutParams(params);
        mSurfaceView.requestLayout();
    }

    private void freeCameraResource() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.lock();
            mCamera.release();
            mCamera = null;
        }
    }

    private void initRecord() throws IOException {
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.reset();
        if (mCamera != null)
            mMediaRecorder.setCamera(mCamera);

        mMediaRecorder.setOnErrorListener(this);
        mMediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);// 视频源
//        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 音频源
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);// 音频源
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);// 视频输出格式
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);// 音频格式
        mMediaRecorder.setVideoSize(mWidth, mHeight);// 设置分辨率
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setVideoEncodingBitRate(1 * swidth * sheight);// 设置帧频率
        mMediaRecorder.setOrientationHint(90);
        mMediaRecorder.setOutputFile(videoPath);
        mMediaRecorder.prepare();
        try {
            mMediaRecorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void record(final OnRecordFinishListener onRecordFinishListener) {
        time_num.setVisibility(VISIBLE);
        this.mOnRecordFinishListener = onRecordFinishListener;
        try {
            if (!isOpenCamera)
                initCamera();
            initRecord();
            mTimer = new Timer();
            mTimer.schedule(new TimerTask() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    mRecordMaxTime--;
                    if (mRecordMaxTime != -1) {
                        handler.obtainMessage(0).sendToTarget();
                    }
                    if (-1 == mRecordMaxTime) {// 达到指定时间，停止拍摄
                        stop();
                        if (mOnRecordFinishListener != null)
                            mOnRecordFinishListener.onRecordFinish();
                    }
                }
            }, 0, 1000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    time_num.setText(mRecordMaxTime + "");
                    break;
                case 1:
                    intSuffaceSize(mHeight, mWidth);
                    break;
                default:
                    break;
            }
        }
    };

    public void stop() {
        stopRecord();
        releaseRecord();
        freeCameraResource();
    }

    public void stopRecord() {
        if (mTimer != null)
            mTimer.cancel();
        if (mMediaRecorder != null) {
            // 设置后不会崩
            mMediaRecorder.setOnErrorListener(null);
            mMediaRecorder.setPreviewDisplay(null);
            try {
                mMediaRecorder.stop();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (RuntimeException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void releaseRecord() {
        if (mMediaRecorder != null) {
            mMediaRecorder.setOnErrorListener(null);
            try {
                mMediaRecorder.release();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mMediaRecorder = null;
    }

    public int getTimeCount() {
        return mTimeCount;
    }

    public File getmVecordFile() {
        return mVecordFile;
    }

    public interface OnRecordFinishListener {
        public void onRecordFinish();
    }

    @Override
    public void onError(MediaRecorder mr, int what, int extra) {
        try {
            if (mr != null)
                mr.reset();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
