package com.codename26.serviceslideshow;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.File;
import java.net.URI;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class SlideService extends Service {
    private final IBinder mBinder = new LocalBinder();
    private int[] imageArr = {R.drawable.i1, R.drawable.i2, R.drawable.i3, R.drawable.i4, R.drawable.i5};
    private int current = 0;
    private int prev = 0;
    private Timer mTimer;
    private MyTimerTask mTimerTask;
    private boolean slideShowIsActive = false;
    public SlideService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("Bind_Status", "Service Binded");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("Bind_Status", "Service Unbinded");
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Service_Status", "Service Stopped");
        Log.d("Timer_Status", "Timer status onDestroy" + mTimer);
    }

    public class LocalBinder extends Binder{
        SlideService getService(){
            return SlideService.this;
        }
    }

    public Uri getNextImage(){
        int nextImageIndex = current;
        while (nextImageIndex == current){
            nextImageIndex = new Random().nextInt(5);
        }
        prev = current;
        current = nextImageIndex;
        return Uri.parse("android.resource://com.codename26.serviceslideshow/" + imageArr[current]);
    }
    public Uri getPrevImage(){
        current = prev;
        return Uri.parse("android.resource://com.codename26.serviceslideshow/" + imageArr[prev]);
    }

    public void startSlideShow(){
        if (mTimer != null) {
            mTimer.cancel();
        }
        if (slideShowIsActive){
            mTimer.purge();
            mTimer = null;
            slideShowIsActive = false;
            Log.d("Timer_Status", "timer stopped");
        } else {
            mTimer = new Timer();
            mTimerTask = new MyTimerTask();
            slideShowIsActive = true;
            Log.d("Timer_Status", "timer started " + slideShowIsActive);
            mTimer.schedule(mTimerTask, 1000, 1000);
        }
    }

    class MyTimerTask extends TimerTask{
        @Override
        public void run() {
            int i = current;
            while (i == current){
                i = new Random().nextInt(5);
            }
            Log.d("timer", "Timertask i = " + i);
            current = i;
            sendMessage(i);
        }
    }

    private void sendMessage(int i) {
        Intent intent = new Intent(MainActivity.SLIDE_SHOW_RECEIVER);
        // You can also include some extra data.
        intent.putExtra(MainActivity.IMAGE_URI, Uri.parse("android.resource://com.codename26.serviceslideshow/" + imageArr[i]));
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    public void stopSlideShow() {
        if (slideShowIsActive){
            if (mTimer != null){
                mTimer.cancel();
                mTimer = null;
                slideShowIsActive = false;
                Log.d("Timer_Status", "timer stopped");
            }
        }
    }
}
