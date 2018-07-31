package com.hipla.retail.activity;

import android.app.Dialog;
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
import com.hipla.retail.adapter.MyCartAdapter;
import com.hipla.retail.databinding.ActivityCartDetailBinding;
import com.hipla.retail.model.Order;
import com.hipla.retail.model.Product;
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

public class CartDetailActivity extends AppCompatActivity implements StringRequestListener {

    private ActivityCartDetailBinding binding;
    private MyCartAdapter mAdapter;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_cart_detail);
        binding.setMycart(CartDetailActivity.this);

        initView();
    }

    private void initView() {

        progressDialog = new ProgressDialog(CartDetailActivity.this);
        progressDialog.setMessage(getResources().getString(R.string.please_wait));

        final Order orderDetail = Paper.book().read(NetworkUtility.ORDER_HISTORY);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(CartDetailActivity.this);
        binding.recyclerGrocery.setLayoutManager(layoutManager);
        binding.recyclerGrocery.addItemDecoration(new SimpleDividerItemDecoration(CartDetailActivity.this));

        mAdapter = new MyCartAdapter(CartDetailActivity.this, orderDetail.getProduct());
        binding.recyclerGrocery.setAdapter(mAdapter);

        binding.price.setText(String.format("Subtotal : %s Rs/-", orderDetail.getTotal_amount()));
        binding.tvItem.setText(String.format("Items : %s", orderDetail.getTotal_quantity()));

        if(orderDetail.getGate_pass().equalsIgnoreCase("0")){
            binding.btnApproved.setText(getString(R.string.approved));
            binding.btnApproved.setBackground(getResources().getDrawable(R.drawable.btn_green));
            binding.btnApproved.setTextColor(getResources().getColor(R.color.colorWhite));
        }else{
            binding.btnApproved.setText(getString(R.string.already_approved));
            binding.btnApproved.setBackground(getResources().getDrawable(R.drawable.btn_background_grey));
            binding.btnApproved.setTextColor(getResources().getColor(R.color.colorGreydeep));
        }

        binding.btnApproved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(orderDetail.getGate_pass().equalsIgnoreCase("0")) {
                    requestData(orderDetail.getOrder_unique_id());
                }else{
                    finish();
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Paper.book().delete(NetworkUtility.ORDER_HISTORY);

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

    @Override
    public void onBackPressed() {

    }

    private void requestData(String qrData) {
        if(!progressDialog.isShowing()){
            progressDialog.show();
        }

        HashMap<String, String> parameter = new HashMap<>();
        parameter.put("order_unique_id", String.format("%s", qrData));
        //parameter.put("order_unique_id", "OD15001200145");

        new PostStringRequest(CartDetailActivity.this, parameter, CartDetailActivity.this,
                "getProductInfo", NetworkUtility.BASEURL + NetworkUtility.GATE_STATUS);
    }

    @Override
    public void onSuccess(String result, String type) throws JSONException {
        if(progressDialog.isShowing()){
            progressDialog.dismiss();
        }

        Log.d("Response", "Responce: " + result);
        try{

            JSONObject response = new JSONObject(result);
            if(response.optString("status").equalsIgnoreCase("success")) {
                Toast.makeText(CartDetailActivity.this, getResources().getString(R.string.order_approved), Toast.LENGTH_SHORT).show();
                finish();
            }

        }catch (Exception e){
            Toast.makeText(CartDetailActivity.this, getResources().getString(R.string.please_try_again), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFailure(int responseCode, String responseMessage) {

    }

    @Override
    public void onStarted() {

    }

}
