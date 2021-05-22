package com.templatevilla.colorbookspaint.model;

public class SubImages {


    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getImage_id() {
        return image_id;
    }

    public void setImage_id(String image_id) {
        this.image_id = image_id;
    }

    public String category_id, image_id;
    public String name;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String image;

    public SubImages(String category_id, String image_id, String name, String image) {
        this.category_id = category_id;
        this.image_id = image_id;
        this.name = name;
        this.image = image;
    }
}
