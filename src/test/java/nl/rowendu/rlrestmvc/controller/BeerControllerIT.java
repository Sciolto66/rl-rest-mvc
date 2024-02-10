package nl.rowendu.rlrestmvc.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import nl.rowendu.rlrestmvc.entities.Beer;
import nl.rowendu.rlrestmvc.mappers.BeerMapper;
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
  @Autowired BeerMapper beerMapper;

  @Test
  void testListBeers() {
    List<BeerDto> beerDtoList = beerController.listBeers();

    assertThat(beerDtoList).hasSize(3);
  }

  @Transactional
  @Rollback
  @Test
  void testEmptyListBeers() {
    beerRepository.deleteAll();
    List<BeerDto> beerDtoList = beerController.listBeers();
    assertThat(beerDtoList).isEmpty();
  }

  @Test
  void testGetBeerById() {
    Beer beer = beerRepository.findAll().getFirst();
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
            .price(new BigDecimal("0.78"))
            .quantityOnHand(333)
            .build();

    ResponseEntity<BeerDto> responseEntity = beerController.handlePost(beerDto);

    assertThat(responseEntity.getStatusCode().value()).isEqualTo(201);
    assertThat(Objects.requireNonNull(responseEntity.getHeaders().getLocation()).getPath())
        .isNotNull();
    assertThat(beerRepository.count()).isEqualTo(4);

    String[] pathParts = responseEntity.getHeaders().getLocation().getPath().split("/");
    UUID uuid = UUID.fromString(pathParts[pathParts.length - 1]);

    Beer beer =
        beerRepository
            .findById(uuid)
            .orElseThrow(() -> new NoSuchElementException("No beer found with id " + uuid));
    assertThat(beer).isNotNull();
  }

  @Test
  void testUpdateNotFound() {
    UUID uuid = UUID.randomUUID();
    BeerDto beerDto = BeerDto.builder().build();
    assertThrows(NotFoundException.class, () -> beerController.updateBeerById(uuid, beerDto));
  }

  @Test
  @Transactional
  @Rollback
  void testUpdateBeerById() {
    Beer beer = beerRepository.findAll().getFirst();
    BeerDto beerDto = beerMapper.beerToBeerDto(beer);
    beerDto.setId(null);
    beerDto.setVersion(null);
    final String newBeerName = "Better Beer Name";
    beerDto.setBeerName(newBeerName);

    ResponseEntity<BeerDto> responseEntity = beerController.updateBeerById(beer.getId(), beerDto);

    assertThat(responseEntity.getStatusCode().value()).isEqualTo(204);
    assertThat(beerRepository.count()).isEqualTo(3);

    Beer updatedBeer =
        beerRepository
            .findById(beer.getId())
            .orElseThrow(() -> new NoSuchElementException("No beer found with id " + beer.getId()));

    assertThat(updatedBeer.getBeerName()).isEqualTo("Better Beer Name");
  }

  @Test
  void testPatchNotFound() {
    UUID uuid = UUID.randomUUID();
    BeerDto beerDto = BeerDto.builder().build();
    assertThrows(NotFoundException.class, () -> beerController.patchBeerById(uuid, beerDto));
  }

  @Test
  @Transactional
  @Rollback
  void testPatchBeerById() {
    Beer beer = beerRepository.findAll().getFirst();
    BeerDto beerDto = beerMapper.beerToBeerDto(beer);
    final String newBeerName = "Patched Beer Name";
    beerDto.setBeerName(newBeerName);

    ResponseEntity<BeerDto> responseEntity = beerController.patchBeerById(beer.getId(), beerDto);

    assertThat(responseEntity.getStatusCode().value()).isEqualTo(204);
    assertThat(beerRepository.count()).isEqualTo(3);

    Beer patchedBeer =
        beerRepository
            .findById(beer.getId())
            .orElseThrow(() -> new NoSuchElementException("No beer found with id " + beer.getId()));

    assertThat(patchedBeer.getBeerName()).isEqualTo("Patched Beer Name");
  }

  @Test
  @Transactional
  @Rollback
  void testDeleteBeerById() {
    assertThat(beerRepository.count()).isEqualTo(3);
    Beer beer = beerRepository.findAll().getFirst();

    ResponseEntity<?> responseEntity = beerController.deleteBeerById(beer.getId());

    assertThat(responseEntity.getStatusCode().value()).isEqualTo(204);
    assertThat(beerRepository.count()).isEqualTo(2);
  }

  @Test
  void testDeleteNotFound() {
    UUID uuid = UUID.randomUUID();
    assertThrows(NotFoundException.class, () -> beerController.deleteBeerById(uuid));
  }
}
