package com.hipla.retail.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.databinding.BindingAdapter;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.hipla.retail.R;
import com.hipla.retail.application.MainApplication;
import com.hipla.retail.db.Db_helper;
import com.hipla.retail.helper.MarshmallowPermissionHelper;
import com.hipla.retail.model.Login_model;
import com.hipla.retail.model.ZoneInfo;
import com.hipla.retail.networking.NetworkUtility;
import com.navigine.naviginesdk.NavigineSDK;

import io.paperdb.Paper;

public class SplashActivity extends AppCompatActivity {

    private static final int REQUEST_ALL_PERMISSION = 1000;
    private Login_model login_model;
    private String TAG="Retail";
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);
        login_model = new Login_model();

        initView();
    }

    private void initView() {

        setUpZoneData();

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (haveNetworkConnection()) {
                    checkNavinginePermissions();
                }else{
                    showNetConnectionDialog();
                }
            }
        }, 3000);

    }

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    private void showNetConnectionDialog() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(SplashActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(SplashActivity.this);
        }
        builder.setTitle("No Internet Connection")
                .setMessage("Please enable your network connection to proceed")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        if (haveNetworkConnection()) {
                            checkNavinginePermissions();
                        }else{
                            showNetConnectionDialog();
                        }
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void checkNavinginePermissions() {
        if (Build.VERSION.SDK_INT > 22) {
            if (MarshmallowPermissionHelper.getAllNaviginePermission(null
                    , this, REQUEST_ALL_PERMISSION)) {
                if(!MainApplication.isNavigineInitialized) {
                    (new InitTask(this)).execute();
                }else{
                    if(Paper.book().read(NetworkUtility.USER_INFO,null)!=null){
                        startActivity(new Intent(SplashActivity.this, ScaningActivity.class));
                        overridePendingTransition(R.anim.slideinfromright, R.anim.slideouttoleft);
                        supportFinishAfterTransition();
                    }else{
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        overridePendingTransition(R.anim.slideinfromright, R.anim.slideouttoleft);
                        supportFinishAfterTransition();
                    }
                }
            }
        } else {
            if(!MainApplication.isNavigineInitialized) {
                (new InitTask(this)).execute();
            }else{
                if(Paper.book().read(NetworkUtility.USER_INFO,null)!=null){
                    startActivity(new Intent(SplashActivity.this, ScaningActivity.class));
                    overridePendingTransition(R.anim.slideinfromright, R.anim.slideouttoleft);
                    supportFinishAfterTransition();
                }else{
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    overridePendingTransition(R.anim.slideinfromright, R.anim.slideouttoleft);
                    supportFinishAfterTransition();
                }
            }
        }
    }

    @BindingAdapter("app:font_lite")
    public static  void setFont_lite(TextView tv , Typeface typeface){
        AssetManager assetManager = tv.getContext().getAssets();
        typeface = Typeface.createFromAsset(assetManager, "fonts/helviticaneulight.ttf");

        tv.setTypeface(typeface);
    }

    @BindingAdapter("app:font_thin")
    public static  void setFont_thin(TextView tv , Typeface typeface){

        AssetManager assetManager = tv.getContext().getAssets();
        typeface = Typeface.createFromAsset(assetManager, "fonts/helviticaneuthin.ttf");

        tv.setTypeface(typeface);
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
            if (!NavigineSDK.loadLocation(MainApplication.LOCATION_ID, 60)) {
                mErrorMsg = "Error downloading location information! Please, try again later or contact technical support";
                return Boolean.FALSE;
            }
            return Boolean.TRUE;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            if (result.booleanValue()) {
                // Starting main activity
                if(Paper.book().read(NetworkUtility.USER_INFO,null)!=null){
                    startActivity(new Intent(SplashActivity.this, ScaningActivity.class));
                    overridePendingTransition(R.anim.slideinfromright, R.anim.slideouttoleft);
                    supportFinishAfterTransition();
                }else{
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    overridePendingTransition(R.anim.slideinfromright, R.anim.slideouttoleft);
                    supportFinishAfterTransition();
                }
            } else {
                Toast.makeText(mContext, mErrorMsg, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }
    }

    // Callback with the request from calling requestPermissions(...)
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        switch (requestCode) {
            case REQUEST_ALL_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[3] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[4] == PackageManager.PERMISSION_GRANTED) {
                    if(!MainApplication.isNavigineInitialized) {
                        (new InitTask(this)).execute();
                    }
                }
                return;
            }

            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    private void setUpZoneData(){
        Db_helper db_helper = new Db_helper(getApplicationContext());

        /*db_helper.insert_zone(new ZoneInfo(1,"676,1374","16.5,9","19,9","19,7","16.5,7"));
        db_helper.insert_zone(new ZoneInfo(2,"163,923","16,13","18,13","18,11","16,11"));
        db_helper.insert_zone(new ZoneInfo(3,"737,234","16,30","18,30","18,26","16,26"));
        db_helper.insert_zone(new ZoneInfo(4,"447,1313","11,13","15,13","15,9","11,9"));
        db_helper.insert_zone(new ZoneInfo(5,"563,1537","14,7","18,7","18,5","14,5"));
        db_helper.insert_zone(new ZoneInfo(6,"939,602","26,35","29,35","29,32","26,32"));
        db_helper.insert_zone(new ZoneInfo(7,"1014,1295","28,12","31,12","31,9","28,9"));*/

        db_helper.insert_zone(new ZoneInfo(1, "202,1018", "5.2,17", "7.4,17", "7.4,12", "5.2,12"));
        db_helper.insert_zone(new ZoneInfo(2,"199,766","5.5,23.1","7.8,23.1","7.8,21.3","5.5,21.3"));
        db_helper.insert_zone(new ZoneInfo(3,"306,1461","7.5,6.5","9.2,6.5","9.2,5.5","7.5,5.5"));
        db_helper.insert_zone(new ZoneInfo(4,"563,1458","12.5,6","15.5,6","15.5,5","12.5,5"));
        db_helper.insert_zone(new ZoneInfo(5,"884,1453","19.6,6","22,6","22,4.5","19,4.5"));
        db_helper.insert_zone(new ZoneInfo(6,"967,984","21.7,16","23.2,16","23.2,12.5","21.7,12.5"));
        db_helper.insert_zone(new ZoneInfo(7,"969,756","22,20.8","23,20.8","23,19","22,19"));
        db_helper.insert_zone(new ZoneInfo(8,"582,415","11,28.5","19,28.5","19,27.5","11,27.5"));

    }

}
