package nl.rowendu.rlrestmvc.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.*;
import nl.rowendu.rlrestmvc.entities.Beer;
import nl.rowendu.rlrestmvc.mappers.BeerMapper;
import nl.rowendu.rlrestmvc.model.BeerDto;
import nl.rowendu.rlrestmvc.model.BeerStyle;
import nl.rowendu.rlrestmvc.repositories.BeerRepository;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
class BeerControllerIT {
  public static final String LONG_BEER_NAME =
      "Test Beer 01234567890123456789012345678901234567890123456789";
  @Autowired BeerController beerController;
  @Autowired BeerRepository beerRepository;
  @Autowired BeerMapper beerMapper;
  @Autowired WebApplicationContext wac;
  @Autowired ObjectMapper objectMapper;
  MockMvc mockMvc;

  @Value("${beer.api.path}")
  private String beerPath;

  @BeforeEach
  void setUp() {
    mockMvc = webAppContextSetup(wac).build();
  }

  @Test
  void testListBeersByStyleAndNameShowInventoryTrue() throws Exception {
    mockMvc
        .perform(
            get(beerPath)
                .queryParam("beerName", "IPA")
                .queryParam("beerStyle", BeerStyle.IPA.name())
                .queryParam("showInventory", "true"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()", is(310)))
        .andExpect(jsonPath("$.[0].quantityOnHand").value(IsNull.notNullValue()));
  }

  @Test
  void testListBeersByStyleAndNameShowInventoryFalse() throws Exception {
    mockMvc
        .perform(
            get(beerPath)
                .queryParam("beerName", "IPA")
                .queryParam("beerStyle", BeerStyle.IPA.name())
                .queryParam("showInventory", "false"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()", is(310)))
        .andExpect(jsonPath("$.[0].quantityOnHand").value(IsNull.nullValue()));
  }

  @Test
  void testListBeersByStyleAndName() throws Exception {
    mockMvc
        .perform(
            get(beerPath)
                .queryParam("beerName", "IPA")
                .queryParam("beerStyle", BeerStyle.IPA.name()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()", is(310)));
  }

  @Test
  void testListBeersByName() throws Exception {
    mockMvc
        .perform(get(beerPath).queryParam("beerName", "IPA"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()", is(336)));
  }

  @Test
  void testListBeersByStyle() throws Exception {
    mockMvc
        .perform(get(beerPath).queryParam("beerStyle", BeerStyle.IPA.name()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.size()", is(548)));
  }

  @Test
  void testPatchBeerByIdBadName() throws Exception {
    Beer beer = beerRepository.findAll().getFirst();
    Map<String, Object> beerPatch = Map.of("beerName", LONG_BEER_NAME);
    MvcResult result =
        mockMvc
            .perform(
                patch(beerPath + "/{beerId}", beer.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(beerPatch)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.length()", is(1)))
            .andReturn();

    System.out.println(result.getResponse().getContentAsString());
  }

  @Test
  void testListBeers() {
    List<BeerDto> beerDtoList = beerController.listBeers(null, null, null);

    assertThat(beerDtoList).hasSize(2413);
  }

  @Transactional
  @Rollback
  @Test
  void testEmptyListBeers() {
    beerRepository.deleteAll();
    List<BeerDto> beerDtoList = beerController.listBeers(null, null, null);
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
    assertThat(beerRepository.count()).isEqualTo(2414);

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
    assertThat(beerRepository.count()).isEqualTo(2413);

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
    assertThat(beerRepository.count()).isEqualTo(2413);

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
    assertThat(beerRepository.count()).isEqualTo(2413);
    Beer beer = beerRepository.findAll().getFirst();

    ResponseEntity<?> responseEntity = beerController.deleteBeerById(beer.getId());

    assertThat(responseEntity.getStatusCode().value()).isEqualTo(204);
    assertThat(beerRepository.count()).isEqualTo(2412);
  }

  @Test
  void testDeleteNotFound() {
    UUID uuid = UUID.randomUUID();
    assertThrows(NotFoundException.class, () -> beerController.deleteBeerById(uuid));
  }
}
