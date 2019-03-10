package com.bionicshack.volvo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CarsDatabase {

    private ArrayList<Car> cars;

    public CarsDatabase() {

        cars = new ArrayList<Car>();

    }

    public void setCars(JSONArray jsonarr) {

        cars.clear();

        try {
            JSONObject obj;
            for (int i = 0; i < jsonarr.length(); i++) {
                obj = jsonarr.getJSONObject(i);
                cars.add(new Car(obj.getInt("id"),
                        obj.getString("model"),
                        obj.getDouble("fuel"),
                        obj.getDouble("cost"),
                        obj.getBoolean("hybrid"),
                        obj.getDouble("lat"),
                        obj.getDouble("lon")));
            }
        } catch (JSONException e) {
        }

    }

    public int getNumCars() {
        return cars.size();
    }

    public Car getCar(int id) {
        return cars.get(id);
    }

}
