package com.hipla.retail.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.hipla.retail.R;
import com.hipla.retail.application.MainApplication;
import com.hipla.retail.db.Db_helper;
import com.hipla.retail.model.ZoneInfo;
import com.hipla.retail.networking.NetworkUtility;
import com.hipla.retail.networking.PostStringRequest;
import com.hipla.retail.networking.StringRequestListener;
import com.navigine.naviginesdk.DeviceInfo;
import com.navigine.naviginesdk.Location;
import com.navigine.naviginesdk.NavigationThread;
import com.navigine.naviginesdk.NavigineSDK;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import io.paperdb.Paper;

public class MyService extends Service implements StringRequestListener {

    private static final String TAG = "NAVIGINE.Demo";
    public static final String ERROR = "error";
    public static final String DEVICE_LOCATION = "deviceLocation";
    private static final int UPDATE_TIMEOUT = 300;  // milliseconds
    private static final int ADJUST_TIMEOUT = 5000; // milliseconds
    private static final int ERROR_MESSAGE_TIMEOUT = 5000; // milliseconds
    private static final boolean ORIENTATION_ENABLED = true; // Show device orientation?

    private TimerTask mTimerTask = null;
    private Timer mTimer = new Timer();
    private Handler mHandler = new Handler();
    private boolean mAdjustMode = false;
    private long mErrorMessageTime = 0;
    private Location mLocation = null;
    private DeviceInfo mDeviceInfo = null; // Current device
    private Intent locatonFetch;
    private Intent errorMessage;
    private Db_helper db_helper;
    private ArrayList<ZoneInfo> zoneInfos = new ArrayList<>();
    private ArrayList<PointF[]> zoneInfoPoint = new ArrayList<>();
    private int currentZone = 0;
    private int zoneEnterOutCount = 0;
    private List<Integer> zoneNotificationShown = new ArrayList<>();

    public MyService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Paper.book().delete(DEVICE_LOCATION);

        if (MainApplication.isNavigineInitialized) {
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    mHandler.post(mRunnable);
                }
            };
            mTimer.schedule(mTimerTask, UPDATE_TIMEOUT, UPDATE_TIMEOUT);
        } else {
            new InitTask(getApplicationContext()).execute();
        }

        db_helper = new Db_helper(getApplicationContext());
        if (db_helper != null) {
            zoneInfos = db_helper.getAllZoneInfo();

            for (ZoneInfo zoneInfo :
                    zoneInfos) {
                zoneInfoPoint.add(convertToPoints(zoneInfo));
            }
        }

    }

    @Override
    public void onDestroy() {
        //MainApplication.finish();
        if(mTimer!=null){
            mTimer.cancel();
        }

        if(mTimerTask!=null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }

        super.onDestroy();
    }

    @Override
    public void onSuccess(String result, String type) throws JSONException {

    }

    @Override
    public void onFailure(int responseCode, String responseMessage) {

    }

    @Override
    public void onStarted() {

    }

    class InitTask extends AsyncTask<Void, Void, Boolean> {
        private Context mContext = null;
        private String mErrorMsg = null;

        public InitTask(Context context) {
            mContext = context.getApplicationContext();
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            if (!MainApplication.initialize(getApplicationContext())) {
                mErrorMsg = "Error downloading location information! Please, try again later or contact technical support";
                return Boolean.FALSE;
            }
            Log.d(TAG, "Initialized!");
            if (!NavigineSDK.loadLocation(MainApplication.LOCATION_ID, 30)) {
                mErrorMsg = "Error downloading location information! Please, try again later or contact technical support";
                return Boolean.FALSE;
            }
            return Boolean.TRUE;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            if (result.booleanValue()) {
                // Starting main activity

                mTimerTask = new TimerTask() {
                    @Override
                    public void run() {
                        mHandler.post(mRunnable);
                    }
                };
                mTimer.schedule(mTimerTask, UPDATE_TIMEOUT, UPDATE_TIMEOUT);

            } else {
                Toast.makeText(mContext, mErrorMsg, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }
    }

    final Runnable mRunnable =
            new Runnable() {
                public void run() {
                    if (MainApplication.Navigation == null) {
                        Log.d(TAG, "Sorry, navigation is not supported on your device!");
                        return;
                    }

                    final long timeNow = NavigineSDK.currentTimeMillis();

                    if (mErrorMessageTime > 0 && timeNow > mErrorMessageTime + ERROR_MESSAGE_TIMEOUT) {
                        mErrorMessageTime = 0;
                    }

                    // Start navigation if necessary
                    if (MainApplication.Navigation.getMode() == NavigationThread.MODE_IDLE)
                        MainApplication.Navigation.setMode(NavigationThread.MODE_NORMAL);

                    // Get device info from NavigationThread
                    mDeviceInfo = MainApplication.Navigation.getDeviceInfo();
                    Paper.book().write(DEVICE_LOCATION, mDeviceInfo);

                    if (mDeviceInfo.errorCode == 0) {
                        mErrorMessageTime = 0;

                        locatonFetch = new Intent("android.intent.action.MAIN");
                        sendBroadcast(locatonFetch);

                        //calculateZone(mDeviceInfo);

                    } else {
                        switch (mDeviceInfo.errorCode) {
                            case 4:
                                errorMessage = new Intent("android.intent.action.SUCCESSLOCATION");
                                errorMessage.putExtra(ERROR, "Fetching your location...");
                                sendBroadcast(errorMessage);
                                //Paper.book().delete(CONST.WELCOME_NOTIFICATION);
                                break;

                            case 8:
                            case 30:
                                errorMessage = new Intent("android.intent.action.SUCCESSLOCATION");
                                errorMessage.putExtra(ERROR, "You are out of navigation zone.");
                                sendBroadcast(errorMessage);
                                //Paper.book().delete(CONST.WELCOME_NOTIFICATION);
                                break;

                            default:
                                errorMessage = new Intent("android.intent.action.SUCCESSLOCATION");
                                errorMessage.putExtra(ERROR, String.format(Locale.ENGLISH,
                                        "Something is wrong with location '%s' (error code %d)! " +
                                                "Please, contact technical support!",
                                        mLocation.name, mDeviceInfo.errorCode));
                                sendBroadcast(errorMessage);
                                //Paper.book().delete(CONST.WELCOME_NOTIFICATION);
                                break;
                        }
                    }
                }
            };

    private void calculateZone(DeviceInfo mDeviceInfo) {
        Log.d(TAG, "X : " + mDeviceInfo.x + " Y: " + mDeviceInfo.y);
        for (int index = 0; index < zoneInfoPoint.size(); index++) {
            boolean inZone = contains(zoneInfoPoint.get(index), new PointF(mDeviceInfo.x, mDeviceInfo.y));

        }
    }

    private PointF[] convertToPoints(ZoneInfo zoneInfo) {
        PointF[] pointFs = new PointF[4];

        String[] pointsA = zoneInfo.getPointA().split(",");
        PointF pointA = new PointF(Float.parseFloat(pointsA[0]), Float.parseFloat(pointsA[1]));
        pointFs[0] = pointA;

        String[] pointsB = zoneInfo.getPointB().split(",");
        PointF pointB = new PointF(Float.parseFloat(pointsB[0]), Float.parseFloat(pointsB[1]));
        pointFs[1] = pointB;

        String[] pointsC = zoneInfo.getPointC().split(",");
        PointF pointC = new PointF(Float.parseFloat(pointsC[0]), Float.parseFloat(pointsC[1]));
        pointFs[2] = pointC;

        String[] pointsD = zoneInfo.getPointD().split(",");
        PointF pointD = new PointF(Float.parseFloat(pointsD[0]), Float.parseFloat(pointsD[1]));
        pointFs[3] = pointD;

        return pointFs;
    }

    public boolean contains(PointF[] points, PointF test) {
        int i;
        int j;
        boolean result = false;
        /*for (i = 0, j = points.length - 1; i < points.length; j = i++) {
            if ((points[i].y > test.y) != (points[j].y > test.y) &&
                    (test.x < (points[j].x - points[i].x) * (test.y - points[i].y) / (points[j].y - points[i].y) + points[i].x)) {
                result = !result;
            }
        }*/
        if (((points[0].x < test.x) && (points[0].y > test.y))) {
            if (((points[3].x < test.x) && (points[3].y < test.y))) {
                if (((points[1].x > test.x) && (points[1].y > test.y))) {
                    if (((points[2].x > test.x) && (points[2].y < test.y))) {
                        result = true;
                    }
                }
            }
        }
        return result;
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.mipmap.ic_launcher : R.mipmap.ic_launcher;
    }


    private void requestInZoneData(String userId, String zoneId) {
        HashMap<String, String> parameter = new HashMap<>();
        parameter.put("userid", String.format("%s", userId));
        parameter.put("zoneid", String.format("%s", zoneId));

        new PostStringRequest(getApplicationContext(), parameter, MyService.this,
                "registerZoneIn", NetworkUtility.BASEURL + NetworkUtility.FORGET_PASSWORD);
    }

    private void requestOutZoneData(String userId, String zoneId, String out_time, String spent_time) {
        HashMap<String, String> parameter = new HashMap<>();
        parameter.put("userid", String.format("%s", userId));
        parameter.put("zoneid", String.format("%s", zoneId));
        parameter.put("out_time", String.format("%s", out_time));
        parameter.put("spent_time", String.format("%s", spent_time));

        new PostStringRequest(getApplicationContext(), parameter, MyService.this,
                "registerZoneOut", NetworkUtility.BASEURL + NetworkUtility.FORGET_PASSWORD);
    }

}
