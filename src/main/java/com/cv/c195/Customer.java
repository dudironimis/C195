package com.cv.c195;

public class Customer {
    private int id;
    private String name;
    private String address;
    private String zip;
    private String phone;
    private String state;
    private String country;
    private String addressWithState;

    /**
     * Constructor for customer class.
     *
     * @param id - This customer's ID
     * @param name - This customer's name
     * @param address - This customer's address
     * @param zip - This customer's postal code
     * @param phone - This customer's phone number
     * @param state - This customer's first-level division
     * @param country = This customer's country
     */
    public Customer(int id, String name, String address, String zip, String phone, String state, String country) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.zip = zip;
        this.phone = phone;
        this.state = state;
        this.country = country;
    }

    /**
     *
     * @return The ID of this customer
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the ID for this customer
     * @param id - The ID for this customer
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     *
     * @return The name for this customer
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this customer
     * @param name - The name of this customer
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return The address of this customer
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the address of this customer
     * @param address - The address of this customer
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     *
     * @return The postal code of this customer
     */
    public String getZip() {
        return zip;
    }

    /**
     * Sets the postal code for this customer
     * @param zip - The postal code of this customer
     */
    public void setZip(String zip) {
        this.zip = zip;
    }

    /**
     *
     * @return The phone number for this customer
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the phone number for this customer
     * @param phone - The phone number for this customer
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     *
     * @return THe state of this customer
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the first-level division of this customer
     * @param state - The first-level division of this customer
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     *
     * @return The country of this customer
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the country for this customer
     * @param country - The customer for this country
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     *
     * @return This customer's address with their state. Formatted "address, state"
     */
    public String getAddressWithState(){
        addressWithState = address + ", " + state;
        return addressWithState;
    }
}
