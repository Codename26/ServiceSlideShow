package com.codename26.serviceslideshow;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.File;
import java.net.URI;
import java.util.Random;

public class SlideService extends Service {
    private final IBinder mBinder = new LocalBinder();
    private String[] imageUris = {"/sdcard/Download/1.png", "/sdcard/Download/2.png","/sdcard/Download/3.png",
            "/sdcard/Download/4.png", "/sdcard/Download/5.png"};
    private int current = 0;
    private int prev = 0;
    public SlideService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Service Status", "Service Stopped");
    }

    public class LocalBinder extends Binder{
        SlideService getService(){
            return SlideService.this;
        }
    }

    public Uri getNextImage(){
        Log.d("current index", String.valueOf(current));
        int nextImageIndex = current;
        while (nextImageIndex == current){
            nextImageIndex = new Random().nextInt(5);
        }
        prev = current;
        current = nextImageIndex;
        return Uri.parse(new File(imageUris[nextImageIndex]).toString());
    }
    public Uri getPrevImage(){
        current = prev;
        return Uri.parse(new File(imageUris[prev]).toString());
    }

    private void sendMessage() {
        Intent intent = new Intent("custom-event-name");
        // You can also include some extra data.
        intent.putExtra("message", "This is my message!");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}
