package com.example.ckrao.myapplication.Model;

public class CitySortModel {

    private String name;//显示的数据
    private String sortLetters;//显示数据拼音的首字母
    private String fullSort;//拼音

    public String getFullSort() {
        return fullSort;
    }

    public void setFullSort(String fullSort) {
        this.fullSort = fullSort;
    }
    //    private String cityId;

//    public String getCityId() {
//        return cityId;
//    }
//
//    public void setCityId(String cityId) {
//        this.cityId = cityId;
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSortLetters() {
        return sortLetters;
    }

    public void setSortLetters(String sortLetters) {
        this.sortLetters = sortLetters;
    }
}
