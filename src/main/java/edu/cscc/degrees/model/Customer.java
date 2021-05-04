package edu.cscc.degrees.model;

import java.time.LocalDate;

public class Customer {
    private String taxID;
    private String firstName;
    private String lastName;

    public Customer() {
    }

    public Customer(String taxID, String firstName, String lastName) {
        this.taxID = taxID;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getTaxID() {
        return taxID;
    }

    public void setTaxID(String taxID) {
        this.taxID = taxID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
