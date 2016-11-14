package com.example.ckrao.myapplication.Model;

import android.graphics.drawable.GradientDrawable;

/**
 * Created by CKRAO on 2016/9/28.
 */

public class CurrentWeatherBean {
    private String temp;
    private String week;
    private String city;
    private String weather;
    private String img, img_01, img_02, img_03,
            temp_01, temp_02, temp_03;
    private String mCloth_tx, mCloth_content,
            mAir_conditioning_content, mAir_conditioning_tx,
            mUltraviolet_radiation_content,
            mUltraviolet_radiation_tx, mSport_tx, mSport_content;
    private String rainfall, humidity, pm;

    public String getRainfall() {
        return rainfall;
    }

    public void setRainfall(String rainfall) {
        this.rainfall = rainfall;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getPm() {
        return pm;
    }

    public void setPm(String pm) {
        this.pm = pm;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getImg_01() {
        return img_01;
    }

    public void setImg_01(String img_01) {
        this.img_01 = img_01;
    }

    public String getImg_02() {
        return img_02;
    }

    public void setImg_02(String img_02) {
        this.img_02 = img_02;
    }

    public String getImg_03() {
        return img_03;
    }

    public void setImg_03(String img_03) {
        this.img_03 = img_03;
    }

    public String getTemp_01() {
        return temp_01;
    }

    public void setTemp_01(String temp_01) {
        this.temp_01 = temp_01;
    }

    public String getTemp_02() {
        return temp_02;
    }

    public void setTemp_02(String temp_02) {
        this.temp_02 = temp_02;
    }

    public String getTemp_03() {
        return temp_03;
    }

    public void setTemp_03(String temp_03) {
        this.temp_03 = temp_03;
    }

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
