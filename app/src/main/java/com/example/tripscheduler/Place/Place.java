package com.example.tripscheduler.Place;

import android.graphics.Bitmap;

public class Place {

    private String name, location, label;
    private String image;

    public Place(String name, String location, String label, String image) {
        this.name = name;
        this.location = location;
        this.label = label;
        this.image = image;
    }

    public String getData(String key) {
        switch (key) {
            case "name":
                return name;

            case "location":
                return location;

            case "label":
                return label;
        }
        return null;
    }

    public String getImage()
    {
        return image;
    }
}
