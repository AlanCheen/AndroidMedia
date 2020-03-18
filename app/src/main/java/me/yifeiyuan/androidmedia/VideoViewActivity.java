package me.yifeiyuan.androidmedia;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.VideoView;

public class VideoViewActivity extends AppCompatActivity {


    private static final String TAG = "VideoViewActivity";
    VideoView videoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);
        videoView = (VideoView) findViewById(R.id.video);

        videoView.setVideoPath("http://multimedia.file.alimmdn.com/templatetest/clip3.mp4?t=1506329775094");
//        videoView.setVideoPath("https://videotool.alicdn.com/templatetest/ALL.mp4?t=1506738400993");

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d(TAG, "onCompletion: ");
            }
        });

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.d(TAG, "onPrepared: ");
            }
        });

        videoView.start();

//        videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
//            @Override
//            public boolean onInfo(MediaPlayer mp, int what, int extra) {
//                return false;
//            }
//        });

    }

}
