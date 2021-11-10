package com.sothawo.mapjfxdemo;

public class Restaurant {
    private  double lieferUmkreis;
    private double lat;
    private double lng;
    private String name;

    public Restaurant(String name, double lat, double lng, double lieferUmkreis) {
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.lieferUmkreis = lieferUmkreis;
    }
}
