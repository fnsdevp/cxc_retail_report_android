package com.hipla.retail.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hipla.retail.R;
import com.hipla.retail.application.MainApplication;
import com.hipla.retail.databinding.ActivityLoginBinding;
import com.hipla.retail.helper.MarshmallowPermissionHelper;
import com.hipla.retail.model.Login_model;
import com.hipla.retail.model.Profile;
import com.hipla.retail.networking.NetworkUtility;
import com.hipla.retail.networking.PostStringRequest;
import com.hipla.retail.networking.StringRequestListener;
import com.hipla.retail.util.ErrorMessageDialog;
import com.hipla.retail.util.InternetConnectionDetector;
import com.navigine.naviginesdk.NavigineSDK;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity implements StringRequestListener {

    private static final int REQUEST_ALL_PERMISSION = 100;
    private ActivityLoginBinding binding;
    private Login_model login_model;
    private static final String TAG = "Navigine.Demo";
    private static final int LOADER_TIMEOUT = 30; // seconds
    private int mLoader = 0;
    private int mLoaderState = 0;
    private long mLoaderTime = 0;
    private HashMap<String , String> login_params;
    private TextView mErrorLabel = null;
    private TimerTask mTimerTask = null;
    private Handler mHandler = new Handler();
    private Timer mTimer = new Timer();
    private ProgressDialog mProgresDialog ;
    private boolean isEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        login_model = new Login_model();
        binding.setUser(login_model);
        binding.setActivity(LoginActivity.this);

        mProgresDialog = new ProgressDialog(this);
        mProgresDialog.setMessage("Please wait...");
        mProgresDialog.setCancelable(false);

        //Log.d("Test",""+ CONST.distance(32.9697, -96.80322, 29.46786, -98.53506, "K") + " Miles\n");
        setBluetoothEnable(true);
        //calculateDistance();
        //setUpZoneData();

        if (haveNetworkConnection()) {
            checkNavinginePermissions();
        }else{
            showNetConnectionDialog();
        }

    }

    private void calculateDistance() {
        String distanceAPIUrl = "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&" +
                "origins=23.535632,87.301350&destinations=23.535108,87.302932&" +
                "key=AIzaSyC4B_g2Itz8ayzVU-D0C8gJDLw79A333ng";

        new PostStringRequest(LoginActivity.this, login_params, LoginActivity.this, "Distance",
                distanceAPIUrl);
    }

    public void login(){
        //Log.d(TAG, "login" + login_model.getUsername());

        if(InternetConnectionDetector.getInstant(LoginActivity.this).isConnectingToInternet()) {
            String username = binding.edtUsernameLogin.getText().toString().trim();
            String password = binding.edtPasswordLogin.getText().toString().trim();

            if (!username.isEmpty()) {
                if (!password.isEmpty()) {
                    if (password.length() < 6) {

                        binding.edtPasswordLogin.setError("Please enter a valid password.");

                    } else {
                        mProgresDialog.show();
                        login_params = new HashMap<>();
                        login_params.put("email", username);
                        login_params.put("password", password);
                        login_params.put("regkey", ""+ Paper.book().read(NetworkUtility.TOKEN, ""));
                        login_params.put("device_type", "Android");
                        Log.d(TAG, "login: params"+login_params);
                        new PostStringRequest(LoginActivity.this, login_params, LoginActivity.this, "Login",
                                NetworkUtility.BASEURL + NetworkUtility.LOGIN);
                    }
                } else {

                    // passsword empty
                    EditText edt =  (EditText) findViewById(R.id.edt_password_login);
                    edt.setError("Please Enter a password");

                }

            } else {
// username empty

               EditText edt =  (EditText) findViewById(R.id.edt_username_login);
               edt.setError("Please Enter a username");
            }

        }else{
            Snackbar snackbar = Snackbar
                    .make(binding.rlLogin, "No internet connection!", Snackbar.LENGTH_LONG);
                    /*.setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                        }
                    });*/
            snackbar.show();

            Log.d(TAG, "login: snackk bar ");
            // network error
        }

    }

    public void signup(){
        Log.d(TAG, "signup");
        startActivity(new Intent(LoginActivity.this,SignupActivity.class));
        overridePendingTransition(R.anim.slideinfromright, R.anim.slideouttoleft);
        supportFinishAfterTransition();
    }

    public void forgetpassoword(){
        startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
        overridePendingTransition(R.anim.slideinfromright, R.anim.slideouttoleft);
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

    @Override
    public void onSuccess(String result, String type) throws JSONException {

        switch (type){

            case "Login":{

                Log.d(TAG,"result"+result);
                mProgresDialog.dismiss();

                JSONObject response = new JSONObject(result);

                if(response.optString("status").equals("success")) {

                    GsonBuilder builder = new GsonBuilder();
                    builder.setPrettyPrinting();
                    Gson gson = builder.create();

                    Profile profile = gson.fromJson(response.getJSONArray("profile").getJSONObject(0).toString() , Profile.class);

                    Log.d(TAG, " gson: "+profile.toString());

                    if(profile.getEmail()!=null && profile.getPhone()!=null && (Integer)profile.getId()!=null) {
                        Paper.book().write(NetworkUtility.USER_INFO, profile);

                        startActivity(new Intent(LoginActivity.this, ScaningActivity.class));
                        overridePendingTransition(R.anim.slideinfromright, R.anim.slideouttoleft);
                        supportFinishAfterTransition();

                    }else{
                        ErrorMessageDialog.getInstant(LoginActivity.this).show("Please try again.");
                    }

                }else {

                    ErrorMessageDialog.getInstant(LoginActivity.this).show(response.optString("message"));
                }

            }
            break;
            case "Distance":
                Log.d(TAG, "result"+result);
                try{
                    JSONObject jsonObject = new JSONObject(result);
                    if(jsonObject.optString("status").equalsIgnoreCase("OK")) {
                        JSONObject rows = jsonObject.getJSONArray("rows").getJSONObject(0);
                        JSONObject elements = rows.getJSONArray("elements").getJSONObject(0);
                        if(elements.getString("status").equalsIgnoreCase("OK")) {
                            String time = elements.getJSONObject("duration").getString("value");
                        }
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
                break;

        }

    }

    @Override
    public void onFailure(int responseCode, String responseMessage) {
        mProgresDialog.dismiss();

        Log.d(TAG,"failure"+responseMessage);
    }

    @Override
    public void onStarted() {
        Log.d(TAG,"onstarted");
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
            if(mProgresDialog.isShowing()){
                mProgresDialog.dismiss();
            }
            if (result.booleanValue()) {
                // Starting main activity
                if(Paper.book().read(NetworkUtility.USER_INFO,null)!=null){
                    startActivity(new Intent(LoginActivity.this, ScaningActivity.class));
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

            if(!mProgresDialog.isShowing()){
                mProgresDialog.show();
            }
        }
    }

    private boolean setBluetoothEnable(boolean enable) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(bluetoothAdapter != null) {
             isEnabled = bluetoothAdapter.isEnabled();
        }else{
            return false ;
        }

        if (enable && !isEnabled) {
            return bluetoothAdapter.enable();
        } else if (!enable && isEnabled) {
            return bluetoothAdapter.disable();
        }
        // No need to change bluetooth state
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void showNetConnectionDialog() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(LoginActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(LoginActivity.this);
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

    private void checkNavinginePermissions() {
        if (Build.VERSION.SDK_INT > 22) {
            if (MarshmallowPermissionHelper.getAllNaviginePermission(null
                    , this, REQUEST_ALL_PERMISSION)) {
                if(!MainApplication.isNavigineInitialized) {
                    (new InitTask(this)).execute();
                }else{
                    if(Paper.book().read(NetworkUtility.USER_INFO,null)!=null){
                        startActivity(new Intent(LoginActivity.this, ScaningActivity.class));
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
                    startActivity(new Intent(LoginActivity.this, ScaningActivity.class));
                    overridePendingTransition(R.anim.slideinfromright, R.anim.slideouttoleft);
                    supportFinishAfterTransition();
                }
            }
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

   /* private void setUpZoneData(){
        Db_helper db_helper = new Db_helper(getApplicationContext());

        db_helper.insert_zone(new ZoneInfo(1,"807,1133","20,22","22,22","22,20","20,20"));
        db_helper.insert_zone(new ZoneInfo(2,"573,1324","16,13","18,13","18,11","16,11"));
        db_helper.insert_zone(new ZoneInfo(3,"720,840","16,30","18,30","18,26","16,26"));
        db_helper.insert_zone(new ZoneInfo(4,"610,504","12,39","15,39","15,36","12,36"));
        db_helper.insert_zone(new ZoneInfo(5,"566,1154","16,19","18,19","18,17","16,17"));
        db_helper.insert_zone(new ZoneInfo(6,"555,692","15,36","17,36","17,34","15,34"));
        db_helper.insert_zone(new ZoneInfo(7,"555,692","6,49","10,49","10,36","6,36"));
    }*/

}

