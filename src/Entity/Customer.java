/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entity;

import java.util.Objects;

/**
 *
 * @author Admin
 */
public class Customer {
    private int id;
    private String customerName;
    private String address;
    private String phoneNumber;

    // Constructor
    public Customer(int id, String customerName, String address, String phoneNumber) {
        this.id = id;
        this.customerName = customerName;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    // Getter for ID
    public int getId() {
        return id;
    }

    // Setter for ID
    public void setId(int id) {
        this.id = id;
    }

    // Getter for CustomerName
    public String getCustomerName() {
        return customerName;
    }

    // Setter for CustomerName
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    // Getter for Address
    public String getAddress() {
        return address;
    }

    // Setter for Address
    public void setAddress(String address) {
        this.address = address;
    }

    // Getter for PhoneNumber
    public String getPhoneNumber() {
        return phoneNumber;
    }

    // Setter for PhoneNumber
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    // Override toString method
    @Override
    public String toString() {
        return customerName;
    }
    
    // Override equals method
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return id == customer.id &&
                customerName.equals(customer.customerName) &&
                address.equals(customer.address) &&
                phoneNumber.equals(customer.phoneNumber);
    }

    // Override hashCode method
    @Override
    public int hashCode() {
        return Objects.hash(id, customerName, address, phoneNumber);
    }
}
