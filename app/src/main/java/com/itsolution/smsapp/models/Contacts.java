package com.itsolution.smsapp.models;

public class Contacts {

    int id;
    String name;
    String mobileNumber;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Contacts{" +
                "id=" + id +
                ", mobileNumber=" + mobileNumber +
                ", name='" + name + '\'' +
                '}';
    }
}
