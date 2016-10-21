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
    private String mCloth_tx, mCloth_content,
            mAir_conditioning_content, mAir_conditioning_tx,
            mUltraviolet_radiation_content,
            mUltraviolet_radiation_tx, mSport_tx, mSport_content;

    public String getCloth_tx() {
        return mCloth_tx;
    }

    public void setCloth_tx(String cloth_tx) {
        mCloth_tx = cloth_tx;
    }

    public String getCloth_content() {
        return mCloth_content;
    }

    public void setCloth_content(String cloth_content) {
        mCloth_content = cloth_content;
    }

    public String getAir_conditioning_content() {
        return mAir_conditioning_content;
    }

    public void setAir_conditioning_content(String air_conditioning_content) {
        mAir_conditioning_content = air_conditioning_content;
    }

    public String getAir_conditioning_tx() {
        return mAir_conditioning_tx;
    }

    public void setAir_conditioning_tx(String air_conditioning_tx) {
        mAir_conditioning_tx = air_conditioning_tx;
    }

    public String getUltraviolet_radiation_content() {
        return mUltraviolet_radiation_content;
    }

    public void setUltraviolet_radiation_content(String ultraviolet_radiation_content) {
        mUltraviolet_radiation_content = ultraviolet_radiation_content;
    }

    public String getUltraviolet_radiation_tx() {
        return mUltraviolet_radiation_tx;
    }

    public void setUltraviolet_radiation_tx(String ultraviolet_radiation_tx) {
        mUltraviolet_radiation_tx = ultraviolet_radiation_tx;
    }

    public String getSport_tx() {
        return mSport_tx;
    }

    public void setSport_tx(String sport_tx) {
        mSport_tx = sport_tx;
    }

    public String getSport_content() {
        return mSport_content;
    }

    public void setSport_content(String sport_content) {
        mSport_content = sport_content;
    }

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
