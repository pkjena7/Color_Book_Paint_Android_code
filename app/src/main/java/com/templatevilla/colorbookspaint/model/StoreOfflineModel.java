package com.templatevilla.colorbookspaint.model;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public class StoreOfflineModel {

    public String category_id;
    public String name;
    public String categoryURL;
    public boolean isCategoryDownload=false;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String path;


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

    public Bitmap getBitmap() {
        return bitmap;
    }


    public List<String> getSubcategoryList() {
        return subEasycategoryList;
    }

    public void setEasySubcategoryList(List<String> subcategoryList) {
        this.subEasycategoryList = subcategoryList;
    }

    public List<String> subEasycategoryList = new ArrayList<>();


    public void setSubHardcategoryList(List<String> subHardcategoryList) {
        this.subHardcategoryList = subHardcategoryList;
    }

    public List<String> getSubHardcategoryList() {
        return subHardcategoryList;
    }

    public List<String> subHardcategoryList = new ArrayList<>();

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    private Bitmap bitmap;

    public StoreOfflineModel(String category_id, String name, String image, String path) {
        this.category_id = category_id;
        this.name = name;
        this.image = image;
        this.categoryURL = path;
//        this.path = path;
    }


    public StoreOfflineModel(String category_id, String name, String image) {
        this.category_id = category_id;
        this.name = name;
        this.image = image;
        this.path = path;
    }


}
