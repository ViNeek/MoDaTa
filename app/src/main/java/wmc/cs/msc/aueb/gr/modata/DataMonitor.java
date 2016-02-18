package wmc.cs.msc.aueb.gr.modata;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

// Data Usage
import android.content.pm.PackageManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;

import android.net.TrafficStats;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import android.content.Context;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by ellen on 2/10/16.
 */
public class DataMonitor extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "DataMonitor";

    public static final String STATS_FILE_NAME = "stats";
    public static final String STATS_FILE_EXT = ".csv";
    public static final String STATS_DIR = "Modata";
    public static final int SLEEP_INTERVAL = 2000;
    public static final double KBYTE_SIZE_INV = 1.0 / 1024.0;
    public static final double KBYTE_SIZE = 1024.0;
    public static final boolean STATS_FILE_WRITE = false;

    public static boolean isDone;

    public static final Set<String> mApps;
    public static final Set<String> mPackages;
    public static final Set<Drawable> mPackagesDrawables;
    public static final Set<UsageStatistics> mAppStats;
    public static final Map<String, UsageStatistics> mAppStatsMap;
    public static final Map<String, PrintWriter> mAppStatsFileMap;

    private Context mParent;
    private Intent mIntent;

    //private File mStoreFile;
    //private PrintWriter mStorePrinter;

    public DataMonitor(Intent inten, Context c) {
        mParent = c;
        mIntent = inten;


    }

    public static List<UsageStatistics> getMonitoredPackages() {
        List<UsageStatistics> l_Ret = new ArrayList<>();

        for ( String app : mApps ) {
            UsageStatistics l_Stats = new UsageStatistics(app);
            l_Ret.add(l_Stats);
            mAppStats.add(l_Stats);
            mAppStatsMap.put(app, l_Stats);
        }

        return l_Ret;
    }

    public void finish() {
        isDone = true;
    }

    protected Void doInBackground(Void... args) {
        File l_StoreFile;
        PrintWriter l_StorePrinter;

        long l_Latch = 1;

        long startTime;
        long endTime;
        long scanStart;
        long scanDuration;
        long scanEnd;
        long monitorStart;
        long monitorEnd;

        double initRcvd;
        double initSend;

        long lastRcvd;
        long lastSend;

        long sleepInterval = SLEEP_INTERVAL;

        UsageStatistics l_Stats;

        StringBuilder l_Log = new StringBuilder();

        l_StorePrinter = null;

        for ( String app : mApps ) {
            l_StoreFile = new File(getAppStorageDir(mParent, STATS_DIR), STATS_FILE_NAME +"_"+app+STATS_FILE_EXT);
            if (!l_StoreFile.exists()) {
                try {
                    l_StoreFile.createNewFile();
                } catch (IOException ioex) {
                    Log.e("FILE", "General");
                }
            }

            try {
                l_StorePrinter = new PrintWriter(l_StoreFile);
                mAppStatsFileMap.put(app, l_StorePrinter);
            } catch (FileNotFoundException fnfex) {
                Log.e("FILE", "not found");
            } catch (IOException ioex) {
                Log.e("FILE", "General");
            }
        }

        final PackageManager pm = mParent.getPackageManager();
        List<ApplicationInfo> packages;

        // Basic Logging
        Log.i(TAG, "Tasks " + Utilities.getRunningTaskCount(mParent));
        Log.i(TAG, "Services " + Utilities.getRunningServiceCount(mParent));
        Log.i(TAG, "WiFi speed " + ConnectivityMonitor.getWifiSpeed(mParent));
        Log.i(TAG, "WiFi Rssi " + ConnectivityMonitor.getWifiRssi(mParent));
        Log.i(TAG, "Battery " + AndroidPowerCollector.getBatteryCapacity(mParent));
        Log.i(TAG, "Battery " + AndroidPowerCollector.getWifiOnConsumption(mParent));
        Log.i(TAG, "battery " + AndroidPowerCollector.getWifiActiveConsumption(mParent));

        monitorStart = System.currentTimeMillis();

        // get a list of installed apps.
        packages = pm.getInstalledApplications(0);

        // To get starting values
        // loop through the list of installed packages and see if the selected
        // app is in the list
        for (ApplicationInfo packageInfo : packages) {
            // get the UID for the selected app
            int UID = packageInfo.uid;
            String package_name = packageInfo.packageName;

            if (!mPackages.contains(package_name))
                continue;

            Log.i(TAG, package_name);
            ApplicationInfo app = null;

            try {
                app = pm.getApplicationInfo(package_name, 0);
            } catch (PackageManager.NameNotFoundException e) {
                // TODO Auto-generated catch block
                //e.printStackTrace();
            }

            String name = (String) pm.getApplicationLabel(app);

            l_Stats = mAppStatsMap.get(name);

            l_Stats.setmMonitorStart(monitorStart);
            l_Stats.setmConnType(ConnectivityMonitor.getConnectionType(mParent));

            long received = TrafficStats.getUidRxBytes(UID);
            long send = TrafficStats.getUidTxBytes(UID);
            lastSend = send;
            lastRcvd = received;

            l_Stats.setmRecvBytes(0.0);
            l_Stats.setmSendBytes(0.0);
            l_Stats.setmMRecvBytes(0.0);
            l_Stats.setmMSendBytes(0.0);

            l_Stats.mLastRcvd = received;
            l_Stats.mLastSend = send;

            double mreceived = TrafficStats.getMobileRxBytes() * KBYTE_SIZE_INV;;
            double msend = TrafficStats.getMobileTxBytes() * KBYTE_SIZE_INV;;
            double total = received + send;

            //l_StorePrinter.println(received + "," + mreceived + "," + send + "," + msend + ",");
            Log.i(TAG, "" + received + "," + mreceived + "," + send + "," + msend + ",");
        }

        while( !isDone ) {
            try {
                Thread.sleep(sleepInterval);
            } catch (InterruptedException ex) {

            }

            scanStart = System.currentTimeMillis();

            // get a list of installed apps.
            packages = pm.getInstalledApplications(0);

            // loop through the list of installed packages and see if the selected
            // app is in the list
            for (ApplicationInfo packageInfo : packages) {
                // get the UID for the selected app
                int UID = packageInfo.uid;
                String package_name = packageInfo.packageName;

                //Log.i("INFO ", "Name : " + package_name );
                //Log.i("INFO ", "Name : " + (String) pm.getApplicationLabel(packageInfo) );

                if ( !mPackages.contains(package_name) )
                    continue;


                Log.i(TAG, package_name);
                ApplicationInfo app = null;
                try {
                    app = pm.getApplicationInfo(package_name, 0);
                } catch (PackageManager.NameNotFoundException e) {
                    // TODO Auto-generated catch block
                    //e.printStackTrace();
                }
                String name = (String) pm.getApplicationLabel(app);

                l_Stats = mAppStatsMap.get(name);
                l_StorePrinter = mAppStatsFileMap.get(name);

                Log.i(TAG, ConnectivityMonitor.getConnectionType(mParent));
                l_Stats.setmConnType(ConnectivityMonitor.getConnectionType(mParent));

                long received = TrafficStats.getUidRxBytes(UID) - l_Stats.mLastRcvd;
                long send = TrafficStats.getUidTxBytes(UID) - l_Stats.mLastSend;
                double prev_received;
                double prev_send;
                if ( ConnectivityMonitor.isConnectedWifi(mParent) ) {
                    prev_received = l_Stats.getmRecvBytes();
                    prev_send = l_Stats.getmSendBytes();
                } else {
                    prev_received = l_Stats.getmMRecvBytes();
                    prev_send = l_Stats.getmMSendBytes();
                }
                double r_diff = received * KBYTE_SIZE_INV - prev_received;
                double s_diff = send * KBYTE_SIZE_INV - prev_send;

                Log.i("INFO ", "Name : " + name + " Bytes Sent: " + prev_send + " Bytes Recvd: " + prev_received);
                Log.i("INFO ", "Name : " + name + " Bytes Sent: " + r_diff + " Bytes Recvd: " + s_diff);

                //l_Stats.setmRecvBytes(prev_received + ((received * KBYTE_SIZE_INV) - prev_received));
                //l_Stats.setmSendBytes(prev_send + ((send * KBYTE_SIZE_INV) - prev_send));

                if ( ConnectivityMonitor.isConnectedWifi(mParent) ) {
                    //l_Stats.setmRecvBytes(received * KBYTE_SIZE_INV);
                    //l_Stats.setmSendBytes(send * KBYTE_SIZE_INV);
                    l_Stats.setmRecvBytes(l_Stats.getmRecvBytes() + r_diff);
                    l_Stats.setmSendBytes(l_Stats.getmSendBytes() + s_diff);
                } else {
                    //l_Stats.setmMRecvBytes(received * KBYTE_SIZE_INV);
                    //l_Stats.setmMSendBytes(send * KBYTE_SIZE_INV);
                    l_Stats.setmMRecvBytes(l_Stats.getmMRecvBytes() + r_diff);
                    l_Stats.setmMSendBytes(l_Stats.getmMSendBytes() + s_diff);
                }
                l_Stats.setmBatteryLevel(ModataActivity.gBatteryLevel);
                l_Stats.setmMonitorDuration(scanStart - monitorStart);

                Log.i("INFO 2 ", "Name : " + name + " Bytes Sent: " + l_Stats.getmRecvBytes() + " Bytes Recvd: " + received);

                long mreceived = TrafficStats.getMobileRxBytes();
                long msend = TrafficStats.getMobileTxBytes();
                long total = received + send;

                //l_Stats.mLastRcvd = received;
                //l_Stats.mLastSend = send;

                l_StorePrinter.println(l_Stats.toFile());

                //Log.i("INFO ", "Name : " + name + " Bytes Sent: " + send + " Bytes Recvd: " + received);
            }

            scanEnd = System.currentTimeMillis();

            Log.i(TAG, "\n --NEXT-- \n");

            Handler mainHandler = new Handler(mParent.getMainLooper());

            Runnable myRunnable = new Runnable() {
                @Override
                public void run() { ModataActivity.gListAdapter.notifyDataSetChanged(); } // This is your code
            };
            mainHandler.post(myRunnable);
        }

        monitorEnd = System.currentTimeMillis();
        for ( UsageStatistics stat : mAppStats ) {
            l_StorePrinter = mAppStatsFileMap.get(stat.getmName());
            //stat.setmMonitorDuration(monitorEnd -  stat.getmMonitorStart());
            //l_StorePrinter.println(stat.getmRecvBytes()+","+ stat.getmRecvBytes()+","+stat.getmMonitorDuration() );
        }

        //l_StorePrinter.println(l_Log.toString());

        mParent.stopService(new Intent(mParent, DataMonitorService.class));

        for ( String app : mApps ) {
            l_StorePrinter = mAppStatsFileMap.get(app);
            if (l_StorePrinter != null) {
                l_StorePrinter.close();
            }
        }

        return null;
    }

    public File getAppStorageDir(Context context, String albumName) {
        // Get the directory for the app's private pictures directory.
        File file = new File(context.getExternalFilesDir(
                null), albumName);
        if (!file.mkdirs()) {
            Log.e("Directory", "Directory not created");
        }
        return file;
    }

    protected void onProgressUpdate(Integer... progress) {
    }

    protected void onPostExecute() {

    }

    static {
        mApps = new HashSet();
        mPackages = new HashSet();
        mAppStats = new HashSet();
        mAppStatsMap = new HashMap();
        mAppStatsFileMap =  new HashMap();
        mPackagesDrawables = new HashSet();

        mApps.add("Chrome");
        mApps.add("YouTube");
        mApps.add("Gmail");
        mApps.add("Messenger");
        mApps.add("Dropbox");
        mApps.add("Facebook");
        mApps.add("Skype");

        mPackages.add("com.android.chrome");
        mPackages.add("com.facebook.katana");
        mPackages.add("com.google.android.gm");
        mPackages.add("com.facebook.orca");
        mPackages.add("com.dropbox.android");
        mPackages.add("com.google.android.youtube");
        mPackages.add("com.skype.raider");

    }
}
