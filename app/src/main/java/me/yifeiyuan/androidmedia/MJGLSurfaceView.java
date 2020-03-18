package me.yifeiyuan.androidmedia;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import java.nio.ByteBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by mingjue on 2017/9/26.
 */
public class MJGLSurfaceView extends GLSurfaceView implements GLSurfaceView.Renderer {

    public MJGLSurfaceView(Context context) {
        super(context);
        init();
    }

    public MJGLSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        getHolder().addCallback(null);

//        setEGLContextFactory(new EGLContextFactory() {
//            @Override
//            public EGLContext createContext(EGL10 egl, EGLDisplay display, EGLConfig eglConfig) {
//                return egl.eglCreateContext(display, eglConfig);
//            }
//
//            @Override
//            public void destroyContext(EGL10 egl, EGLDisplay display, EGLContext context) {
//
//            }
//        });
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

    }

    int w;
    int h;

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        w = width;
        h = height;
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        ByteBuffer buffer = ByteBuffer.allocate(1111);
        gl.glReadPixels(0, 0, w, h, 1,1,buffer);
    }
}
