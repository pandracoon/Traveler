package com.example.tripscheduler.Server;

import java.util.ArrayList;

public class StringConversion {

    public String arrayListToString_1(ArrayList<Integer> arrayList) {

        return arrayList.toString();
    }

    public String arrayListToString_2(ArrayList<ArrayList<Integer>> arrayList) {

        return arrayList.toString();
    }

    public String integerToString_2(Integer num1, Integer num2) {

        return num1.toString() + " " + num2.toString();
    }

    public String integerToString_3(Integer num1, Integer num2, Integer num3) {

        return num1.toString() + " " + num2.toString() + " " + num3.toString();
    }
}
