package com.hipla.retail.db;

import android.provider.BaseColumns;

/**
 * Created by FNSPL on 8/25/2017.
 */

public class Db_contracts {


    private Db_contracts(){}

    public static class Entries implements BaseColumns{

        public  static final String TABLE_USER = "user";
        public  static final String COLUMN_UID = "uid";
        public  static final String COLUMN_FNAME = "fname";
        public  static final String COLUMN_LNAME = "lname";
        public  static final String COLUMN_MOBILE = "mobile";
        public  static final String COLUMN_EMAIL = "email";
        public  static final String COLUMN_LOCATION = "location";
        public  static final String COLUMN_PIN = "pin";
        public static final String COLUMN_USER_TYPE = "userType";
    }

    public static class Product implements BaseColumns{

        public  static final String TABLE_PRODUCT = "product";
        public  static final String COLUMN_PRODUCT_ID = "product_id";
        public  static final String COLUMN_PRODUCT_NAME = "product_name";
        public  static final String COLUMN_PRODUCT_IMAGE = "product_image";
        public  static final String COLUMN_PRICE = "product_price";
        public  static final String COLUMN_QUANTITY = "product_quantity";

    }

    public static class ZoneInfo implements BaseColumns{

        public  static final String TABLE_ZONE = "zoneInfo";
        public  static final String COLUMN_ZONE_ID = "zoneId";
        public  static final String COLUMN_CENTER = "centerPoint";
        public  static final String COLUMN_POINT_A = "pointA";
        public  static final String COLUMN_POINT_B = "pointB";
        public  static final String COLUMN_POINT_C = "pointC";
        public  static final String COLUMN_POINT_D = "pointD";

    }
}
