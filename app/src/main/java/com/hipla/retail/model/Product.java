package com.hipla.retail.model;

/**
 * Created by FNSPL on 10/31/2017.
 */

public class Product {

    private int id;
    private String title;
    private String description;
    private String path;
    private int quantity;
    private int price;

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        if (title != null)
            return title;
        else
            return "";
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        if (description != null)
            return description;
        else
            return "";
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPath() {
        if (path != null)
            return path;
        else
            return "";
    }

    public void setPath(String path) {
        this.path = path;
    }
}
