package nl.rowendu.rlrestmvc.controller;

import nl.rowendu.rlrestmvc.entities.Customer;
import nl.rowendu.rlrestmvc.model.CustomerDto;
import nl.rowendu.rlrestmvc.repositories.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class CustomerControllerIT {
    @Autowired
    CustomerController customerController;
    @Autowired
    CustomerRepository customerRepository;

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
        Customer customer = customerRepository.findAll().get(0);
        CustomerDto customerDto = customerController.getCustomerById(customer.getId());
        assertThat(customerDto).isNotNull();
        assertEquals(customer.getId(), customerDto.getId());
    }

    @Test
    void testGetCustomerByIdFails() {
        UUID uuid = UUID.randomUUID();
        assertThrows(NotFoundException.class, () ->
            customerController.getCustomerById(uuid)
        );
    }
}
