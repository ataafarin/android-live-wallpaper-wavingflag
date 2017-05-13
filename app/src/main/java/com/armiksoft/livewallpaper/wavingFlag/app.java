package com.armiksoft.livewallpaper.wavingFlag;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.onesignal.OneSignal;

public class app extends Application{

	private static Context mContext;

	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this;

//		Log.d("OneSignalTag", "Before OneSignal init");
//		OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
//		OneSignal.startInit(this).init();
//		Log.d("OneSignalTag", "After OneSignal init");
	}
	public static Context getContext(){
		return mContext;
	}
}