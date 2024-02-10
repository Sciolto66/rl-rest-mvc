package nl.rowendu.rlrestmvc.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import lombok.RequiredArgsConstructor;
import nl.rowendu.rlrestmvc.mappers.CustomerMapper;
import nl.rowendu.rlrestmvc.model.CustomerDto;
import nl.rowendu.rlrestmvc.repositories.CustomerRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
@RequiredArgsConstructor
public class CustomerServiceJPA implements CustomerService {

  private final CustomerRepository customerRepository;
  private final CustomerMapper customerMapper;

  @Override
  public List<CustomerDto> listCustomers() {
    return customerRepository.findAll().stream()
        .map(customerMapper::customerToCustomerDto)
        .toList();
  }

  @Override
  public Optional<CustomerDto> getCustomerById(UUID id) {
    return Optional.ofNullable(
        customerMapper.customerToCustomerDto(customerRepository.findById(id).orElse(null)));
  }

  @Override
  public CustomerDto saveNewCustomer(CustomerDto customerDto) {
    return customerMapper.customerToCustomerDto(
        customerRepository.save(customerMapper.customerDtoToCustomer(customerDto)));
  }

  @Override
  public Optional<CustomerDto> updateCustomerById(UUID customerId, CustomerDto customerDto) {
    AtomicReference<Optional<CustomerDto>> customerDtoOptional = new AtomicReference<>();

    customerRepository
        .findById(customerId)
        .ifPresentOrElse(
            customer -> {
              customer.setName(customerDto.getName());
              customerDtoOptional.set(
                  Optional.of(
                      customerMapper.customerToCustomerDto(customerRepository.save(customer))));
            },
            () -> customerDtoOptional.set(Optional.empty()));

    return customerDtoOptional.get();
  }

  @Override
  public boolean deleteCustomerById(UUID customerId) {
    if (customerRepository.existsById(customerId)) {
      customerRepository.deleteById(customerId);
      return true;
    } else {
      return false;
    }
  }

  @Override
  public Optional<CustomerDto> patchCustomerById(UUID customerId, CustomerDto customerDto) {
    AtomicReference<Optional<CustomerDto>> customerDtoOptional = new AtomicReference<>();

    customerRepository
        .findById(customerId)
        .ifPresentOrElse(
            customer -> {
              if (customerDto.getName() != null) {
                customer.setName(customerDto.getName());
              }
              customer.setUpdateDate(LocalDateTime.now());
              customerDtoOptional.set(
                  Optional.of(
                      customerMapper.customerToCustomerDto(customerRepository.save(customer))));
            },
            () -> customerDtoOptional.set(Optional.empty()));

    return customerDtoOptional.get();
  }
}
