package com.example.tripscheduler.Server;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Model {

    public class DirectionResults {
        @SerializedName("routes")
        private List<Route> routes;
        public List<Route> getRoutes() {
            return routes;
        }
    }

    public class Route{
        @SerializedName("legs")
        private List<Legs> legses;

        public List<Legs> getLegses() {
            return legses;
        }

        public void setLegses(List<Legs> legses) {
            this.legses = legses;
        }
    }

    public class Distance{
        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        @SerializedName("text")
        private String text;

        @SerializedName("value")
        private int value;
    }

    public class Duration{
        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        @SerializedName("text")
        private String text;

        @SerializedName("value")
        private int value;
    }

    public class Legs{
        private Distance distance;
        private Duration duration;

        public Distance getDistance() {
            return distance;
        }

        public void setDistance(Distance distance) {
            this.distance = distance;
        }

        public Duration getDuration() {
            return duration;
        }

        public void setDuration(Duration duration) {
            this.duration = duration;
        }
    }

    public class Steps {
        private Location start_location;
        private Location end_location;
        private OverviewPolyLine polyline;

        public Location getStart_location() {
            return start_location;
        }

        public Location getEnd_location() {
            return end_location;
        }

        public OverviewPolyLine getPolyline() {
            return polyline;
        }
    }

    public class OverviewPolyLine {

        @SerializedName("points")
        public String points;

        public String getPoints() {
            return points;
        }
    }

    public class Location {
        private double lat;
        private double lng;

        public double getLat() {
            return lat;
        }

        public double getLng() {
            return lng;
        }
    }
}
