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
import com.hipla.retail.adapter.InterestedProductListAdapter;
import com.hipla.retail.adapter.MyCartAdapter;
import com.hipla.retail.databinding.ActivityLoyalCustomerDetailsBinding;
import com.hipla.retail.model.Order;
import com.hipla.retail.model.Product;
import com.hipla.retail.model.ProductInfo;
import com.hipla.retail.model.Profile;
import com.hipla.retail.networking.NetworkUtility;
import com.hipla.retail.networking.PostStringRequest;
import com.hipla.retail.networking.StringRequestListener;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import io.paperdb.Paper;

public class LoyalCustomerDetailsActivity extends AppCompatActivity implements StringRequestListener {

    private Profile profile;
    private ActivityLoyalCustomerDetailsBinding binding;
    private ProgressDialog progressDialog;
    private InterestedProductListAdapter mAdapter;
    private List<ProductInfo> productDataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_loyal_customer_details);
        binding.setActivity(LoyalCustomerDetailsActivity.this);

        initView();
    }

    private void initView() {
        progressDialog = new ProgressDialog(LoyalCustomerDetailsActivity.this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));

        profile = Paper.book().read(NetworkUtility.LOYAL_USER);

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.tvProfileName.setText(String.format("%s", profile.getFname()+" "+profile.getLname()));
        binding.tvPhone.setText(String.format("Phone No : %s", profile.getPhone()));
        binding.tvCustomerType.setText(String.format("Customer Type : %s", profile.getUsertype().substring(0, 1).toUpperCase() +
                profile.getUsertype().substring(1)));

        ImageLoader.getInstance().displayImage(NetworkUtility.IMAGE_BASEURL+ profile.getImage(),
                binding.ivProfilePic, NetworkUtility.ErrorWithLoaderRoundedCorner);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(LoyalCustomerDetailsActivity.this);
        binding.recyclerProductList.setLayoutManager(layoutManager);
        binding.recyclerProductList.addItemDecoration(new SimpleDividerItemDecoration(LoyalCustomerDetailsActivity.this));

        getRecomendedProducts();
    }

    private void requestData() {
        if(!progressDialog.isShowing()){
            progressDialog.show();
        }

        HashMap<String, String> parameter = new HashMap<>();
        parameter.put("user_id", String.format("%s", profile.getId()));

        new PostStringRequest(LoyalCustomerDetailsActivity.this, parameter, LoyalCustomerDetailsActivity.this,
                "getProductInfo", NetworkUtility.BASEURL + NetworkUtility.LOYAL_CUSTOMER_DETAILS);
    }

    public void getRecomendedProducts() {
        if(!progressDialog.isShowing()){
            progressDialog.show();
        }

        HashMap<String, String> parameter = new HashMap<>();
        parameter.put("user_id", String.format("%s",profile.getId()));

        new PostStringRequest(LoyalCustomerDetailsActivity.this, parameter, LoyalCustomerDetailsActivity.this,
                "getRecomendedList", NetworkUtility.BASEURL + NetworkUtility.RECOMENDED_FOR_YOU);
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

                JSONArray productlist = response.getJSONArray("productlist");

                for (int i = 0; i < productlist.length(); i++) {
                    ProductInfo product = gson.fromJson(productlist.getJSONObject(i).toString(), ProductInfo.class);
                    int imageSize =  productlist.getJSONObject(i).getJSONArray("images").length();
                    String[] images = new String[imageSize];
                    for (int j = 0; j < imageSize; j++) {
                        images[j] = (String) productlist.getJSONObject(i).getJSONArray("images").get(j);
                    }
                    product.setImages(images);

                    productDataList.add(product);
                }

                if(productDataList.size()>0) {
                    mAdapter = new InterestedProductListAdapter(LoyalCustomerDetailsActivity.this, productDataList);
                    binding.recyclerProductList.setAdapter(mAdapter);
                }else{
                    Toast.makeText(LoyalCustomerDetailsActivity.this, "No information found.", Toast.LENGTH_SHORT).show();
                }

            }else{
                Toast.makeText(LoyalCustomerDetailsActivity.this, response.optString("message"),
                        Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e){
            Toast.makeText(LoyalCustomerDetailsActivity.this, getResources().getString(R.string.please_try_again),
                    Toast.LENGTH_SHORT).show();
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

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

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

}
