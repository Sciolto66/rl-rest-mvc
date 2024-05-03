package nl.rowendu.rlrestmvc.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import nl.rowendu.rlrestmvc.config.SpringSecConfig;
import nl.rowendu.rlrestmvc.model.BeerDto;
import nl.rowendu.rlrestmvc.services.BeerService;
import nl.rowendu.rlrestmvc.services.BeerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(BeerController.class)
@Import(SpringSecConfig.class)
class BeerControllerTest {

  public static final String USERNAME = "user1";
  public static final String PASSWORD = "password";
  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper objectMapper;
  @MockBean BeerService beerService;
  BeerServiceImpl beerServiceImpl;
  @Captor ArgumentCaptor<UUID> uuidArgumentCaptor;
  @Captor ArgumentCaptor<BeerDto> beerArgumentCaptor;

  @Value("${beer.api.path}")
  private String beerPath;

  @BeforeEach
  void setUp() {
    beerServiceImpl = new BeerServiceImpl();
  }

  @Test
  void testPatchBeerById() throws Exception {
    BeerDto beerDto = beerServiceImpl.listBeers(null, null, null, 1, 25).getContent().getFirst();
    given(beerService.patchBeerById(any(UUID.class), any(BeerDto.class)))
        .willReturn(Optional.of(beerDto));
    Map<String, Object> beerPatch = Map.of("beerName", "Patched Beer Name");

    mockMvc
        .perform(
            patch(beerPath + "/{beerId}", beerDto.getId())
                .with(httpBasic(BeerControllerTest.USERNAME, BeerControllerTest.PASSWORD))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(beerPatch)))
        .andExpect(status().isNoContent());

    verify(beerService).patchBeerById(uuidArgumentCaptor.capture(), beerArgumentCaptor.capture());
    assertThat(uuidArgumentCaptor.getValue()).isEqualTo(beerDto.getId());
    assertThat(beerArgumentCaptor.getValue().getBeerName()).isEqualTo(beerPatch.get("beerName"));
  }

  @Test
  void testDeleteBeerById() throws Exception {
    BeerDto beerDto = beerServiceImpl.listBeers(null, null, null, 1, 25).getContent().getFirst();
    given(beerService.deleteBeerById(any(UUID.class))).willReturn(true);

    mockMvc
        .perform(
            delete(beerPath + "/{beerId}", beerDto.getId())
                .with(httpBasic(BeerControllerTest.USERNAME, BeerControllerTest.PASSWORD))
                .with(csrf())
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    verify(beerService).deleteBeerById(uuidArgumentCaptor.capture());
    assertThat(uuidArgumentCaptor.getValue()).isEqualTo(beerDto.getId());
  }

  @Test
  void testUpdateBeerById() throws Exception {
    BeerDto beerDto = beerServiceImpl.listBeers(null, null, null, 1, 25).getContent().getFirst();
    given(beerService.updateBeerById(any(UUID.class), any(BeerDto.class)))
        .willReturn(Optional.of(beerDto));

    mockMvc
        .perform(
            put(beerPath + "/{beerId}", beerDto.getId())
                .with(httpBasic(BeerControllerTest.USERNAME, BeerControllerTest.PASSWORD))
                .with(csrf())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(beerDto)))
        .andExpect(status().isNoContent());
    verify(beerService).updateBeerById(any(UUID.class), any(BeerDto.class));
  }

  @Test
  void testUpdateBeerByIdBlankName() throws Exception {
    BeerDto beerDto = beerServiceImpl.listBeers(null, null, null, 1, 25).getContent().getFirst();
    beerDto.setBeerName("");

    mockMvc
        .perform(
            put(beerPath + "/{beerId}", beerDto.getId())
                .with(httpBasic(BeerControllerTest.USERNAME, BeerControllerTest.PASSWORD))
                .with(csrf())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(beerDto)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.length()", is(1)));
  }

  @Test
  void testCreateNewBeer() throws Exception {
    BeerDto beerDto = beerServiceImpl.listBeers(null, null, null, 1, 25).getContent().getFirst();
    beerDto.setVersion(null);
    beerDto.setId(null);

    given(beerService.saveNewBeer(any(BeerDto.class)))
        .willReturn(beerServiceImpl.listBeers(null, null, null, 1, 25).getContent().get(1));

    mockMvc
        .perform(
            post(beerPath)
                .with(httpBasic(BeerControllerTest.USERNAME, BeerControllerTest.PASSWORD))
                .with(csrf())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(beerDto)))
        .andExpect(status().isCreated())
        .andExpect(header().exists("Location"));
  }

  @Test
  void getBeerList() throws Exception {
    given(beerService.listBeers(any(), any(), any(), any(), any()))
        .willReturn(beerServiceImpl.listBeers(null, null, null, 1, 25));

    mockMvc
        .perform(
            get(beerPath)
                .with(httpBasic(BeerControllerTest.USERNAME, BeerControllerTest.PASSWORD))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(
            jsonPath(
                "$.content.length()",
                is(beerServiceImpl.listBeers(null, null, null, 1, 25).getContent().size())));
  }

  @Test
  void testCreateBeerNullBeerName() throws Exception {
    BeerDto beerDto = BeerDto.builder().build();
    given(beerService.saveNewBeer(any(BeerDto.class)))
        .willReturn(beerServiceImpl.listBeers(null, null, null, 1, 25).getContent().get(1));
    MvcResult mvcResult =
        mockMvc
            .perform(
                post(beerPath)
                    .with(httpBasic(BeerControllerTest.USERNAME, BeerControllerTest.PASSWORD))
                    .with(csrf())
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(beerDto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.length()", is(6)))
            .andReturn();
    System.out.println(mvcResult.getResponse().getContentAsString());
  }

  @Test
  void testListBeers() throws Exception {
    given(beerService.listBeers(any(), any(), any(), any(), any()))
        .willReturn(beerServiceImpl.listBeers(null, null, null, 1, 25));

    mockMvc
        .perform(
            get(beerPath)
                .with(httpBasic(BeerControllerTest.USERNAME, BeerControllerTest.PASSWORD))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content.length()", is(3)));
  }

  @Test
  void getBeerById() throws Exception {
    BeerDto testBeerDto =
        beerServiceImpl.listBeers(null, null, null, 1, 25).getContent().getFirst();

    given(beerService.getBeerById(testBeerDto.getId())).willReturn(Optional.of(testBeerDto));

    mockMvc
        .perform(
            get(beerPath + "/{beerId}", testBeerDto.getId())
                .with(httpBasic(BeerControllerTest.USERNAME, BeerControllerTest.PASSWORD))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", is(testBeerDto.getId().toString())))
        .andExpect(jsonPath("$.beerName", is(testBeerDto.getBeerName())));
  }

  @Test
  void getBeerByIdNotFound() throws Exception {
    given(beerService.getBeerById(any(UUID.class))).willReturn(Optional.empty());

    mockMvc
        .perform(
            get(beerPath + "/{beerId}", UUID.randomUUID())
                .with(httpBasic(BeerControllerTest.USERNAME, BeerControllerTest.PASSWORD))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }
}
