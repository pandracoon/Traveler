package com.example.tripscheduler.Travel;

public class Travel {

  private String email, title, area, startDate, endDate;

  public Travel(String email, String title, String area, String startDate, String endDate) {
    this.email = email;
    this.title = title;
    this.area = area;
    this.startDate = startDate;
    this.endDate = endDate;
  }

  public String getData(String key) {
    switch (key) {
      case "email":
        return email;

      case "title":
        return title;

      case "area":
        return area;

      case "startDate":
        return startDate;

      case "endDate":
        return endDate;
    }
    return null;
  }

  public String getTitle() {
    return title;
  }

  public String getDate() {
    return startDate + " ~ " + endDate;
  }

  public String getArea() {
    return area;
  }
}
