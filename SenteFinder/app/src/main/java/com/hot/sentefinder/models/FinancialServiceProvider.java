package com.hot.sentefinder.models;

import android.os.Parcel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Jamie on 4/5/2017.
 */

public class FinancialServiceProvider implements Serializable {
    @SerializedName("id")
    @Expose
    private long id;
    @SerializedName("amenity")
    @Expose
    private String amenity;
    @SerializedName("contact:phone")
    @Expose
    private String contactPhone;
    @SerializedName("contact:website")
    @Expose
    private String contactWebsite;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("opening_hours")
    @Expose
    private String openingHours;
    @SerializedName("coordinates")
    @Expose
    private List<Double> coordinates = null;
    @SerializedName("atm")
    @Expose
    private String atm;
    @SerializedName("addr:city")
    @Expose
    private String addrCity;
    @SerializedName("female")
    @Expose
    private String female;
    @SerializedName("level")
    @Expose
    private String level;
    @SerializedName("male")
    @Expose
    private String male;
    @SerializedName("network")
    @Expose
    private String network;
    @SerializedName("addr:street")
    @Expose
    private String addrStreet;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("shop")
    @Expose
    private String shop;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("operator")
    @Expose
    private String operator;
    @SerializedName("addr:housenumber")
    @Expose
    private String addrHousenumber;
    @SerializedName("addr:housename")
    @Expose
    private String addrHousename;

    public FinancialServiceProvider(){}

    public FinancialServiceProvider(long id, String amenity, String contactPhone, String contactWebsite, String name, String openingHours, List<Double> coordinates, String atm, String addrCity, String female, String level, String male, String network, String addrStreet, String phone, String shop, String description, String operator, String addrHousenumber, String addrHousename) {
        this.id = id;
        this.amenity = amenity;
        this.contactPhone = contactPhone;
        this.contactWebsite = contactWebsite;
        this.name = name;
        this.openingHours = openingHours;
        this.coordinates = coordinates;
        this.atm = atm;
        this.addrCity = addrCity;
        this.female = female;
        this.level = level;
        this.male = male;
        this.network = network;
        this.addrStreet = addrStreet;
        this.phone = phone;
        this.shop = shop;
        this.description = description;
        this.operator = operator;
        this.addrHousenumber = addrHousenumber;
        this.addrHousename = addrHousename;
    }

    public FinancialServiceProvider(long id) {

    }

    protected FinancialServiceProvider(Parcel in) {
        id = in.readLong();
        amenity = in.readString();
        contactPhone = in.readString();
        contactWebsite = in.readString();
        name = in.readString();
        openingHours = in.readString();
        atm = in.readString();
        addrCity = in.readString();
        female = in.readString();
        level = in.readString();
        male = in.readString();
        network = in.readString();
        addrStreet = in.readString();
        phone = in.readString();
        shop = in.readString();
        description = in.readString();
        operator = in.readString();
        addrHousenumber = in.readString();
        addrHousename = in.readString();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAmenity() {
        return amenity;
    }

    public void setAmenity(String amenity) {
        this.amenity = amenity;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getContactWebsite() {
        return contactWebsite;
    }

    public void setContactWebsite(String contactWebsite) {
        this.contactWebsite = contactWebsite;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public List<Double> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Double> coordinates) {
        this.coordinates = coordinates;
    }

    public String getAtm() {
        return atm;
    }

    public void setAtm(String atm) {
        this.atm = atm;
    }

    public String getAddrCity() {
        return addrCity;
    }

    public void setAddrCity(String addrCity) {
        this.addrCity = addrCity;
    }

    public String getFemale() {
        return female;
    }

    public void setFemale(String female) {
        this.female = female;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getMale() {
        return male;
    }

    public void setMale(String male) {
        this.male = male;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getAddrStreet() {
        return addrStreet;
    }

    public void setAddrStreet(String addrStreet) {
        this.addrStreet = addrStreet;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getAddrHousenumber() {
        return addrHousenumber;
    }

    public void setAddrHousenumber(String addrHousenumber) {
        this.addrHousenumber = addrHousenumber;
    }

    public String getAddrHousename() {
        return addrHousename;
    }

    public void setAddrHousename(String addrHousename) {
        this.addrHousename = addrHousename;
    }

//    @Override
//    public String toString() {
//        StringBuilder sb = new StringBuilder();
//        sb. append("id: ").append(id);
//        sb. append(", lat: ").append(lat);
//        sb. append(", lon: ").append(lon);
//        sb. append(", amenity: ").append(amenity);
//        sb. append(", city: ").append(city);
//        sb. append(", street: ").append(street);
//        sb. append(", opening_hours: ").append(opening_hours);
//        sb. append(", operator: ").append(operator);
//        sb. append(", network: ").append(network);
//        sb. append(", atm: ").append(atm);
//        sb. append(", phone: ").append(phone);
//        sb. append(", website: ").append(website);
//
//        return super.toString();
//    }
}
