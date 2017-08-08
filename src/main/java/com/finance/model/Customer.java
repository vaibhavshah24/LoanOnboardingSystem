package com.finance.model;

public class Customer {

    private String name;
    private String idType;
    private String idNumber;

    public Customer(String name, String idType, String idNumber) {
        this.name = name;
        this.idType = idType;
        this.idNumber = idNumber;
    }

    public String getName() {
        return name;
    }

    public String getIdType() {
        return idType;
    }

    public String getIdNumber() {
        return idNumber;
    }

}
