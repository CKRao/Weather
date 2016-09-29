package com.example.ckrao.myapplication;

import android.graphics.drawable.GradientDrawable;

/**
 * Created by CKRAO on 2016/9/28.
 */

public class CurrentWeatherBean {
    private String temp;
    private String week;
    private String city;
    private String weather;

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
