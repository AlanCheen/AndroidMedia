package me.yifeiyuan.androidmedia;

import android.content.Context;
import android.hardware.Camera;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

import java.util.List;

/**
 * Created by mingjue on 2017/8/30.
 */
public class CameraManager {

    private static final String TAG = "CameraManager";

    private Camera mCamera;

    private final Object mLock = new Object();

    private static CameraManager sInstance;

    //默认后置摄像头
    private int mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;

    private int mOrientation = 90;//default

    /**
     * 拍照尺寸
     */
    public Camera.Size mCameraPictureSize;

    private CameraManager() {}

    public static CameraManager getInstance() {
        if (sInstance == null) {
            synchronized (CameraManager.class) {
                if (sInstance == null) {
                    sInstance = new CameraManager();
                }
            }
        }
        return sInstance;
    }

    public @Nullable Camera getCamera() {
        return mCamera;
    }

    /**
     * @param cameraId
     * @see Camera.CameraInfo.CAMERA_FACING_BACK
     * @see Camera.CameraInfo.CAMERA_FACING_FRONT
     *
     * @return
     */
    public CameraManager setCameraId(int cameraId) {
        this.mCameraId = cameraId;
        return sInstance;
    }

    public int getCameraId() {
        return mCameraId;
    }

    public CameraManager open(Context context) {
        int num = Camera.getNumberOfCameras();
        synchronized (mLock) {
            try {
                release();
                if (num > 0) {
                    mCamera = Camera.open(mCameraId);
                } else {
                    mCamera = Camera.open();
                }
                mOrientation = setCameraDisplayOrientation(context, mCameraId, mCamera);

                Camera.Parameters parameters = mCamera.getParameters();

                List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sInstance;
    }

    public void release() {
        synchronized (mLock) {
            if (mCamera != null) {
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }
        }
    }

    /**
     * call open first
     * @param enable 是否开启
     * @return
     */
    public CameraManager switchFlash(boolean enable) {
        synchronized (mLock) {
            if (mCamera != null) {

                Camera.Parameters parameters = mCamera.getParameters();
                List<String> modes = parameters.getSupportedFlashModes();

                if (enable) {
                    //turn on
                    String mode = findMatch(modes, Camera.Parameters.FLASH_MODE_TORCH,
                            Camera.Parameters.FLASH_MODE_ON,
                            Camera.Parameters.FLASH_MODE_RED_EYE,
                            Camera.Parameters.FLASH_MODE_AUTO);
                    if (null != mode) {
                        parameters.setFlashMode(mode);
                        mCamera.setParameters(parameters);
                    }
                } else {
                    // turn off
                    String mode = findMatch(modes, Camera.Parameters.FLASH_MODE_OFF,
                            Camera.Parameters.FLASH_MODE_AUTO);
                    if (null != mode) {
                        parameters.setFlashMode(mode);
                        mCamera.setParameters(parameters);
                    }
                }
            }else{
                Log.e(TAG, "switchFlash: camera is null,call open first");
            }
        }
        return sInstance;
    }

    public CameraManager setPreviewCallback(Camera.PreviewCallback callback) {
        synchronized (mLock) {
            if (mCamera != null) {
                mCamera.setPreviewCallback(callback);
            }
        }
        return sInstance;
    }

    private String findMatch(List<String> list, String... target) {
        if (null == list) {
            return null;
        }
        for (final String s : target) {
            if (list.contains(s)) {
                return s;
            }
        }
        return null;
    }

    public CameraManager startPreview() {
        synchronized (mLock) {
            if (null != mCamera) {
                mCamera.startPreview();
            }else{
                Log.e(TAG, "startPreview: camera is null,call open first");
            }
        }
        return sInstance;
    }

    public void setupPreviewSize(Camera camera){

    }

    void setting() {
        Camera.Parameters parameters = mCamera.getParameters();
    }


    /**
     * @param context
     * @param cameraId
     * @param camera
     *
     * @return orientation
     */
    public static int setCameraDisplayOrientation(Context context,
                                                  int cameraId,
                                                  android.hardware.Camera camera) {

        if (null == context || null == camera) {
            return -1;
        }
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
        return result;
    }
}
