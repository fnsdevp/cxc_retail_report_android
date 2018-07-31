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
import com.hipla.retail.model.Profile;
import com.hipla.retail.networking.NetworkUtility;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;


public class LoyalUsersAdapter extends RecyclerView.Adapter<LoyalUsersAdapter.ViewHolder> {
    private List<Profile> values;
    private Context context ;
    private OnRowClickListener mListener;

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView tv_profile_name;
        public TextView tv_phone;
        public ImageView iv_profile_pic , iv_openMap;
        public View layout;
        public TextView tv_customer_type;

        public ViewHolder(View v) {
            super(v);
            layout = v;

            tv_profile_name = (TextView) v.findViewById(R.id.tv_profile_name);
            tv_phone = (TextView) v.findViewById(R.id.tv_phone);
            tv_customer_type = (TextView) v.findViewById(R.id.tv_customer_type);
            iv_profile_pic = (ImageView) v.findViewById(R.id.iv_profile_pic);
            iv_openMap = (ImageView) v.findViewById(R.id.iv_openMap);

        }
    }

    public void notifyDataChange(List<Profile> data) {
        this.values = data ;
        notifyDataSetChanged();
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public LoyalUsersAdapter(Context context, List<Profile> myDataset) {
        this.context = context ;
        this.values = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public LoyalUsersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                           int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v = inflater.inflate(R.layout.item_loyal_customer, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.tv_profile_name.setText(String.format("%s", values.get(position).getFname()+" "+values.get(position).getLname()));
        holder.tv_phone.setText(String.format("Phone No : %s", values.get(position).getPhone()));
        holder.tv_customer_type.setText(String.format("Customer Type : %s", values.get(position).getUsertype().substring(0, 1).toUpperCase() +
                values.get(position).getUsertype().substring(1)));

        ImageLoader.getInstance().displayImage(NetworkUtility.IMAGE_BASEURL+ values.get(position).getImage(),
                holder.iv_profile_pic, NetworkUtility.ErrorWithLoaderRoundedCorner);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener!=null){
                    mListener.onRowClick(values.get(position), position);
                }
            }
        });

        holder.iv_openMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mListener!=null){
                    mListener.onMapOpen(values.get(position), position);
                }
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return values.size();
    }

    public interface OnRowClickListener{
        void onRowClick(Profile userProfile , int position);
        void onMapOpen(Profile userProfile , int position);
    }

    public void setOnRowClickListener(OnRowClickListener mListener){
        this.mListener = mListener;
    }
}