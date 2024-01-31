package nl.rowendu.rlrestmvc.services;

import lombok.RequiredArgsConstructor;
import nl.rowendu.rlrestmvc.mappers.CustomerMapper;
import nl.rowendu.rlrestmvc.model.CustomerDto;
import nl.rowendu.rlrestmvc.repositories.CustomerRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Primary
@RequiredArgsConstructor
public class CustomerServiceJPA implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public List<CustomerDto> listCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(customerMapper::customerToCustomerDto)
                .toList();
    }

    @Override
    public Optional<CustomerDto> getCustomerById(UUID id) {
        return Optional.ofNullable(customerMapper.customerToCustomerDto(customerRepository.findById(id)
                .orElse(null)));
    }

    @Override
    public CustomerDto saveNewCustomer(CustomerDto customerDto) {
        return null;
    }

    @Override
    public void updateCustomerById(UUID customerId, CustomerDto customerDto) {

    }

    @Override
    public void deleteCustomerById(UUID customerId) {

    }

    @Override
    public void patchCustomerById(UUID customerId, CustomerDto customerDto) {

    }
}
