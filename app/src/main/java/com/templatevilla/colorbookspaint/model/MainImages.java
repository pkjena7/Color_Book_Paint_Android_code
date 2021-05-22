package com.templatevilla.colorbookspaint.model;

public class MainImages {


    public String category_id;
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

    public MainImages(String category_id, String name, String image) {
        this.category_id = category_id;
        this.name = name;
        this.image = image;
    }
}
