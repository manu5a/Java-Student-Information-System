package com.student.model;

import java.time.LocalDate;

public sealed class Person permits Student {
    private String name;
    private String email;
    private Address address;
    private final LocalDate enrollmentDate; // Added for Date/Time API

    public Person(String name, String email, Address address) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.enrollmentDate = LocalDate.now(); // Default to today
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    @Override
    public String toString() {
        return "Person{name='" + name + "', email='" + email + "', address=" + address + 
               ", enrollmentDate=" + enrollmentDate + "}";
    }
}