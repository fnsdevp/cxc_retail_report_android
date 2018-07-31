package com.hipla.retail.model;

import android.databinding.BaseObservable;
import android.graphics.Typeface;

/**
 * Created by FNSPL on 8/8/2017.
 */

public class Login_model extends BaseObservable {

    public String username , password , email ,phone_number ,location ;
    public Typeface typeface ;

    public Typeface getTypeface() {
      //  Typeface typeface = Typeface.createFromAsset(c)

        return typeface;
    }

    public void setTypeface(Typeface typeface) {
        this.typeface = typeface;
    }



    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone_number() {
        return phone_number;
    }
    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
}
