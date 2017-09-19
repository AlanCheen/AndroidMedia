package me.yifeiyuan.androidmedia;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.SurfaceView;
import android.widget.FrameLayout;

public class GameDisplayActivity extends AppCompatActivity {

     private GameDisplay gameDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_display);
        //声明
        //初始化
        DisplayMetrics dm = getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        gameDisplay = new GameDisplay(this, dm.widthPixels, dm.heightPixels);
        gameDisplay.setVisibility(SurfaceView.VISIBLE);

        //加入到当前activity的layout中
         FrameLayout root = (FrameLayout) findViewById(R.id.root);
         root.addView(gameDisplay,0);
    }
}
