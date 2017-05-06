package net.testSocket;

import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * Created by administrator on 6/6/16.
 */
public class AppInfo {
    public String appName = "";
    public String packageName = "";
    public String versionName = "";
    public int versionCode = 0;
    public Drawable appIcon = null;
    public void print() {
        Log.v("XXX", "Name:" + appName + " Package:" + packageName);
        Log.v("XXX", "Name:" + appName + " versionName:" + versionName);
        Log.v("XXX", "Name:" + appName + " versionCode:" + versionCode);
    }
}
