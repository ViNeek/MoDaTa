package wmc.cs.msc.aueb.gr.modata;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.BatteryManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.pm.PackageManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;

import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import android.net.TrafficStats;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import java.lang.reflect.Field;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.Scanner;

import android.app.IntentService;
import android.content.Intent;

import android.os.AsyncTask;

import android.view.View;
import android.widget.Toast;

public class ModataActivity extends AppCompatActivity {

    private static ModataActivity gInstance = null;

    private static PowerCollector gPowerMonitor = null;

    private static ConnectivityMonitor gConnectionMonitor = null;

    //public static ArrayAdapter<UsageStatistics> gListAdapter;
    public static CustomAdapter gListAdapter;

    public static int gBatteryLevel;

    private ListView gListView;
    private Button gToggleButton;
    private Button gInfoButton;
    private boolean gMonitoring;
    private Intent gMonitoringIntent;
    private BroadcastReceiver gBatteryInfoReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Remove notification bar
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //Remove title bar
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);

        //set content view AFTER ABOVE sequence (to avoid crash)
        setContentView(R.layout.activity_modata);

        this.setTitle("Mo.DaTa");

        gListView = (ListView) findViewById(R.id.PackageListID);
        gToggleButton = (Button) findViewById(R.id.ToggleButtonID);
        gInfoButton = (Button) findViewById(R.id.InfoButtonID);

        gToggleButton.bringToFront();
        gInfoButton.bringToFront();
        gMonitoring = false;
        if ( DataMonitorService.isStarted ) {
            gToggleButton.setText("STOP");
            gMonitoring = true;
        }

        /*gListAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                DataMonitor.getMonitoredPackages() );*/

        DataMonitor.getMonitoredPackages();

        String[] prgmNameList = DataMonitor.mApps.toArray(new String[1]);
        Drawable[] prgmImages = new Drawable[prgmNameList.length];

        final PackageManager pm = getPackageManager();
        // get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(0);

        // loop through the list of installed packages and see if the selected
        // app is in the list
        for (ApplicationInfo packageInfo : packages) {
            int UID = packageInfo.uid;
            String package_name = packageInfo.packageName;
            String name = (String) pm.getApplicationLabel(packageInfo);

            if (!DataMonitor.mPackages.contains(package_name))
                continue;

            int index = 0;

            for (int i = 0; i < prgmNameList.length; i++ ) {
                if ( prgmNameList[i].equalsIgnoreCase( name ) ) {
                    index = i;
                    break;
                }
            }

            try {
                prgmImages[index] = pm.getApplicationIcon(package_name);
            } catch (NameNotFoundException e) {
                Log.e("Manager", "Icon not found for " + package_name);
            }
        }

        gListAdapter = new CustomAdapter(this, prgmNameList, prgmImages);

        gListView.setAdapter(gListAdapter);
        gListView.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        gToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gMonitoring) {
                    gToggleButton.setText("START");
                    //gMonitoringIntent.putExtra("STOP", true);
                    DataMonitor.isDone = true;
                    gMonitoring = false;
                    //stopService();
                } else {
                    gToggleButton.setText("STOP");
                    startService((View) null);
                    DataMonitor.isDone = false;
                    gMonitoring = true;
                }
            }
        });

        gInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = new AlertDialog.Builder(ModataActivity.this).create();
                alertDialog.setTitle("Device Info");
                StringBuilder info = new StringBuilder();
                info.append("Battery Capacity " + AndroidPowerCollector.getBatteryCapacity(ModataActivity.this)).append(" mAh\n");
                info.append("Wifi On " + AndroidPowerCollector.getWifiOnConsumption(ModataActivity.this)).append(" mAh\n");
                info.append("Wifi Active " + AndroidPowerCollector.getWifiActiveConsumption(ModataActivity.this)).append(" mAh\n");
                info.append("Mobile On " + AndroidPowerCollector.getRadioOnConsumption(ModataActivity.this)).append(" mAh\n");
                info.append("Mobile Active " + AndroidPowerCollector.getRadioActiveConsumption(ModataActivity.this)).append(" mAh\n");
                info.append("WiFi speed " + ConnectivityMonitor.getWifiSpeed(ModataActivity.this)).append(" MBps\n");
                info.append("WiFi Rssi " + ConnectivityMonitor.getWifiRssi(ModataActivity.this)).append(" dBm");
                alertDialog.setMessage(info.toString());
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });

        gBatteryInfoReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // this is where we deal with the data sent from the battery.
                int  health= intent.getIntExtra(BatteryManager.EXTRA_HEALTH,0);
                int  icon_small= intent.getIntExtra(BatteryManager.EXTRA_ICON_SMALL,0);
                int  level= intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);
                int  plugged= intent.getIntExtra(BatteryManager.EXTRA_PLUGGED,0);
                boolean  present= intent.getExtras().getBoolean(BatteryManager.EXTRA_PRESENT);
                int  scale= intent.getIntExtra(BatteryManager.EXTRA_SCALE,0);
                int  status= intent.getIntExtra(BatteryManager.EXTRA_STATUS,0);
                String  technology= intent.getExtras().getString(BatteryManager.EXTRA_TECHNOLOGY);
                int  temperature= intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0);
                int  voltage= intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE,0);

                gBatteryLevel = level;

                Log.i("Battery", level+"");
                Log.i("Battery", health+"");
                Log.i("Battery", scale+"");
                Log.i("Battery", voltage+"");
                Log.i("Battery", temperature+"");
            }
        };
        this.registerReceiver(gBatteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        gInstance = this;

        gPowerMonitor = new AndroidPowerCollector();
        gConnectionMonitor = new ConnectivityMonitor();

        Log.i("Connection", "" + ConnectivityMonitor.isConnectedWifi(this));
        Log.i("Connection", "" + ConnectivityMonitor.isConnectedMobile(this));
        Log.i("Connection", "" + ConnectivityMonitor.isConnectedFast(this));

        Log.i("battery", "Battery " + AndroidPowerCollector.getBatteryCapacity(this));
        Log.i("battery", "Battery " + AndroidPowerCollector.getWifiOnConsumption(this));
        Log.i("battery", "battery " + AndroidPowerCollector.getWifiActiveConsumption(this));
        Log.i("Radio", "battery " + AndroidPowerCollector.getRadioActiveConsumption(this));
        Log.i("Radio", "battery " + AndroidPowerCollector.getRadioOnConsumption(this));

        //this.startService((View)null);
        //gPowerMonitor.processApplicationUsage("Facebook");

        //getPakagesInfoUsingHashMap();
    }

    public static ModataActivity getActivityInstance() {
        return gInstance;
    }

    public void getPakagesInfoUsingHashMap() {
        final PackageManager pm = getPackageManager();
        // get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(0);

        // loop through the list of installed packages and see if the selected
        // app is in the list
        for (ApplicationInfo packageInfo : packages) {
            // get the UID for the selected app
            int UID = packageInfo.uid;
            String package_name = packageInfo.packageName;
            ApplicationInfo app = null;
            try {
                app = pm.getApplicationInfo(package_name, 0);
            } catch (NameNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            String name = (String) pm.getApplicationLabel(app);

            double received = (double) TrafficStats.getUidRxBytes(UID)

                    / (1024 * 1024);
            double send = (double) TrafficStats.getUidTxBytes(UID)
                    / (1024 * 1024);
            double total = received + send;

            Log.i("INFO ", "Name : " + name + " Bytes Sent: " + send + " Bytes Recvd: " + received);
        }

        //Class x = Object.class;
        try {
            /*Class<?> x = Class.forName("com.android.internal.os.BatteryStatsImpl");
            //Class<?> x = Class.forName("android.os.BatteryManager");
            Constructor[] constructors = x.getDeclaredConstructors();
            Field[] fields = x.getDeclaredFields();
            Method[] methods = x.getDeclaredMethods();
            for (Constructor constructor : constructors) {
                Log.i("Constructor ", "name : " + constructor.getName() );
            }
            for (Field field : fields) {
                Log.i("Field ", "name : " + field.getName() );
            }
            for (Method method : methods) {
                Log.i("Method ", "name : " + method.getName() );
            }
*/
            Class watchdogClass = Class.forName("com.android.server.Watchdog");
            Method getInstance = watchdogClass.getDeclaredMethod("getInstance");
            getInstance.setAccessible(true);
            Log.i("Class ", "Not Found 0");
            Method rebootSystem = watchdogClass.getDeclaredMethod("rebootSystem", String.class);
            rebootSystem.setAccessible(true);
            Log.i("Class ", "Not Found 1");
            Object watchdogInstance = getInstance.invoke(null);
            rebootSystem.invoke(watchdogInstance, "my reboot message");
            Log.i("Class ", "Not Found 2");
        } catch (ClassNotFoundException ex) {
            Log.i("Class ", "Not Found" );
        } catch (NoSuchMethodException ex) {
            Log.i("Class ", "No such method" );
        } catch (IllegalAccessException ex) {
            Log.i("Class ", "Illegal Access" );
        } catch (InvocationTargetException ex) {
            Log.i("Class ", "Illegal Access" );
        }
    }

    // Method to start the service
    public void startService(View view) {
        gMonitoringIntent = new Intent(getBaseContext(), DataMonitorService.class);

        dumpResults();

        startService(gMonitoringIntent);
    }

    private void dumpResults() {
        /*if ( !DataMonitor.STATS_FILE_WRITE ) {
            File readFile = new File(getFilesDir(), DataMonitor.STATS_FILE);
            try {
                Log.i("INFO", readFile.getCanonicalPath());
                Log.i("INFO", readFile.exists()+"");
            } catch (IOException ioex) {

            }
            if ( readFile.exists() ) {
                try {
                    Scanner reader = new Scanner(readFile);
                    while( reader.hasNext() ) {
                        Log.i("INFO 2", reader.nextLine());
                    }
                } catch (IOException ioex) {

                }
            }
        }*/
    }

    // Method to stop the service
    public void stopService(View view) {
        stopService(new Intent(getBaseContext(), DataMonitorService.class));
    }
}
