package nl.rowendu.rlrestmvc.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import jakarta.validation.ConstraintViolationException;
import java.math.BigDecimal;
import java.util.List;

import nl.rowendu.rlrestmvc.bootstrap.BootstrapData;
import nl.rowendu.rlrestmvc.entities.Beer;
import nl.rowendu.rlrestmvc.model.BeerStyle;
import nl.rowendu.rlrestmvc.services.BeerCsvServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import({BootstrapData.class, BeerCsvServiceImpl.class})
class BeerRepositoryTest {

  @Autowired BeerRepository beerRepository;

  @Test
  void testGetBeerListByName() {
    List<Beer> list = beerRepository.findAllByBeerNameIsLikeIgnoreCase("%IPA%");
    assertThat(list).hasSize(336);
  }

  @Test
  void testSaveBeer() {
    Beer savedBeer =
        beerRepository.save(
            Beer.builder()
                .beerName("Test Beer")
                .upc("1234567890123")
                .beerStyle(BeerStyle.GOSE)
                .price(new BigDecimal("12.95"))
                .build());

    beerRepository.flush();

    assertThat(savedBeer).isNotNull();
    assertThat(savedBeer.getId()).isNotNull();
  }

  @Test
  void testSaveBeerNameTooLong() {
    final String longBeerName = "Test Beer 01234567890123456789012345678901234567890123456789";
    final String expectedErrorMessage = "Beer name is too long";

    Beer longBeer =
            Beer.builder()
                    .beerName(longBeerName)
                    .upc("1234567890123")
                    .beerStyle(BeerStyle.GOSE)
                    .price(new BigDecimal("12.95"))
                    .build();

      beerRepository.save(longBeer);

      assertThatThrownBy(() -> beerRepository.flush())
            .isInstanceOf(ConstraintViolationException.class)
            .hasMessageContaining(expectedErrorMessage);
  }

}
