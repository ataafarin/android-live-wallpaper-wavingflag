package com.armiksoft.livewallpaper.wavingFlag;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.onesignal.OneSignal;

public class OpenGLFlagActivity extends Activity {

    private Button btn_setwallpaper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);
//        surfaceView = (OpenGLFlagSurfaceView) findViewById(R.id.surface_view);
//        btn_setwallpaper=(Button)findViewById(R.id.wallpaperButton);
//        Log.d("OneSignalTag", "Before OneSignal init");
//
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
//        OneSignal.startInit(this).init();
//
//        Log.d("OneSignalTag", "After OneSignal init");
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
    }
    public void onClick(View view) {
        Intent intent = new Intent(
                WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                new ComponentName(this, StripesService.class));
        startActivity(intent);
    }

}
