package com.codename26.serviceslideshow;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import java.util.Random;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class SlideShowService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_NEXT = "com.codename26.serviceslideshow.action.NEXT";
    public static final String ACTION_PREV = "com.codename26.serviceslideshow.action.PREV";

    // TODO: Rename parameters
    public static final String EXTRA_PARAM1 = "com.codename26.serviceslideshow.extra.PARAM1";
    public static final String EXTRA_PARAM2 = "com.codename26.serviceslideshow.extra.PARAM2";

    private String[] imageUris = {"/sdcard/Download/1.png", "/sdcard/Download/2.png","/sdcard/Download/3.png",
            "/sdcard/Download/4.png", "/sdcard/Download/5.png"};
    private String prevImageURI = "";
    public SlideShowService() {
        super("SlideShowService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_NEXT.equals(action)) {
                Log.d("prevImageURI", prevImageURI);
                String randImage = "";
                PendingIntent mPendingIntent = intent.getParcelableExtra(MainActivity.PARAM_PINTENT);
                if (prevImageURI.length() == 0) {
                    randImage = imageUris[new Random().nextInt(5)];
                    prevImageURI = randImage;
                } else {
                    randImage = prevImageURI;
                    while (randImage.equals(prevImageURI)){
                        randImage = imageUris[new Random().nextInt(5)];
                    }
                    prevImageURI = randImage;
                }
                Intent backIntent = new Intent().putExtra(MainActivity.IMAGE_URI, randImage);
                try {
                    mPendingIntent.send(SlideShowService.this, MainActivity.TASK_NEXT, backIntent);
                    Log.d("Image Uri", randImage);
                    Log.d("prevImageURI", prevImageURI);
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
            } else if (ACTION_PREV.equals(action)) {

            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Service Status", "Service stopped");
    }
}
