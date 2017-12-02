package com.codename26.serviceslideshow;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.IBinder;
import android.os.PersistableBundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.Slide;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    public static final String IMAGE_URI = "IMAGE_URI";
    public static final String SLIDE_SHOW_RECEIVER = "SLIDE_SHOW_RECEIVER";
    public static final String ACTION_SLIDESHOW = "ACTION_SLIDESHOW";
    private static final String SLIDE_SHOW_STATUS = "SLIDE_SHOW_STATUS";
    private Button btnPrev;
    private Button btnNext;
    private Button btnSlideShow;
    private ImageView mImageView;
    static final int TASK_NEXT = 1;
    static int TASK_PREV = 2;
    public static final String PARAM_PINTENT = "PARAM_PINTENT";
    SlideService mService;
    boolean mBound = false;
    private boolean slideShowStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initButtons();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(MainActivity.SLIDE_SHOW_RECEIVER));
        if(savedInstanceState != null) {
            slideShowStatus = savedInstanceState.getBoolean(MainActivity.SLIDE_SHOW_STATUS, false);
            Log.d("slideShowStatus", "SlideShowStatus from saved state " + String.valueOf(slideShowStatus));
        }
        Log.d("slideShowStatus", String.valueOf(slideShowStatus));

    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(MainActivity.this, SlideService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("Broadcast Receiver", "Received Message");
            Uri mUri = intent.getParcelableExtra(MainActivity.IMAGE_URI);
            mImageView.setImageURI(mUri);
        }
    };

    private void initButtons() {
        btnPrev = findViewById(R.id.buttonPrev);
        btnNext = findViewById(R.id.buttonNext);
        btnSlideShow = findViewById(R.id.buttonSlideShow);
        mImageView = findViewById(R.id.imageView);

        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBound){
                    mImageView.setImageURI(mService.getPrevImage());
                }

            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBound){
                    mImageView.setImageURI(mService.getNextImage());
                }

            }
        });
        btnSlideShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBound){
                    mService.startSlideShow();
                    slideShowStatus = !slideShowStatus;
                    Log.d("slideShowStatus", "OnClickSlideShow" + slideShowStatus);
                }


            }
        });

    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            SlideService.LocalBinder binder = (SlideService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
            if (slideShowStatus){
                mService.startSlideShow();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(MainActivity.SLIDE_SHOW_STATUS, slideShowStatus);
    }
}
