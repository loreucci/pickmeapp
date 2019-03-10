package com.bionicshack.volvo;

public class Car {

    private int id;
    private String model;
    private double fuel, cost, lat, lon;
    private boolean hybrid;

    public Car(int id, String model, double fuel, double cost, boolean hybrid, double lat, double lon) {
        this.id = id;
        this.model = model;
        this.fuel = fuel;
        this.cost = cost;
        this.hybrid = hybrid;
        this.lat = lat;
        this.lon = lon;
    }

    int getId() {
        return id;
    }

    String getModel() {
        return model;
    }

    double getFuel() {
        return fuel;
    }

    double getCost() {
        return cost;
    }

    boolean isHybrid() {
        return hybrid;
    }

    double getLat() {
        return lat;
    }

    double getLon() {
        return lon;
    }

}
