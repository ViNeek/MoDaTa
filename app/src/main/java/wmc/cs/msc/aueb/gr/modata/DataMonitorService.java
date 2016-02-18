package wmc.cs.msc.aueb.gr.modata;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import android.content.Context;

import android.app.PendingIntent;
import android.app.Notification;

/**
 * Created by ellen on 2/10/16.
 */
public class DataMonitorService extends Service {

    public static boolean isStarted;

    private DataMonitor gDataMonitor;

        @Override
        public IBinder onBind(Intent arg0) {
            return null;
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            // Let it continue running until it is stopped.
            Intent inten = new Intent(this, ModataActivity.class);
            inten.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, inten, 0);

            Notification noti = new Notification.Builder(getApplicationContext())
                    .setContentTitle("Pratikk")
                    .setContentText("Subject")
                    .setContentIntent(pendingIntent)
                    .build();

            startForeground(1234, noti);

            if ( !isStarted ) {
                Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();

                new DataMonitor(intent, getBaseContext()).execute();

                isStarted = true;
            }

            return START_STICKY;
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            isStarted = false;
            Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
        }

    public void finish() {
        gDataMonitor.finish();
    }
}
