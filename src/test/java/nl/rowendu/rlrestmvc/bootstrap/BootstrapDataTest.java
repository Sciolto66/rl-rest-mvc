package nl.rowendu.rlrestmvc.bootstrap;

import static org.assertj.core.api.Assertions.assertThat;

import nl.rowendu.rlrestmvc.repositories.BeerRepository;
import nl.rowendu.rlrestmvc.repositories.CustomerRepository;
import nl.rowendu.rlrestmvc.services.BeerCsvService;
import nl.rowendu.rlrestmvc.services.BeerCsvServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(BeerCsvServiceImpl.class)
class BootstrapDataTest {

  @Autowired BeerRepository beerRepository;
  @Autowired CustomerRepository customerRepository;
  @Autowired BeerCsvService csvService;
  BootstrapData bootstrapData;

  @BeforeEach
  void setUp() {
    bootstrapData = new BootstrapData(beerRepository, customerRepository, csvService);
  }

  @Test
  void Testrun() throws Exception {
    bootstrapData.run(null);

    assertThat(beerRepository.count()).isEqualTo(2413);
    assertThat(customerRepository.count()).isEqualTo(3);
  }
}
