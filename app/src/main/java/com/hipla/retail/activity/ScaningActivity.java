package com.hipla.retail.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hipla.retail.R;
import com.hipla.retail.databinding.ActivityScaningBinding;
import com.hipla.retail.helper.MarshmallowPermissionHelper;
import com.hipla.retail.model.Order;
import com.hipla.retail.model.Product;
import com.hipla.retail.model.Profile;
import com.hipla.retail.networking.NetworkUtility;
import com.hipla.retail.networking.PostStringRequest;
import com.hipla.retail.networking.StringRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import io.paperdb.Paper;
import me.ydcool.lib.qrmodule.activity.QrScannerActivity;

public class ScaningActivity extends AppCompatActivity implements StringRequestListener {

    private static final int REQUEST_CAMERA_STORAGE_PERMISSION = 100;
    private ProgressDialog progressDialog;
    private ActivityScaningBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_scaning);
        binding.setActivity(ScaningActivity.this);

        progressDialog = new ProgressDialog(ScaningActivity.this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));

        binding.btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCameraPermission();
            }
        });

        binding.btnLoyalCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ScaningActivity.this, LoyalCustomersListActivity.class));
            }
        });

    }

    public void checkCameraPermission() {
        if (Build.VERSION.SDK_INT > 22) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {

                if (Build.VERSION.SDK_INT > 22) {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {

                    }

                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                            REQUEST_CAMERA_STORAGE_PERMISSION);
                }
            } else {
                Intent intent = new Intent(ScaningActivity.this, QrScannerActivity.class);
                startActivityForResult(intent, QrScannerActivity.QR_REQUEST_CODE);
            }
        } else {
            Intent intent = new Intent(ScaningActivity.this, QrScannerActivity.class);
            startActivityForResult(intent, QrScannerActivity.QR_REQUEST_CODE);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == QrScannerActivity.QR_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Log.d("AAAAAA", resultCode == RESULT_OK
                        ? data.getExtras().getString(QrScannerActivity.QR_RESULT_STR)
                        : "Scanned Nothing!");

                try {
                    String qrData = data.getExtras().getString(QrScannerActivity.QR_RESULT_STR);
                    //Toast.makeText(ScaningActivity.this, ""+qrData, Toast.LENGTH_SHORT).show();

                    requestData(qrData);

                } catch (Exception e) {

                }

            } else {
                //Toast.makeText(ScaningActivity.this, getResources().getString(R.string.scan_nothing), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void requestData(String qrData) {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }

        HashMap<String, String> parameter = new HashMap<>();
        parameter.put("order_unique_id", String.format("%s", qrData));
        //parameter.put("order_unique_id", "OD15001200145");

        new PostStringRequest(ScaningActivity.this, parameter, ScaningActivity.this,
                "getProductInfo", NetworkUtility.BASEURL + NetworkUtility.ORDER_DETAIL);
    }

    private void requestData() {
        if (Paper.book().read(NetworkUtility.USER_INFO) != null) {

            Profile profile = Paper.book().read(NetworkUtility.USER_INFO);

            HashMap<String, String> parameter = new HashMap<>();
            parameter.put("regkey", String.format("%s", Paper.book().read(NetworkUtility.TOKEN, "")));
            parameter.put("sales_id", String.format("%s", profile.getId()));
            parameter.put("device_type", String.format("%s", "Android"));

            new PostStringRequest(ScaningActivity.this, parameter, ScaningActivity.this,
                    "updateRegToken", NetworkUtility.BASEURL + NetworkUtility.UPDATE_DEVICE_TOKEN);
        }
    }

    @Override
    public void onSuccess(String result, String type) throws JSONException {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        Log.d("Response", "Responce: " + result);
        try {

            switch (type) {
                case "getProductInfo":
                    JSONObject response = new JSONObject(result);
                    if (response.optString("status").equalsIgnoreCase("success")) {

                        GsonBuilder builder = new GsonBuilder();
                        builder.setPrettyPrinting();
                        Gson gson = builder.create();

                        JSONArray order_list = response.getJSONArray("order_list");
                        Order orderDetail = gson.fromJson(order_list.getJSONObject(0).toString(), Order.class);

                        Product[] products = gson.fromJson(order_list.getJSONObject(0).
                                getJSONArray("product").toString(), Product[].class);
                        List<Product> productList = Arrays.asList(products);
                        //Toast.makeText(this, ""+productList.size(), Toast.LENGTH_SHORT).show();

                        orderDetail.setProduct(productList);

                        Paper.book().write(NetworkUtility.ORDER_HISTORY, orderDetail);

                        startActivity(new Intent(ScaningActivity.this, CartDetailActivity.class));

                    } else {
                        Toast.makeText(ScaningActivity.this, response.optString("message"), Toast.LENGTH_SHORT).show();
                    }

                    break;


            }

        } catch (Exception e) {
            Toast.makeText(ScaningActivity.this, getResources().getString(R.string.please_try_again), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFailure(int responseCode, String responseMessage) {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onStarted() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        switch (requestCode) {
            case REQUEST_CAMERA_STORAGE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent intent = new Intent(ScaningActivity.this, QrScannerActivity.class);
                    startActivityForResult(intent, QrScannerActivity.QR_REQUEST_CODE);
                }
                return;
            }

            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    public void logout() {
        Paper.book().delete(NetworkUtility.USER_INFO);

        startActivity(new Intent(ScaningActivity.this, LoginActivity.class));
        supportFinishAfterTransition();
        overridePendingTransition(R.anim.slideinfromleft, R.anim.slideouttoright);
    }

    @Override
    protected void onResume() {
        super.onResume();

        requestData();

        if(!haveNetworkConnection()){
            showNetConnectionDialog();
        }
    }

    public boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {

            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;

            /*if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;*/

        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    private void showNetConnectionDialog() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(ScaningActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(ScaningActivity.this);
        }
        builder.setTitle("No Internet Connection")
                .setMessage("Please connect to local WIFI and then proceed.")
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        if (!haveNetworkConnection()) {
                            showNetConnectionDialog();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

}
