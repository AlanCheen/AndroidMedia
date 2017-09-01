package me.yifeiyuan.androidmedia;

import android.hardware.Camera;

/**
 * Created by mingjue on 2017/8/30.
 */
public class CameraManager {

    Camera mCamera;

    private static final CameraManager instance = new CameraManager();


    private CameraManager() {
    }

    public static CameraManager get(){
        return instance;
    }

    public void setCamera(Camera camera) {
        if (mCamera == camera) {
            return;
        }
    }

    void setting(){
        Camera.Parameters parameters = mCamera.getParameters();

    }
}
