package com.hipla.retail.model;

/**
 * Created by FNSPL on 10/24/2017.
 */

public class ProductInfo {

    private int id;
    private String bar_code;
    private String title;
    private String description;
    private String category_name;
    private String price;
    private String product_image;
    private String product_image_folder;
    private String zone_id;
    private String unit;
    private String url;
    private String[] images;

    public int getId() {
        if ((Integer) id != null) {
            return id;
        } else {
            return 0;
        }
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBar_code() {
        if (bar_code != null)
            return bar_code;
        else
            return "";
    }

    public void setBar_code(String bar_code) {
        this.bar_code = bar_code;
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

    public String getCategory_name() {
        if (category_name != null)
            return category_name;
        else
            return "";
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getPrice() {
        if (price != null)
            return price;
        else
            return "";
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getProduct_image() {
        if (product_image != null)
            return product_image;
        else
            return "";
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public String getProduct_image_folder() {
        if (product_image_folder != null)
            return product_image_folder;
        else
            return "";
    }

    public void setProduct_image_folder(String product_image_folder) {
        this.product_image_folder = product_image_folder;
    }

    public String getZone_id() {
        if (zone_id != null)
            return zone_id;
        else
            return "";
    }

    public void setZone_id(String zone_id) {
        this.zone_id = zone_id;
    }

    public String getUnit() {
        if (unit != null)
            return unit;
        else
            return "";
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getUrl() {
        if (url != null)
            return url;
        else
            return "";
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String[] getImages() {
        if(images!=null)
        return images;
        else
            return new String[0];
    }

    public void setImages(String[] images) {
        this.images = images;
    }
}
