package com.hot.sentefinder.viewmodels;

/**
 * Created by Jamie on 4/20/2017.
 */

public class FinancialServiceProviderViewModel {
    private long id;
    private String name;
    private String operator;
    private double distance;
    private String amenity;

    public FinancialServiceProviderViewModel() {
    }

    public FinancialServiceProviderViewModel(long id, String name, String operator, double distance) {
        this.id = id;
        this.name = name;
        this.operator = operator;
        this.distance = distance;
    }

    public FinancialServiceProviderViewModel(long id, String name, String operator, double distance, String amenity) {
        this.id = id;
        this.name = name;
        this.operator = operator;
        this.distance = distance;
        this.amenity = amenity;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getAmenity() {
        return amenity;
    }

    public void setAmenity(String amenity) {
        this.amenity = amenity;
    }
}
