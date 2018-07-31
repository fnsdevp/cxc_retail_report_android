package com.hipla.retail.adapter;

/**
 * Created by User on 8/3/2017.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hipla.retail.R;
import com.hipla.retail.model.Product;
import com.hipla.retail.model.ProductInfo;
import com.hipla.retail.networking.NetworkUtility;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;


public class InterestedProductListAdapter extends RecyclerView.Adapter<InterestedProductListAdapter.ViewHolder> {
    private List<ProductInfo> values;
    private Context context ;

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView tv_name_itemGrocery;
        public TextView tv_price;
        public ImageView iv_product ;
        public LinearLayout ll_spinner_dialog ;
        public View layout;
        private  ImageView iv_min_cart , iv_add_cart, iv_delete;
        private  TextView tv_quantity_cart;

        public ViewHolder(View v) {
            super(v);
            layout = v;

            tv_name_itemGrocery = (TextView) v.findViewById(R.id.tv_product_name);
            tv_price = (TextView) v.findViewById(R.id.tv_price);
            iv_product = (ImageView) v.findViewById(R.id.iv_product);

        }
    }

    public void notifyDataChange(List<ProductInfo> data) {
        this.values = data ;
        notifyDataSetChanged();
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public InterestedProductListAdapter(Context context, List<ProductInfo> myDataset) {
        this.context = context ;
        this.values = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public InterestedProductListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                      int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v = inflater.inflate(R.layout.item_interested_product, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.tv_name_itemGrocery.setText(String.format("%s", values.get(position).getTitle()));
        holder.tv_price.setText(String.format("%s Rs/-", values.get(position).getPrice()));

        ImageLoader.getInstance().displayImage(values.get(position).getUrl()+values.get(position).getProduct_image(),
                holder.iv_product, NetworkUtility.ErrorWithLoaderNormalCorner);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return values.size();
    }

}