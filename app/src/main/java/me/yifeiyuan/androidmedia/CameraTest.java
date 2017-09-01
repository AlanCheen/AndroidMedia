package me.yifeiyuan.androidmedia;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;

/**
 * Created by mingjue on 2017/8/30.
 */
public class CameraTest {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void test(Context cxt) {

        CameraManager manager = (CameraManager) cxt.getSystemService(Context.CAMERA_SERVICE);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                String[] list = manager.getCameraIdList();
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
}
