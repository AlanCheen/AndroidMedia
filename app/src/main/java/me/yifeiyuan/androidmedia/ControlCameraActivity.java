package me.yifeiyuan.androidmedia;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

import static android.hardware.Camera.CameraInfo.CAMERA_FACING_FRONT;

/**
 * https://developer.android.com/training/camera/cameradirect.html#TaskOpenCamera
 */
public class ControlCameraActivity extends AppCompatActivity {


    private static final String TAG = "ControlCameraActivity";

    private SurfaceView mSurface;
    private SurfaceHolder mHolder;
    private HolderCallback mCallback;
    private Camera mCamera;
    private List<Camera.Size> mSupportedPreviewSizes;

    private Camera.Size mPreviewSize;

//    private int cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    private int cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_camera);
        mSurface = (SurfaceView) findViewById(R.id.surface);


        mCallback = new HolderCallback();

        mHolder = mSurface.getHolder();

        mHolder.addCallback(mCallback);

    }


    private boolean safeCameraOpen(int id){
        boolean opened = false;

        try {
            releaseCameraAndPreview();
            mCamera = Camera.open(id);
            opened = (mCamera != null);
        } catch (Exception e) {
            Log.e(getString(R.string.app_name), "failed to open Camera");
            e.printStackTrace();
        }

        return opened;
    }

    private void releaseCameraAndPreview() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }


    private class HolderCallback implements SurfaceHolder.Callback2{
        @Override
        public void surfaceRedrawNeeded(SurfaceHolder holder) {
            Log.d(TAG, "surfaceRedrawNeeded: ");
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.d(TAG, "surfaceCreated: ");
            // Now that the size is known, set up the camera parameters and begin
            // the preview.
            if (safeCameraOpen(cameraId)) {

                try {
                    List<Camera.Size> localSizes = mCamera.getParameters().getSupportedPreviewSizes();
                    //todo 选择 previewsize
                    mSupportedPreviewSizes = localSizes;//size = 16
                    mPreviewSize = mSupportedPreviewSizes.get(0);// h 1080 w 1920
                    mCamera.setPreviewDisplay(mHolder);

                    Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                    Camera.getCameraInfo(cameraId,cameraInfo);
                    int orientation = cameraInfo.orientation;//0 90 180 270
                    Log.d(TAG, "surfaceCreated orientation: "+orientation);

//                    int mRotation = calcCameraRotation(cameraInfo,0);
//                    mCamera.setDisplayOrientation(mRotation);

                    setCameraDisplayOrientation(ControlCameraActivity.this, cameraId, mCamera);

                    Camera.Parameters parameters = mCamera.getParameters();
                    parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
                    mSurface.requestLayout();
                    mCamera.setParameters(parameters);

                    mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                        @Override
                        public void onPreviewFrame(byte[] data, Camera camera) {
                            Log.d(TAG, "onPreviewFrame: ");
                        }
                    });

                    mCamera.startPreview();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Log.d(TAG, "surfaceChanged: ");
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.d(TAG, "surfaceDestroyed: ");
        }
    }


    public static void setCameraDisplayOrientation(Activity activity,
                                                   int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    private int calcCameraRotation(Camera.CameraInfo cameraInfo, int rotation) {
        if (cameraInfo.facing == CAMERA_FACING_FRONT) {
            return (360 - (cameraInfo.orientation + rotation) % 360) % 360;
        } else {  // back-facing
            return (cameraInfo.orientation - rotation + 360) % 360;
        }
    }
}
