package com.example.ckrao.myapplication;

import java.net.URL;

/**
 * Created by CKRAO on 2016/9/25.
 */

public class DataBean {
    private String city;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    private String wendu;
    private String tianqi;
    private URL imageUrl;

    public String getWendu() {
        return wendu;
    }

    public void setWendu(String wendu) {
        this.wendu = wendu;
    }

    public String getTianqi() {
        return tianqi;
    }

    public void setTianqi(String tianqi) {
        this.tianqi = tianqi;
    }

    public URL getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(URL imageUrl) {
        this.imageUrl = imageUrl;
    }
}
