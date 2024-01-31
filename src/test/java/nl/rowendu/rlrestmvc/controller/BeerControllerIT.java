package nl.rowendu.rlrestmvc.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import nl.rowendu.rlrestmvc.entities.Beer;
import nl.rowendu.rlrestmvc.model.BeerDto;
import nl.rowendu.rlrestmvc.model.BeerStyle;
import nl.rowendu.rlrestmvc.repositories.BeerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class BeerControllerIT {
  @Autowired BeerController beerController;
  @Autowired BeerRepository beerRepository;

  @Test
  void testListBeers() {
    List<BeerDto> beerDtoList = beerController.listBeers();

    assertThat(beerDtoList.size()).isEqualTo(3);
  }

  @Transactional
  @Rollback
  @Test
  void testEmptyListBeers() {
    beerRepository.deleteAll();
    List<BeerDto> beerDtoList = beerController.listBeers();

    assertThat(beerDtoList.size()).isEqualTo(0);
  }

  @Test
  void testGetBeerById() {
    Beer beer = beerRepository.findAll().get(0);
    BeerDto beerDto = beerController.getBeerById(beer.getId());

    assertThat(beerDto).isNotNull();
    assertThat(beerDto.getId()).isEqualTo(beer.getId());
  }

  @Test
  void testGetBeerByIdFails() {
    UUID uuid = UUID.randomUUID();
    assertThrows(NotFoundException.class, () -> beerController.getBeerById(uuid));
  }

  @Test
  @Transactional
  @Rollback
  void testSaveNewBeer() {
    BeerDto beerDto =
        BeerDto.builder()
            .beerName("Bavaria")
            .beerStyle(BeerStyle.PILSNER)
            .upc("45678765543")
            .price(new BigDecimal(0.78))
            .quantityOnHand(333)
            .build();

    ResponseEntity responseEntity = beerController.handlePost(beerDto);

    assertThat(responseEntity.getStatusCodeValue()).isEqualTo(201);
    assertThat(responseEntity.getHeaders().getLocation().getPath()).isNotNull();
    assertThat(beerRepository.count()).isEqualTo(4);

    String[] pathParts = responseEntity.getHeaders().getLocation().getPath().split("/");
    UUID uuid = UUID.fromString(pathParts[pathParts.length - 1]);

    Beer beer = beerRepository.findById(uuid).get();
    assertThat(beer).isNotNull();
  }
}
