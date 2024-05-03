package nl.rowendu.rlrestmvc.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import nl.rowendu.rlrestmvc.entities.Beer;
import nl.rowendu.rlrestmvc.entities.BeerOrder;
import nl.rowendu.rlrestmvc.entities.BeerOrderShipment;
import nl.rowendu.rlrestmvc.entities.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class BeerOrderRepositoryTest {
  @Autowired BeerOrderRepository beerOrderRepository;
  @Autowired CustomerRepository customerRepository;
  @Autowired BeerRepository beerRepository;

  Customer testCustomer;
  Beer testBeer;

  @BeforeEach
  void setUp() {
    testCustomer = customerRepository.findAll().getFirst();
    testBeer = beerRepository.findAll().getFirst();
  }

  @Transactional
  @Test
  void testBeerOrders() {
    BeerOrder beerOrder =
        BeerOrder.builder()
            .customerRef("Test Order")
            .customer(testCustomer)
            .beerOrderShipment(BeerOrderShipment.builder().trackingNumber("123456r").build())
            .build();

    BeerOrder savedBeerOrder = beerOrderRepository.save(beerOrder);
    System.out.println(savedBeerOrder.getCustomerRef());

    assertThat(beerOrderRepository.count()).isEqualTo(1L);
  }
}
