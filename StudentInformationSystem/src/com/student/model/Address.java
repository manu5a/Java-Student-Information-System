package com.student.model;

/**
 * 
 * @param street     The street address
 * @param city       The city name
 * @param postalCode The postal code
 */
public record Address(String street, String city, String postalCode) {
    public Address {
        if (street == null || street.isBlank()) {
            throw new IllegalArgumentException("Street cannot be null or empty");
        }
        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException("City cannot be null or empty");
        }
        if (postalCode == null || postalCode.isBlank()) {
            throw new IllegalArgumentException("Postal code cannot be null or empty");
        }
    
        street = street.trim();
        city = city.trim();
        postalCode = postalCode.trim();
    }

    @Override
    public String toString() {
        return street + ", " + city + " - " + postalCode;
    }
}