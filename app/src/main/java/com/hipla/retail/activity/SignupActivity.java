package com.hipla.retail.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hipla.retail.R;
import com.hipla.retail.databinding.ActivitySignupBinding;
import com.hipla.retail.model.Login_model;
import com.hipla.retail.model.Profile;
import com.hipla.retail.networking.NetworkUtility;
import com.hipla.retail.networking.PostStringRequest;
import com.hipla.retail.networking.StringRequestListener;
import com.hipla.retail.util.ErrorMessageDialog;
import com.hipla.retail.util.InternetConnectionDetector;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import io.paperdb.Paper;

/**
 * Created by FNSPL on 8/8/2017.
 */

public class SignupActivity extends AppCompatActivity implements StringRequestListener {

    ActivitySignupBinding binding_signup;
    private Login_model login_model;
    String TAG = "dev";
    ProgressDialog mProgressDialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding_signup = DataBindingUtil.setContentView(this, R.layout.activity_signup);

        login_model = new Login_model();
        binding_signup.setUser(login_model);
        binding_signup.setActivity(SignupActivity.this);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Please wait");
        mProgressDialog.setCancelable(false);

    }


    public void login() {
        /*Log.d(TAG, "login" + login_model.getUsername());
        login_model.getUsername();
*/
        startActivity(new Intent(this, LoginActivity.class));
        overridePendingTransition(R.anim.slideinfromleft, R.anim.slideouttoright);
        supportFinishAfterTransition();
    }

    public void signup() {

        if (InternetConnectionDetector.getInstant(SignupActivity.this).isConnectingToInternet()) {
       /* if (!binding_signup.usernameReg.getText().toString().isEmpty()) {*/
            if (isValidEmail(binding_signup.emailReg.getText().toString())) {
                if (!binding_signup.phoneReg.getText().toString().isEmpty()) {
                    if (!(binding_signup.phoneReg.getText().toString().length() < 10)) {
                        if (!binding_signup.passwordReg.getText().toString().isEmpty()) {
                            if (!(binding_signup.passwordReg.getText().toString().length() < 8)) {
                                if (!binding_signup.fnameReg.getText().toString().isEmpty()) {
                                    if (!binding_signup.lnameReg.getText().toString().isEmpty()) {
                                        if (!binding_signup.locationReg.getText().toString().isEmpty()) {
                                            if (!binding_signup.pincodeReg.getText().toString().isEmpty()) {
                                                mProgressDialog.show();
                                                HashMap<String, String> reg_params = new HashMap<>();
                                                reg_params.put("email", binding_signup.emailReg.getText().toString());
                                                reg_params.put("phone", binding_signup.phoneReg.getText().toString());
                                                reg_params.put("password", binding_signup.passwordReg.getText().toString());
                                                reg_params.put("Fname", binding_signup.fnameReg.getText().toString());
                                                reg_params.put("Lname", binding_signup.lnameReg.getText().toString());
                                                reg_params.put("address", binding_signup.locationReg.getText().toString());
                                                reg_params.put("pincode", binding_signup.pincodeReg.getText().toString());
                                                reg_params.put("device_type", "Android");
                                                //reg_params.put("deviceId", Settings.Secure.getString(SignupActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID));
                                                reg_params.put("regkey", Paper.book().read(NetworkUtility.TOKEN, ""));

                                                new PostStringRequest(SignupActivity.this, reg_params, SignupActivity.this, "SignUp",
                                                        NetworkUtility.BASEURL + NetworkUtility.REGISTRATION);

                                            } else {
                                                EditText editText = (EditText) findViewById(R.id.pincode_reg);
                                                editText.setError("Please Enter a Pincode !");
                                            }

                                        } else {
                                            EditText editText = (EditText) findViewById(R.id.location_reg);
                                            editText.setError("Please Enter a Location !");
                                        }
                                    } else {

                                        EditText editText = (EditText) findViewById(R.id.lname_reg);
                                        editText.setError("Please Enter Last Name !");

                                    }

                                } else {

                                    EditText editText = (EditText) findViewById(R.id.fname_reg);
                                    editText.setError("Please Enter First Name !");
                                }

                            } else {

                                EditText editText = (EditText) findViewById(R.id.password_reg);
                                editText.setError("Password  should be atleast 8 digit !");

                            }

                        } else {

                            EditText editText = (EditText) findViewById(R.id.password_reg);
                            editText.setError("Please Enter a Password !");

                        }

                    } else {

                        EditText editText = (EditText) findViewById(R.id.phone_reg);
                        editText.setError("Please Enter a valid Phone Number !");
                    }


                } else {

                    EditText editText = (EditText) findViewById(R.id.phone_reg);
                    editText.setError("Please Enter a Phone Number !");
                }

            } else {
                EditText editText = (EditText) findViewById(R.id.email_reg);
                editText.setError("Please Enter a valid Email !");

            }

        } else {

            // No internet
            Snackbar snackbar = Snackbar
                    .make(binding_signup.sclReg, "No internet connection!", Snackbar.LENGTH_LONG);
            snackbar.show();
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(this, LoginActivity.class));
        overridePendingTransition(R.anim.slideinfromleft, R.anim.slideouttoright);
        supportFinishAfterTransition();
    }


    @BindingAdapter("app:font_lite")
    public static void setFont_lite(TextView tv, Typeface typeface) {

        AssetManager assetManager = tv.getContext().getAssets();
        typeface = Typeface.createFromAsset(assetManager, "fonts/helviticaneulight.ttf");

        tv.setTypeface(typeface);
    }

    @BindingAdapter("app:font_thin")
    public static void setFont_thin(TextView tv, Typeface typeface) {

        AssetManager assetManager = tv.getContext().getAssets();
        typeface = Typeface.createFromAsset(assetManager, "fonts/helviticaneuthin.ttf");

        tv.setTypeface(typeface);
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    @Override
    public void onSuccess(String result, String type) throws JSONException {

        switch (type) {

            case "SignUp": {

                mProgressDialog.dismiss();
                Log.d(TAG, "onSuccess: signUp" + result);
                JSONObject response = new JSONObject(result);

                if (response.optString("status").equals("success")) {

                    GsonBuilder builder = new GsonBuilder();
                    builder.setPrettyPrinting();
                    Gson gson = builder.create();

                    Profile profile = gson.fromJson(response.getJSONArray("profile").getJSONObject(0).toString(), Profile.class);
                    Log.d(TAG, " gson: " + profile.toString());

                    if(profile.getEmail()!=null && profile.getPhone()!=null && (Integer)profile.getId()!=null) {
                        Paper.book().write(NetworkUtility.USER_INFO, profile);

                        login();
                    }else{
                        ErrorMessageDialog.getInstant(SignupActivity.this).show("Please try again.");
                    }

                } else {

                    ErrorMessageDialog.getInstant(SignupActivity.this).show(response.optString("message"));

                }

            }
        }

    }

    @Override
    public void onFailure(int responseCode, String responseMessage) {

        mProgressDialog.dismiss();
        ErrorMessageDialog.getInstant(SignupActivity.this).show(responseMessage);

    }

    @Override
    public void onStarted() {

    }
}
