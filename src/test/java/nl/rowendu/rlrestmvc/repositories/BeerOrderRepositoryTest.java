package nl.rowendu.rlrestmvc.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import nl.rowendu.rlrestmvc.entities.Beer;
import nl.rowendu.rlrestmvc.entities.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BeerOrderRepositoryTest {
    @Autowired
    BeerOrderRepository beerOrderRepository;
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    BeerRepository beerRepository;
    
    Customer testCustomer;
    Beer testBeer;
    
    @BeforeEach
    void setUp() {
        testCustomer = customerRepository.findAll().getFirst();
        testBeer = beerRepository.findAll().getFirst();
    }

    @Test
    void testBeerOrders() {
    System.out.println(beerOrderRepository.count());
    System.out.println(customerRepository.count());
    System.out.println(beerRepository.count());
    System.out.println(testCustomer.getName());
    System.out.println(testBeer.getBeerName());

    assertThat(beerOrderRepository.count()).isZero();
  }
}
