package wmc.cs.msc.aueb.gr.modata;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;

/**
 * Created by ellen on 2/13/16.
 */
public class Utilities {

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static int getRunningTaskCount(Context c) {
        ActivityManager am = (ActivityManager) c.getSystemService(Activity.ACTIVITY_SERVICE);
        return  am.getRunningTasks(100).size();
    }

    public static int getRunningServiceCount(Context c) {
        ActivityManager am = (ActivityManager) c.getSystemService(Activity.ACTIVITY_SERVICE);
        return  am.getRunningServices(100).size();
    }

}
