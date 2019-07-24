package com.example.tripscheduler.Place;

import android.graphics.Bitmap;

public class TPlace {

  private String name, location, label;
  private String image;
//  private String address;

  public TPlace(String name, String location, String label, String image) {
    this.name = name;
    this.location = location;
    this.label = label;
    this.image = image;
//    this.address = address;
  }

  public String getData(String key) {
    switch (key) {
      case "name":
        return name;

      case "location":
        return location;

      case "label":
        return label;

//      case "address":
//        return address;
    }
    return null;
  }

  public String getImage() {
    return image;
  }
}
