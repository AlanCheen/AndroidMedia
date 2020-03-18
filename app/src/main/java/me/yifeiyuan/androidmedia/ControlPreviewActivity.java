package me.yifeiyuan.androidmedia;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES11Ext;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;


public class ControlPreviewActivity extends AppCompatActivity implements SurfaceTexture.OnFrameAvailableListener {

    private static final String TAG = "ControlPreviewActivity";

    private SurfaceView mSurfaceView;
    private SurfaceHolder mHolder;
    private HolderCallback mCallback;
    private Camera mCamera;
    private List<Camera.Size> mSupportedPreviewSizes;

    private Camera.Size mPreviewSize;

    private SurfaceTexture mSurfaceTexture;

    private int cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    private Surface mSurface;
//    private int cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_preview);
        mSurfaceView = (SurfaceView) findViewById(R.id.surface);

        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                && (Build.VERSION.SDK_INT <= 23)) {
            mSurfaceTexture = new SurfaceTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, true);
        } else {
            mSurfaceTexture = new SurfaceTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES);
        }
        mSurfaceTexture.setOnFrameAvailableListener(this);
        mCallback = new HolderCallback();

        mHolder = mSurfaceView.getHolder();//SurfaceView$4
        mSurface = mHolder.getSurface();
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

    private byte[] mBuffer;

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        Log.d(TAG, "onFrameAvailable: ");
//        mSurfaceView.requestre
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
                    .open(ControlPreviewActivity.this)
                    .getCamera();
            if (null != mCamera) {
                //todo 选择 previewsize
                mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();//size = 16
                mPreviewSize = mSupportedPreviewSizes.get(15);// h 1080 w 1920
//                mPreviewSize = mSupportedPreviewSizes.get(0);// h 1080 w 1920

                Camera.Parameters parameters = mCamera.getParameters();
                parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
                mSurfaceView.requestLayout();
                mCamera.setParameters(parameters);
                for (int i = 0; i < 4; i++) {
                    byte[] buf = new byte[mPreviewSize.width * mPreviewSize.height * 3 / 2];
                    mCamera.addCallbackBuffer(buf);
                }
                mBuffer = new byte[mPreviewSize.width * mPreviewSize.height * 3 / 2];
//                mCamera.addCallbackBuffer(mBuffer);
//                mCamera.setPreviewCallback(new CirclePreviewCallback());
//                mCamera.setPreviewCallbackWithBuffer(new CirclePreviewCallback());
                mCamera.setPreviewCallbackWithBuffer(new SimplePreviewCallback());
//                mCamera.setPreviewCallbackWithBuffer(new BitmapPreviewCallback());
                try {
                    mCamera.setPreviewTexture(mSurfaceTexture);
                    mCamera.startPreview();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mCamera.startPreview();
//                drawThread.start();
            }

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Log.d(TAG, "surfaceChanged: ");
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.d(TAG, "surfaceDestroyed: ");
            CameraManager.getInstance().release();
            run = false;
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
                        if (y >= 300) {
                            y = 0;
                        }
                        if (raids >= 100) {
                            raids = 0;
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
            camera.addCallbackBuffer(mBuffer);
        }
    }

    final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    int x = 10;
    int y = 10;
    int raids = 1;

    private void draw(byte[] data, Camera camera) {
//        Log.d(TAG, "draw()data = [" + data);
        synchronized (mHolder) {
            Canvas canvas = null;
            try {
                int width = mCamera.getParameters().getPreviewSize().width;
                int height = mCamera.getParameters().getPreviewSize().height;

                Bitmap bmp = rawByteArray2RGBABitmap2(data, width, height);
                canvas = mHolder.lockCanvas();
                if (null != canvas) {

                    canvas.drawBitmap(bmp, 0, 0, null);

//                    YuvImage image = new YuvImage(data, mCamera.getParameters().getPreviewFormat(),width, height, null);
//                    YuvImage image = new YuvImage(data, ImageFormat.NV21,width, height, null);
//
//                    ByteArrayOutputStream os = new ByteArrayOutputStream(data.length);
//                    if(!image.compressToJpeg(new Rect(0, 0, width, height), 100, os)){
//                        byte[] tmp = os.toByteArray();
//                        Bitmap bmp = BitmapFactory.decodeByteArray(tmp, 0,tmp.length);
//                        canvas.drawBitmap(bmp, 0, 0, null);
////                        canvas.drawCircle(x, y, raids, paint);
//                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (null != canvas) {
                    mHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
        camera.addCallbackBuffer(data);
    }


    private class SimplePreviewCallback implements Camera.PreviewCallback {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            draw(data, camera);
//            camera.addCallbackBuffer(data);
        }
    }

    private class BitmapPreviewCallback implements Camera.PreviewCallback {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            draw(data, camera);
        }
    }


    public Bitmap rawByteArray2RGBABitmap2(byte[] data, int width, int height) {
        int frameSize = width * height;
        int[] rgba = new int[frameSize];
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++) {
                int y = (0xff & ((int) data[i * width + j]));
                int u = (0xff & ((int) data[frameSize + (i >> 1) * width + (j & ~1) + 0]));
                int v = (0xff & ((int) data[frameSize + (i >> 1) * width + (j & ~1) + 1]));
                y = y < 16 ? 16 : y;
                int r = Math.round(1.164f * (y - 16) + 1.596f * (v - 128));
                int g = Math.round(1.164f * (y - 16) - 0.813f * (v - 128) - 0.391f * (u - 128));
                int b = Math.round(1.164f * (y - 16) + 2.018f * (u - 128));
                r = r < 0 ? 0 : (r > 255 ? 255 : r);
                g = g < 0 ? 0 : (g > 255 ? 255 : g);
                b = b < 0 ? 0 : (b > 255 ? 255 : b);
                rgba[i * width + j] = 0xff000000 + (b << 16) + (g << 8) + r;
            }
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bmp.setPixels(rgba, 0, width, 0, 0, width, height);
        return bmp;
    }


    private boolean run = true;
    private Thread drawThread = new Thread() {
        @Override
        public void run() {
            super.run();
            while (run) {
                draw(mBuffer, mCamera);
                try {
                    Thread.sleep(52);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
}
