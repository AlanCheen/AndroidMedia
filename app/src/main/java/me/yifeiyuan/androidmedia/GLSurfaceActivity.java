package me.yifeiyuan.androidmedia;

import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLSurfaceActivity extends AppCompatActivity {

    private static final String TAG = "GLSurfaceActivity";

    public GLSurfaceView surfaceView;
    public GRender render;
    public Listener listener;

    private Camera mCamera;
    private List<Camera.Size> mSupportedPreviewSizes;

    private Camera.Size mPreviewSize;

    private int cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_glsurface);
        surfaceView = (GLSurfaceView) findViewById(R.id.gl_sf);

        render = new GRender();

        listener = new Listener();

//        surfaceView.setEGLContextClientVersion(2);
        surfaceView.setRenderer(render);
        surfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        surfaceView.setZOrderOnTop(true);
        surfaceView.getHolder().setFormat(PixelFormat.TRANSPARENT);

    }

    SurfaceTexture surfaceTexture;
    private class GRender implements GLSurfaceView.Renderer {

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            Log.d(TAG, "onSurfaceCreated: ");
//            gl.glClearColor(1f, 0f, 0f, 0f);
//            GLES20.glClearColor(0.0f, 0.0f, 1.0f, 1.0f);
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
            surfaceTexture = new SurfaceTexture(0);
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {

            Log.d(TAG, "onSurfaceChanged: ");
//            GLES20.glClearColor(1.0f, 1.0f, 0.0f, 1.0f);

            gl.glViewport(0, 0, width, height);
            if (safeCameraOpen(cameraId)) {

                try {
                    List<Camera.Size> localSizes = mCamera.getParameters().getSupportedPreviewSizes();
                    //todo 选择 preview size
                    mSupportedPreviewSizes = localSizes;//size = 16
                    mPreviewSize = mSupportedPreviewSizes.get(0);// h 1080 w 1920

                    mCamera.setPreviewTexture(surfaceTexture);
                    Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                    Camera.getCameraInfo(cameraId, cameraInfo);
                    int orientation = cameraInfo.orientation;//0 90 180 270

                    ControlCameraActivity.setCameraDisplayOrientation(GLSurfaceActivity.this, cameraId, mCamera);

                    Camera.Parameters parameters = mCamera.getParameters();
                    parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
                    mCamera.setParameters(parameters);

                    surfaceTexture.setOnFrameAvailableListener(listener);

                    mCamera.startPreview();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            Log.d(TAG, "onDrawFrame: ");
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
//            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
            if (null != surfaceTexture) {
                surfaceTexture.updateTexImage();
            }
        }
    }


    private class Listener implements SurfaceTexture.OnFrameAvailableListener {

        @Override
        public void onFrameAvailable(SurfaceTexture surfaceTexture) {
            surfaceView.requestRender();
            Log.d(TAG, "onFrameAvailable: ");
        }
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

}
