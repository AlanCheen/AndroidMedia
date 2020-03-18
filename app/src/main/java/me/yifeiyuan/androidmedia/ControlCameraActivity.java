package me.yifeiyuan.androidmedia;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.IOException;
import java.util.List;


/**
 * https://developer.android.com/training/camera/cameradirect.html#TaskOpenCamera
 */
public class ControlCameraActivity extends AppCompatActivity implements Camera.PreviewCallback {


    private static final String TAG = "ControlCameraActivity";

    private SurfaceView mSurface;
    private SurfaceHolder mHolder;
    private HolderCallback mCallback;
    private Camera mCamera;
    private List<Camera.Size> mSupportedPreviewSizes;

    private Camera.Size mPreviewSize;

    //    private int cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    private int cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
    private byte[] mBuffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_camera);
        mSurface = (SurfaceView) findViewById(R.id.surface);


        mCallback = new HolderCallback();

        mHolder = mSurface.getHolder();

        mHolder.addCallback(mCallback);
    }

    private void releaseCameraAndPreview() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    public void takePicture(View view) {

        Camera.Parameters parameters = mCamera.getParameters();
//        parameters.set("rawsave-mode", "on");
//        mCamera.getParameters().set("",1);

        parameters.setPictureFormat(ImageFormat.NV21);

        mCamera.setParameters(parameters);

        int w = mCamera.getParameters().getPreviewSize().width;
        int h = mCamera.getParameters().getPreviewSize().height;

        int format = mCamera.getParameters().getPreviewFormat();

        int size1 = w*h*ImageFormat.getBitsPerPixel(format)/8;


        int w1 = mCamera.getParameters().getPictureSize().width;
        int h1 = mCamera.getParameters().getPictureSize().height;
        int f1 = mCamera.getParameters().getPictureFormat();

        int size = w1*h1*ImageFormat.getBitsPerPixel(f1)/8;
        Log.d(TAG, "takePicture: "+size1+"---"+size);//3110400

//        mCamera.addCallbackBuffer(new byte[w*h*ImageFormat.getBitsPerPixel(format)/8]);
//        mCamera.addCallbackBuffer(new byte[w*h*ImageFormat.getBitsPerPixel(format)/8]);
//        mCamera.addCallbackBuffer(new byte[w*h*ImageFormat.getBitsPerPixel(format)/8]);

        mBuffer = new byte[w*h* ImageFormat.getBitsPerPixel(format)/8];
//        mCamera.addCallbackBuffer(new byte[3133440]);
//        mCamera.addCallbackBuffer(new byte[3133440]);
//        mCamera.addCallbackBuffer(new byte[3133440]);
//        mCamera.addCallbackBuffer(new byte[3133440]);
//        mCamera.addCallbackBuffer(new byte[3133440]);
//        mCamera.addCallbackBuffer(new byte[3133440]);
//        mCamera.addCallbackBuffer(new byte[3133440]);
//        mCamera.addCallbackBuffer(new byte[2]);
//        mCamera.setOneShotPreviewCallback();
//        mCamera.takePicture(null, new Camera.PictureCallback() {
//            @Override
//            public void onPictureTaken(byte[] data, Camera camera) {
//                final byte[] tmp = mBuffer;
//                Log.d(TAG, "rawdata() called with: " + "data = [" + data + "], camera = [" + camera + "]");
//            }
//        }, new Camera.PictureCallback() {
//            @Override
//            public void onPictureTaken(byte[] data, Camera camera) {//3133440
//                Log.d(TAG, "jpeg() called with: " + "data = [" + data.length + "], camera = [" + camera + "]");
//                camera.stopPreview();
//                camera.addCallbackBuffer(data);
//                camera.setPreviewCallbackWithBuffer(ControlCameraActivity.this);
//                camera.startPreview();
//            }
//        });
        mCamera.setOneShotPreviewCallback(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                Log.d(TAG, "one shot() called with: " + "data = [" + data.length + "], camera = [" + camera + "]");
                camera.stopPreview();
                camera.setPreviewCallback(ControlCameraActivity.this);
                camera.startPreview();
            }
        });
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        Log.d(TAG, "onPreviewFrame() called with: " + "data = [" + data.length + "], camera = [" + camera + "]");
    }


    private class HolderCallback implements SurfaceHolder.Callback2 {
        @Override
        public void surfaceRedrawNeeded(SurfaceHolder holder) {
            Log.d(TAG, "surfaceRedrawNeeded: ");
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.d(TAG, "surfaceCreated: ");

            initWithCamera();
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

    private void initWithCamera() {
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
            parameters.setPictureSize(mPreviewSize.width, mPreviewSize.height);
            parameters.setPictureFormat(ImageFormat.NV21);
            mSurface.requestLayout();
            mCamera.setParameters(parameters);
//            mCamera.addCallbackBuffer(new byte[3133440]);
//            mCamera.addCallbackBuffer(new byte[3133440]);
//            mCamera.addCallbackBuffer(new byte[3133440]);
//            mCamera.addCallbackBuffer(new byte[3133440]);
            mCamera.setPreviewCallback(this);
//            mCamera.setPreviewCallbackWithBuffer(this);
            mCamera.startPreview();
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
