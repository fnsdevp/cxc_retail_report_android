package com.hipla.retail.model;

/**
 * Created by FNSPL on 8/22/2017.
 */


public class Profile {
    private int id;
    private String fname;
    private String lname;
    private String email;
    private String phone;
    private String address;
    private String pincode;
    private String registrationdate;
    private String status;
    private String usertype;
    private String zone_code;
    private String image;

    public String getImage() {
        if(image!=null)
            return image;
        else
            return "";
    }

    public void setImage(String image) {
        this.image = image;
    }


    public String getZone_code() {
        if(zone_code!=null && !zone_code.isEmpty())
        return zone_code;
        else
            return "1";
    }

    public void setZone_code(String zone_code) {
        this.zone_code = zone_code;
    }

    public String getUsertype() {
        if(usertype!=null && usertype.equalsIgnoreCase("loyal"))
        return "Premium";
        else
            return "";
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getPincode() {
        return ""+pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getRegistrationdate() {
        return registrationdate;
    }

    public void setRegistrationdate(String registrationdate) {
        this.registrationdate = registrationdate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    @Override
    public String toString() {
        return "Profile{" +
                "id=" + id +
                ", fname=" + fname +
                ", lname='" + lname + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", pincode=" + pincode +
                ", registrationdate='" + registrationdate + '\'' +
                ", status=" + status +
                '}';
    }
}
