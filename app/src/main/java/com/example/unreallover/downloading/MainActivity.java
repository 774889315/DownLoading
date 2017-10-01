package com.example.unreallover.downloading;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {
    EditText url;
    EditText target;
    Button downBn;
    ProgressBar bar;
    TextView rate;
    DownUtil downUtil;
    private int mDownStatus;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    //要一手权限，诶！就很有灵性。
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        verifyStoragePermissions(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        url = (EditText) findViewById(R.id.url);
        target = (EditText) findViewById(R.id.target);
        downBn = (Button) findViewById(R.id.down);
        rate = (TextView) findViewById(R.id.rate);
        bar = (ProgressBar) findViewById(R.id.bar);



        final Handler handler = new Handler()
        {
            public void handleMessage(Message msg)
            {
                if (msg.what == 0x123)
                {
                    bar.setProgress(mDownStatus);
                    //防止出现下载百分之106的尴尬场面
                    if(mDownStatus>=100)
                    {
                        mDownStatus=100;
                    }
                    rate.setText(mDownStatus+"%");
                }
            }
        };
        downBn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                downUtil = new DownUtil(url.getText().toString(),target.getText().toString(),6);
                downUtil = new DownUtil("https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=2716938858,1844466994&fm=173&s=CF6028C4C80058C44B807C9003005092&w=500&h=307&img.JPEG","/mnt/ext_sdcard/a.JPEG",6);
                new Thread()
                {
                    @Override
                    public void run()
                    {
                        try
                        {
                            downUtil.download();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        final Timer timer = new Timer();
                        timer.schedule(new TimerTask()
                        {
                            @Override
                            public void run()
                            {
                                double completeRate = downUtil.getCompleteRate();
                                mDownStatus = (int) (completeRate *100);
                                handler.sendEmptyMessage(0x123);
                                if(mDownStatus >= 100)
                                {
                                    timer.cancel();
                                }
                            }
                        },0,100);
                    }
                }.start();
            }
        });
    }
}
