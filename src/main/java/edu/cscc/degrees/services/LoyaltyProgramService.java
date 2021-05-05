package edu.cscc.degrees.services;

import edu.cscc.degrees.model.Customer;

public interface LoyaltyProgramService {

    void addLoyaltyPoints(Customer customer, long points);

}
