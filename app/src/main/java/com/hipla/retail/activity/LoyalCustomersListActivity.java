package com.hipla.retail.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hipla.retail.R;
import com.hipla.retail.adapter.LoyalUsersAdapter;
import com.hipla.retail.databinding.ActivityLoyalCustomersListBinding;
import com.hipla.retail.db.Db_helper;
import com.hipla.retail.fragment.NavigineMapDialogNew;
import com.hipla.retail.model.Order;
import com.hipla.retail.model.Product;
import com.hipla.retail.model.Profile;
import com.hipla.retail.model.ZoneInfo;
import com.hipla.retail.networking.NetworkUtility;
import com.hipla.retail.networking.PostStringRequest;
import com.hipla.retail.networking.StringRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import io.paperdb.Paper;

public class LoyalCustomersListActivity extends AppCompatActivity implements StringRequestListener, LoyalUsersAdapter.OnRowClickListener, NavigineMapDialogNew.OnDialogListener {

    private ActivityLoyalCustomersListBinding binding;
    private ProgressDialog progressDialog;
    private LoyalUsersAdapter mAdapter;
    private boolean showingDialog=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_loyal_customers_list);
        binding.setMycart(LoyalCustomersListActivity.this);

        initView();
    }

    private void initView() {
        progressDialog = new ProgressDialog(LoyalCustomersListActivity.this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(LoyalCustomersListActivity.this);
        binding.recyclerLoyalCustomers.setLayoutManager(layoutManager);
        binding.recyclerLoyalCustomers.addItemDecoration(new SimpleDividerItemDecoration(LoyalCustomersListActivity.this));

        mAdapter = new LoyalUsersAdapter(this, new ArrayList<Profile>());
        mAdapter.setOnRowClickListener(this);
        binding.recyclerLoyalCustomers.setAdapter(mAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();

        requestData();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onRowClick(Profile userProfile , int position) {
        Paper.book().write(NetworkUtility.LOYAL_USER, userProfile);

        startActivity(new Intent(LoyalCustomersListActivity.this, LoyalCustomerDetailsActivity.class));
    }

    @Override
    public void onMapOpen(Profile userProfile, int position) {
        Db_helper db_helper = new Db_helper(LoyalCustomersListActivity.this);
        ZoneInfo zoneInfo = db_helper.getZoneInfo(""+userProfile.getZone_code());
        if(zoneInfo!=null) {
            String[] location = zoneInfo.getCenterPoint().split(",");

            if(!showingDialog) {
                NavigineMapDialogNew mapDialog = new NavigineMapDialogNew();
                Bundle bundle = new Bundle();
                bundle.putString(NetworkUtility.POINTX, location[0]);
                bundle.putString(NetworkUtility.POINTY, location[1]);
                mapDialog.setArguments(bundle);
                mapDialog.setOnDialogListener(LoyalCustomersListActivity.this);
                if (mapDialog != null && mapDialog.getDialog() != null
                        && mapDialog.getDialog().isShowing()) {
                    //dialog is showing so do something
                } else {
                    //dialog is not showing
                    showingDialog = true;
                    mapDialog.show(getSupportFragmentManager(), "mapDialog");
                }
            }

        }else{
            Toast.makeText(LoyalCustomersListActivity.this, getResources().getString(R.string.no_data_available), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDissmiss() {
        showingDialog=false;
    }

    public class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {
        private Drawable mDivider;

        public SimpleDividerItemDecoration(Context context) {
            mDivider = context.getResources().getDrawable(R.drawable.line_divider);
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getChildAt(i);

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mDivider.getIntrinsicHeight();

                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }
    }

    private void requestData() {
        if(!progressDialog.isShowing()){
            progressDialog.show();
        }

        HashMap<String, String> parameter = new HashMap<>();

        new PostStringRequest(LoyalCustomersListActivity.this, parameter, LoyalCustomersListActivity.this,
                "getCustomerInfo", NetworkUtility.BASEURL + NetworkUtility.LOYAL_CUSTOMER);
    }

    @Override
    public void onSuccess(String result, String type) throws JSONException {
        if(progressDialog.isShowing()){
            progressDialog.dismiss();
        }

        Log.d("Response", "Responce: " + result);
        try{

            JSONObject response = new JSONObject(result);
            if(response.optString("status").equalsIgnoreCase("success")){

                GsonBuilder builder = new GsonBuilder();
                builder.setPrettyPrinting();
                Gson gson = builder.create();

                JSONArray order_list = response.getJSONArray("interestlist");

                Profile[] products = gson.fromJson(order_list.toString(), Profile[].class);
                List<Profile> profileList = Arrays.asList(products);

                if(profileList.size()>0) {
                    mAdapter.notifyDataChange(profileList);
                } else {
                    Toast.makeText(LoyalCustomersListActivity.this, getResources().getString(R.string.no_loyal_customer),
                            Toast.LENGTH_SHORT).show();
                    finish();
                }

            }else{
                Toast.makeText(LoyalCustomersListActivity.this, response.optString("message"), Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
            Toast.makeText(LoyalCustomersListActivity.this, getResources().getString(R.string.no_loyal_customer), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onFailure(int responseCode, String responseMessage) {
        if(progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    @Override
    public void onStarted() {

    }

}
