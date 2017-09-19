package me.yifeiyuan.androidmedia;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

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


    private class HolderCallback implements SurfaceHolder.Callback2 {
        @Override
        public void surfaceRedrawNeeded(SurfaceHolder holder) {
            Log.d(TAG, "surfaceRedrawNeeded: ");
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.d(TAG, "surfaceCreated: ");

            mCamera = CameraManager.getInstance()
                    .setCameraId(cameraId)
                    .open(ControlCameraActivity.this)
                    .getCamera();
            if (null != mCamera) {
                List<Camera.Size> localSizes = mCamera.getParameters().getSupportedPreviewSizes();
                //todo 选择 previewsize
                mSupportedPreviewSizes = localSizes;//size = 16
                mPreviewSize = mSupportedPreviewSizes.get(0);// h 1080 w 1920
                try {
                    mCamera.setPreviewDisplay(mHolder);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
                mSurface.requestLayout();
                mCamera.setParameters(parameters);
                mCamera.startPreview();
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

    private class CirclePreviewCallback implements Camera.PreviewCallback {

        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        private CirclePreviewCallback() {
            paint.setColor(Color.parseColor("#00ff00"));
        }

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            Log.d(TAG, "onPreviewFrame: " + Thread.currentThread().getName());
            synchronized (mHolder) {
                Canvas canvas = null;
                try {
                    canvas = mHolder.lockCanvas();
                    if (null != canvas) {
                        canvas.drawCircle(100, 100, 40, paint);
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
}
