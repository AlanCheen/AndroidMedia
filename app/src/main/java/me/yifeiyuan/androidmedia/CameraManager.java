package me.yifeiyuan.androidmedia;

import android.content.Context;
import android.hardware.Camera;
import android.view.Surface;
import android.view.WindowManager;

import java.util.List;

/**
 * Created by mingjue on 2017/8/30.
 */
public class CameraManager {

    private Camera mCamera;

    private final Object mLock = new Object();

    private static final CameraManager instance = new CameraManager();

    //默认后置摄像头
    private int mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;

    private CameraManager() {
    }

    public static CameraManager instance() {
        return instance;
    }

    public Camera getCamera() {
        return mCamera;
    }

    public CameraManager setCameraId(int mCameraId) {
        this.mCameraId = mCameraId;
        return instance;
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
                setCameraDisplayOrientation(context, mCameraId, mCamera);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    private void release() {
        synchronized (mLock) {
            if (mCamera != null) {
                mCamera.release();
                mCamera = null;
            }
        }
    }

    public CameraManager switchFlash(boolean enable) {
        synchronized (mLock) {
            if (mCamera != null) {

                Camera.Parameters parameters = mCamera.getParameters();
                List<String> modes = parameters.getSupportedFlashModes();

                if (enable) {
                    //on
                    String mode = findMatch(modes, Camera.Parameters.FLASH_MODE_TORCH,
                            Camera.Parameters.FLASH_MODE_ON,
                            Camera.Parameters.FLASH_MODE_RED_EYE,
                            Camera.Parameters.FLASH_MODE_AUTO);
                    if (null != mode) {
                        parameters.setFlashMode(mode);
                        mCamera.setParameters(parameters);
                    }
                } else {
                    // off
                    String mode = findMatch(modes, Camera.Parameters.FLASH_MODE_OFF,
                            Camera.Parameters.FLASH_MODE_AUTO);
                    if (null != mode) {
                        parameters.setFlashMode(mode);
                        mCamera.setParameters(parameters);
                    }
                }
            }
        }
        return instance;
    }

    public String findMatch(List<String> list, String... target) {
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
            }
        }
        return instance;
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
                                                  int cameraId, android.hardware.Camera camera) {

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
