package me.yifeiyuan.androidmedia;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean hasFeature = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);

    }

    public void recordVideoSimply(View view) {
        Intent intent = new Intent(this, RecordVideoSimplyActivity.class);
        startActivity(intent);
    }


    public void controlCamera(View view) {
        Intent intent = new Intent(this, ControlCameraActivity.class);
        startActivity(intent);
    }

    public void glSurfaceView(View view) {
        Intent intent = new Intent(this, GLSurfaceActivity.class);
        startActivity(intent);
    }
}
