package nl.rowendu.rlrestmvc.services;

import nl.rowendu.rlrestmvc.model.CustomerDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerService {

    List<CustomerDto> listCustomers();

    Optional<CustomerDto> getCustomerById(UUID id);

    CustomerDto saveNewCustomer(CustomerDto customerDto);

    Optional<CustomerDto> updateCustomerById(UUID customerId, CustomerDto customerDto);

    boolean deleteCustomerById(UUID customerId);

    Optional<CustomerDto> patchCustomerById(UUID customerId, CustomerDto customerDto);
}
