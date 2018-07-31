package com.hipla.retail.networking;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;

import com.hipla.retail.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

/**
 * Created by Admin on 10/19/2016.
 */
public class NetworkUtility {

    public static final String BASEURL="http://cxc.gohipla.com/retail/";
    public static final String IMAGE_BASEURL="http://cxc.gohipla.com/retail/";

    public static final String ORDER_DETAIL = "ordpurchasedbyuniqueid.php";
    public static final String GATE_STATUS = "gatestatus.php";
    public static final String LOYAL_CUSTOMER = "all_interested_user.php";
    public static final String LOYAL_CUSTOMER_DETAILS = "interestedproductbyuser.php";
    public static final String REGISTRATION = "sales_register.php";
    public static final String LOGIN = "sales_login.php";
    public static final String UPDATE_DEVICE_TOKEN = "sales_token_update.php";
    public static final String RECOMENDED_FOR_YOU = "productbycatbyuserpreference.php";
    public static final String FORGET_PASSWORD = "";

    public static final String ORDER_HISTORY = "orderHistory";
    public static final String LOYAL_USER = "loyalCustomer";
    public static final String TOKEN = "tokenId";
    public static final String USER_INFO = "userInfo";
    public static final String POINTX = "PointX";
    public static final String POINTY = "PointY";

    public static DisplayImageOptions ErrorWithLoaderNormalCorner = new DisplayImageOptions.Builder()
            .resetViewBeforeLoading(true)
            .showImageOnFail(R.drawable.no_image_found)
            .showImageOnLoading(R.drawable.loading_image)
            .cacheInMemory(true)
            .cacheOnDisk(false)
            .considerExifParams(true)
            .imageScaleType(ImageScaleType.EXACTLY)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

    public static DisplayImageOptions ErrorWithLoaderRoundedCorner = new DisplayImageOptions.Builder()
            .showImageOnFail(R.drawable.no_profile_image)
            .showImageOnLoading(R.drawable.no_profile_image)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .imageScaleType(ImageScaleType.EXACTLY)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .displayer(new RoundedBitmapDisplayer(1000))
            .postProcessor(new BitmapProcessor() {
                @Override
                public Bitmap process(Bitmap bmp) {

                    int dimension = getSquareCropDimensionForBitmap(bmp);
                    bmp = ThumbnailUtils.extractThumbnail(bmp, dimension, dimension);
                    return bmp;
                }
            })
            .build();

    public static int getSquareCropDimensionForBitmap(Bitmap bitmap) {
        //use the smallest dimension of the image to crop to
        return Math.min(bitmap.getWidth(), bitmap.getHeight());
    }

}
