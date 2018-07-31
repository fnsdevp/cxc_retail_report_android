package com.hipla.retail.firebase;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.hipla.retail.R;
import com.hipla.retail.activity.LoyalCustomersListActivity;
import com.hipla.retail.activity.ScaningActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private String TAG = "Firebase";

    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a");

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        //Log.d(TAG, "Notification TripMessageData Body: " + remoteMessage.getNotification().getBody());
        //Log.d(TAG, "Notification TripMessageData Data: " + remoteMessage.getData().toString());

        Map<String, String> data = remoteMessage.getData();

        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        // Get info from the currently active task
        List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);

        try {

            if (data.containsKey("pushType") && data.get("pushType").equalsIgnoreCase("NeedHelp")) {

                sendNotification(getResources().getString(R.string.app_name), data.get("message"));
            }

        }catch (Exception e){

        }

    }

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String title, String message) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(getNotificationIcon())
                        .setColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary))
                        .setVibrate(new long[]{1000, 1000, 1000})
                        .setLights(Color.RED, 3000, 3000)
                        .setContentTitle(""+title)
                        .setContentText("" + message)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(message));
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, LoyalCustomersListActivity.class);
        //resultIntent.putExtra("NotificationMessage", "This is from notification");

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(ScaningActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);
        mBuilder.setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(0, mBuilder.build());
    }


    private int getNotificationIcon() {
        boolean useWhiteIcon = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.mipmap.ic_launcher_round : R.mipmap.ic_launcher;
    }

}
