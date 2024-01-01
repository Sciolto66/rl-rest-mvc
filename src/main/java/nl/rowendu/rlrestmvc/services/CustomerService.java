package nl.rowendu.rlrestmvc.services;

import nl.rowendu.rlrestmvc.model.Customer;

import java.util.List;
import java.util.UUID;

public interface CustomerService {

    List<Customer> listCustomers();

    Customer getCustomerById(UUID id);
}
