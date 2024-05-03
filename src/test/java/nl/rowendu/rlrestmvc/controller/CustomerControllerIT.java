package nl.rowendu.rlrestmvc.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import nl.rowendu.rlrestmvc.entities.Customer;
import nl.rowendu.rlrestmvc.mappers.CustomerMapper;
import nl.rowendu.rlrestmvc.model.CustomerDto;
import nl.rowendu.rlrestmvc.repositories.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class CustomerControllerIT {
  @Autowired CustomerController customerController;
  @Autowired CustomerRepository customerRepository;
  @Autowired CustomerMapper customerMapper;

  @Test
  void testListCustomers() {
    assertEquals(3, customerController.listCustomers().size());
  }

  @Test
  @Transactional
  @Rollback
  void testEmptyListCustomers() {
    customerRepository.deleteAll();
    assertEquals(0, customerController.listCustomers().size());
  }

  @Test
  void testGetCustomerById() {
    Customer customer = customerRepository.findAll().getFirst();
    CustomerDto customerDto = customerController.getCustomerById(customer.getId());
    assertThat(customerDto).isNotNull();
    assertEquals(customer.getId(), customerDto.getId());
  }

  @Test
  void testGetCustomerByIdFails() {
    UUID uuid = UUID.randomUUID();
    assertThrows(NotFoundException.class, () -> customerController.getCustomerById(uuid));
  }

  @Test
  @Transactional
  @Rollback
  void testSaveNewCustomer() {
    CustomerDto customerDto = CustomerDto.builder().name("New Customer").build();

    ResponseEntity<CustomerDto> responseEntity = customerController.handlePost(customerDto);

    assertThat(responseEntity.getStatusCode().value()).isEqualTo(201);
    assertThat(Objects.requireNonNull(responseEntity.getHeaders().getLocation()).getPath())
        .isNotNull();
    assertThat(customerRepository.findAll()).hasSize(4);

    String[] pathParts = responseEntity.getHeaders().getLocation().getPath().split("/");
    UUID uuid = UUID.fromString(pathParts[pathParts.length - 1]);

    Customer customer =
        customerRepository
            .findById(uuid)
            .orElseThrow(() -> new NotFoundException("No customer found with id " + uuid));
    assertThat(customer).isNotNull();
  }

  @Test
  void testUpdateNotFound() {
    UUID uuid = UUID.randomUUID();
    CustomerDto customerDto = CustomerDto.builder().build();
    assertThrows(
        NotFoundException.class, () -> customerController.updateCustomerById(uuid, customerDto));
  }

  @Test
  @Transactional
  @Rollback
  void testUpdateCustomerById() {
    Customer customer = customerRepository.findAll().getFirst();
    CustomerDto customerDto = customerMapper.customerToCustomerDto(customer);
    customerDto.setVersion(null);
    customerDto.setId(null);
    final String newCustomerName = "Better Customer Name";
    customerDto.setName(newCustomerName);

    ResponseEntity<CustomerDto> responseEntity =
        customerController.updateCustomerById(customer.getId(), customerDto);

    assertThat(responseEntity.getStatusCode().value()).isEqualTo(204);
    assertThat(customerRepository.count()).isEqualTo(3);

    Customer updatedCustomer =
        customerRepository
            .findById(customer.getId())
            .orElseThrow(
                () -> new NoSuchElementException("No customer found with id " + customer.getId()));

    assertThat(updatedCustomer.getName()).isEqualTo("Better Customer Name");
  }

  @Test
  void testPatchNotFound() {
    UUID uuid = UUID.randomUUID();
    CustomerDto customerDto = CustomerDto.builder().build();
    assertThrows(
        NotFoundException.class, () -> customerController.patchCustomerById(uuid, customerDto));
  }

  @Test
  @Transactional
  @Rollback
  void testPatchCustomerById() {
    Customer customer = customerRepository.findAll().getFirst();
    CustomerDto customerDto = customerMapper.customerToCustomerDto(customer);
    final String newCustomerName = "Patched Customer Name";
    customerDto.setName(newCustomerName);

    ResponseEntity<CustomerDto> responseEntity =
        customerController.patchCustomerById(customer.getId(), customerDto);

    assertThat(responseEntity.getStatusCode().value()).isEqualTo(204);
    assertThat(customerRepository.count()).isEqualTo(3);

    Customer updatedCustomer =
        customerRepository
            .findById(customer.getId())
            .orElseThrow(
                () -> new NoSuchElementException("No customer found with id " + customer.getId()));
    assertThat(updatedCustomer.getName()).isEqualTo("Patched Customer Name");
  }

  @Test
  @Transactional
  @Rollback
  void testDeleteCustomerById() {
    assertThat(customerRepository.count()).isEqualTo(3);
    Customer customer = customerRepository.findAll().getFirst();

    ResponseEntity<?> responseEntity = customerController.deleteCustomerById(customer.getId());

    assertThat(responseEntity.getStatusCode().value()).isEqualTo(204);
    assertThat(customerRepository.count()).isEqualTo(2);
  }

  @Test
  void testDeleteNotFound() {
    UUID uuid = UUID.randomUUID();
    assertThrows(NotFoundException.class, () -> customerController.deleteCustomerById(uuid));
  }
}
