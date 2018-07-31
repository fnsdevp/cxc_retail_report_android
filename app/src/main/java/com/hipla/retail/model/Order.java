package com.hipla.retail.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FNSPL on 10/31/2017.
 */

public class Order {

    private int id=0;
    private int user_id;
    private String order_unique_id;
    private int total_quantity=0;
    private int total_amount=0;
    private String buyer_name;
    private String delivery_address;
    private String landmark;
    private String pincode;
    private String phone;
    private String order_date;
    private int transaction_id=0;
    private String order_current_status;
    private String gate_pass;
    private List<Product> product = new ArrayList<>();

    public String getGate_pass() {
        return gate_pass;
    }

    public void setGate_pass(String gate_pass) {
        this.gate_pass = gate_pass;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getOrder_unique_id() {
        return order_unique_id;
    }

    public void setOrder_unique_id(String order_unique_id) {
        this.order_unique_id = order_unique_id;
    }

    public int getTotal_quantity() {
        return total_quantity;
    }

    public void setTotal_quantity(int total_quantity) {
        this.total_quantity = total_quantity;
    }

    public int getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(int total_amount) {
        this.total_amount = total_amount;
    }

    public String getBuyer_name() {
        return buyer_name;
    }

    public void setBuyer_name(String buyer_name) {
        this.buyer_name = buyer_name;
    }

    public String getDelivery_address() {
        return delivery_address;
    }

    public void setDelivery_address(String delivery_address) {
        this.delivery_address = delivery_address;
    }

    public String getLandmark() {
        return landmark;
    }

    public void setLandmark(String landmark) {
        this.landmark = landmark;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public int getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(int transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getOrder_current_status() {
        return order_current_status;
    }

    public void setOrder_current_status(String order_current_status) {
        this.order_current_status = order_current_status;
    }

    public List<Product> getProduct() {
        return product;
    }

    public void setProduct(List<Product> product) {
        this.product = product;
    }
}
