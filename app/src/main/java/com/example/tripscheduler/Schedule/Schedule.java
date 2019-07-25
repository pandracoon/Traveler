package com.example.tripscheduler.Schedule;

import java.io.Serializable;
import java.util.ArrayList;

public class Schedule implements Serializable, Comparable<Schedule> {

    private String email, title, name, location, label, memo, start, duration;

    public Schedule(String email, String title, String name, String location, String label,
                    String memo, String start, String duration) {
        this.email = email;
        this.title = title;
        this.name = name;
        this.location = location;
        this.label = label;
        this.memo = memo;
        this.start = start;
        this.duration = duration;
    }

    public String getData(String key) {
        switch (key) {
            case "email":
                return email;

            case "title":
                return title;

            case "name":
                return name;

            case "location":
                return location;

            case "label":
                return label;

            case "memo":
                return memo;

            case "start":
                return start;

            case "duration":
                return duration;
        }
        return null;
    }

    public String getTitle() {
        return title;
    }


    public Integer getMinuteTime() {
        String tempTime = this.start.split(" ")[0].split("\"")[1];
        String tempMinute = this.start.split(" ")[1].split("\"")[0];
        Integer time = Integer.parseInt(tempTime);
        Integer minute = Integer.parseInt(tempMinute);

        return time * 60 + minute;
    }

    @Override
    public int compareTo(Schedule schedule) {
        if (this.getMinuteTime() < schedule.getMinuteTime()) {
            return -1;
        } else if (this.getMinuteTime() > schedule.getMinuteTime()) {
            return 1;
        }
        return 0;
    }
}