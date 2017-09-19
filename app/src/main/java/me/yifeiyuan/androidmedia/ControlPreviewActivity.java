package me.yifeiyuan.androidmedia;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
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

public class ControlPreviewActivity extends AppCompatActivity {

    private static final String TAG = "ControlPreviewActivity";

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
        setContentView(R.layout.activity_control_preview);
        mSurface = (SurfaceView) findViewById(R.id.surface);


        mCallback = new HolderCallback();

        mHolder = mSurface.getHolder();

        mHolder.addCallback(mCallback);

        paint.setColor(Color.parseColor("#00ff00"));
        paint.setStrokeWidth(6);
    }


    private boolean safeCameraOpen(int id) {
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

    private byte[] buffer;

    private class HolderCallback implements SurfaceHolder.Callback2 {

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

//                try {
                List<Camera.Size> localSizes = mCamera.getParameters().getSupportedPreviewSizes();
                //todo 选择 previewsize
                mSupportedPreviewSizes = localSizes;//size = 16
                mPreviewSize = mSupportedPreviewSizes.get(0);// h 1080 w 1920

                setCameraDisplayOrientation(ControlPreviewActivity.this, cameraId, mCamera);

                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
                mSurface.requestLayout();
                mCamera.setParameters(parameters);
                buffer = new byte[mPreviewSize.width * mPreviewSize.height * 3 / 2];
                mCamera.addCallbackBuffer(buffer);

//                mCamera.setPreviewCallback(new CirclePreviewCallback());
                mCamera.setPreviewCallbackWithBuffer(new CirclePreviewCallback());
                try {
                    mCamera.setPreviewTexture(new SurfaceTexture(1));
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
    }

    private int calcCameraRotation(Camera.CameraInfo cameraInfo, int rotation) {
        if (cameraInfo.facing == CAMERA_FACING_FRONT) {
            return (360 - (cameraInfo.orientation + rotation) % 360) % 360;
        } else {  // back-facing
            return (cameraInfo.orientation - rotation + 360) % 360;
        }
    }

    /**
     * 画圆
     */
    private class CirclePreviewCallback implements Camera.PreviewCallback {

        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        int x = 10;
        int y = 10;
        int raids = 1;
        private CirclePreviewCallback() {
            paint.setColor(Color.parseColor("#00ff00"));
            paint.setStrokeWidth(6);
        }

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            Log.d(TAG, "onPreviewFrame: " + Thread.currentThread().getName());
            camera.addCallbackBuffer(buffer);

            synchronized (mHolder) {
                Canvas canvas = null;
                try {
                    canvas = mHolder.lockCanvas();
                    if (null != canvas) {
                        x++;
                        y++;
                        raids++;
                        if (x >= 200) {
                            x = 0;
                        }
                        if (y>=300){
                            y=0;
                        }
                        if (raids>=100){
                            raids=0;
                        }
                        canvas.drawCircle(x, y, raids, paint);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (null != canvas) {
                        mHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }

    final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    int x = 10;
    int y = 10;
    int raids = 1;
    private void draw(byte[]data){
        synchronized (mHolder) {
            Canvas canvas = null;
            try {
                canvas = mHolder.lockCanvas();
                if (null != canvas) {
                    x++;
                    y++;
                    raids++;
                    if (x >= 200) {
                        x = 0;
                    }
                    if (y>=300){
                        y=0;
                    }
                    if (raids>=100){
                        raids=0;
                    }
                    canvas.drawCircle(x, y, raids, paint);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (null != canvas) {
                    mHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }
}
