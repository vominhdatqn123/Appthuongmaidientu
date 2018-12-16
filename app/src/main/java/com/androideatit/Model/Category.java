package com.androideatit.Model;

/**
 * Created by 123456 on 2017/11/17.
 */

public class Category {
    private String Name;
    private String Image;

    public Category() {
    }

    public Category(String image, String name) {
        Name = name;
        Image = image;
    }


    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }
}
