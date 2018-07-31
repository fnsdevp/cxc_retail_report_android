package com.hipla.retail.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.AssetManager;
import android.databinding.BindingAdapter;
import android.databinding.ViewDataBinding;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hipla.retail.R;
import com.hipla.retail.networking.NetworkUtility;
import com.hipla.retail.networking.PostStringRequest;
import com.hipla.retail.networking.StringRequestListener;
import com.hipla.retail.util.ErrorMessageDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by FNSPL on 8/8/2017.
 */

public class ForgetPasswordActivity extends AppCompatActivity implements StringRequestListener {


    private ViewDataBinding binding_forget_password;
    private ProgressDialog mProgressDialog;
    private EditText et_email;
    private Button btn_login, btn_submit;
    private String TAG="getPassword";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_forget_password);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Please wait..");
        mProgressDialog.setCancelable(false);

        init();
    }

    private void init(){
        et_email = (EditText) findViewById(R.id.et_email);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_submit = (Button) findViewById(R.id.btn_submit);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recoverPassword();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        supportFinishAfterTransition();
        overridePendingTransition(R.anim.slideinfromleft, R.anim.slideouttoright);
    }

    public void recoverPassword(){
        if(et_email.getText().toString().trim().isEmpty()){
            et_email.setError("Please Enter a Email Id !");
        }else{
            mProgressDialog.show();
            HashMap<String, String> reg_params = new HashMap<>();
            reg_params.put("email", et_email.getText().toString().trim());

            new PostStringRequest(ForgetPasswordActivity.this, reg_params,
                    ForgetPasswordActivity.this, "getPassword", NetworkUtility.BASEURL + NetworkUtility.FORGET_PASSWORD);
        }
    }

    public void login(){
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

    @Override
    public void onSuccess(String result, String type) throws JSONException {
        switch (type) {
            case "getPassword": {
                mProgressDialog.dismiss();
                Log.d(TAG, "onSuccess: getPassword" + result);
                JSONObject response = new JSONObject(result);

                if (response.optString("status").equalsIgnoreCase("success")) {
                    login();
                    Toast.makeText(ForgetPasswordActivity.this, ""+response.getString("message"), Toast.LENGTH_SHORT).show();
                } else {
                    ErrorMessageDialog.getInstant(ForgetPasswordActivity.this).show(response.optString("message"));
                }
            }
        }

    }

    @Override
    public void onFailure(int responseCode, String responseMessage) {
        mProgressDialog.dismiss();
        ErrorMessageDialog.getInstant(ForgetPasswordActivity.this).show(responseMessage);
    }

    @Override
    public void onStarted() {

    }
}
