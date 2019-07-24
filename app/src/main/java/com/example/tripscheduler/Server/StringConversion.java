package com.example.tripscheduler.Server;

import java.util.ArrayList;

public class StringConversion {

   public static String arrayListToString_1(ArrayList<Integer> arrayList) {

        return arrayList.toString();
    }

    public static String arrayListToString_2(ArrayList<ArrayList<Integer>> arrayList) {

        return arrayList.toString();
    }

    public static String doubleToString_2(Double lat, Double lng) {

        return lat.toString() + " " + lng.toString();
    }

    public static String integerToString_3(Integer num1, Integer num2, Integer num3) {

        return num1.toString() + " " + num2.toString() + " " + num3.toString();
    }
}
