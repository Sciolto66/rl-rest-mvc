package nl.rowendu.rlrestmvc.bootstrap;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import nl.rowendu.rlrestmvc.entities.Beer;
import nl.rowendu.rlrestmvc.entities.Customer;
import nl.rowendu.rlrestmvc.model.BeerStyle;
import nl.rowendu.rlrestmvc.repositories.BeerRepository;
import nl.rowendu.rlrestmvc.repositories.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BootstrapData implements CommandLineRunner {

  private final BeerRepository beerRepository;
  private final CustomerRepository customerRepository;

  @Override
  public void run(String... args) throws Exception {
    loadBeerData();
    loadCustomerData();
  }

  private void loadBeerData() {
    if (beerRepository.count() == 0) {
      Beer beer1 =
          Beer.builder()
              .beerName("Galaxy Cat")
              .beerStyle(BeerStyle.PALE_ALE)
              .upc("12356")
              .price(new BigDecimal("12.99"))
              .quantityOnHand(122)
              .createdDate(LocalDateTime.now())
              .updateDate(LocalDateTime.now())
              .version(1)
              .build();

      Beer beer2 =
          Beer.builder()
              .beerName("Crank")
              .beerStyle(BeerStyle.PALE_ALE)
              .upc("12356222")
              .price(new BigDecimal("11.99"))
              .quantityOnHand(392)
              .createdDate(LocalDateTime.now())
              .updateDate(LocalDateTime.now())
              .version(1)
              .build();

      Beer beer3 =
          Beer.builder()
              .beerName("Sunshine City")
              .beerStyle(BeerStyle.IPA)
              .upc("12356")
              .price(new BigDecimal("13.99"))
              .quantityOnHand(144)
              .createdDate(LocalDateTime.now())
              .updateDate(LocalDateTime.now())
              .version(1)
              .build();

      beerRepository.save(beer1);
      beerRepository.save(beer2);
      beerRepository.save(beer3);
    }
  }

  private void loadCustomerData() {

    if (customerRepository.count() == 0) {
      Customer customer1 =
          Customer.builder()
              .name("Customer 1")
              .createdDate(LocalDateTime.now())
              .updateDate(LocalDateTime.now())
              .version(1)
              .build();

      Customer customer2 =
          Customer.builder()
              .name("Customer 2")
              .createdDate(LocalDateTime.now())
              .updateDate(LocalDateTime.now())
              .version(1)
              .build();

      Customer customer3 =
          Customer.builder()
              .name("Customer 3")
              .createdDate(LocalDateTime.now())
              .updateDate(LocalDateTime.now())
              .version(1)
              .build();

      customerRepository.saveAll(Arrays.asList(customer1, customer2, customer3));
    }
  }
}
