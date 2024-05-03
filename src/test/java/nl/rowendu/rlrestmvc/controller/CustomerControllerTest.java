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
import nl.rowendu.rlrestmvc.model.CustomerDto;
import nl.rowendu.rlrestmvc.services.CustomerService;
import nl.rowendu.rlrestmvc.services.CustomerServiceImpl;
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

@WebMvcTest(CustomerController.class)
@Import(SpringSecConfig.class)
class CustomerControllerTest {

  @Autowired MockMvc mockMvc;
  @Autowired ObjectMapper objectMapper;
  @MockBean CustomerService customerService;
  CustomerServiceImpl customerServiceImpl;
  @Captor ArgumentCaptor<UUID> uuidArgumentCaptor;
  @Captor ArgumentCaptor<CustomerDto> customerArgumentCaptor;

  @Value("${customer.api.path}")
  private String customerPath;

  @BeforeEach
  void setUp() {
    customerServiceImpl = new CustomerServiceImpl();
  }

  @Test
  void testPatchCustomerById() throws Exception {
    CustomerDto customerDto = customerServiceImpl.listCustomers().getFirst();
    given(customerService.patchCustomerById(any(UUID.class), any(CustomerDto.class)))
        .willReturn(Optional.of(customerDto));
    Map<String, Object> customerPatch = Map.of("name", "New Customer Name");

    mockMvc
        .perform(
            patch(customerPath + "/{customerId}", customerDto.getId())
                .with(httpBasic(BeerControllerTest.USERNAME, BeerControllerTest.PASSWORD))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerPatch)))
        .andExpect(status().isNoContent());

    verify(customerService)
        .patchCustomerById(uuidArgumentCaptor.capture(), customerArgumentCaptor.capture());
    assertThat(uuidArgumentCaptor.getValue()).isEqualTo(customerDto.getId());
    assertThat(customerArgumentCaptor.getValue().getName()).isEqualTo(customerPatch.get("name"));
  }

  @Test
  void testDeleteCustomerById() throws Exception {
    CustomerDto customerDto = customerServiceImpl.listCustomers().getFirst();
    given(customerService.deleteCustomerById(any(UUID.class))).willReturn(true);

    mockMvc
        .perform(
            delete(customerPath + "/{customerId}", customerDto.getId())
                .with(httpBasic(BeerControllerTest.USERNAME, BeerControllerTest.PASSWORD))
                .with(csrf())
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    verify(customerService).deleteCustomerById(uuidArgumentCaptor.capture());
    assertThat(uuidArgumentCaptor.getValue()).isEqualTo(customerDto.getId());
  }

  @Test
  void testUpdateCustomerById() throws Exception {
    CustomerDto customerDto = customerServiceImpl.listCustomers().getFirst();
    given(customerService.updateCustomerById(any(UUID.class), any(CustomerDto.class)))
        .willReturn(Optional.of(customerDto));

    mockMvc
        .perform(
            put(customerPath + "/{customerId}", customerDto.getId())
                .with(httpBasic(BeerControllerTest.USERNAME, BeerControllerTest.PASSWORD))
                .with(csrf())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerDto)))
        .andExpect(status().isNoContent());
    verify(customerService)
        .updateCustomerById(uuidArgumentCaptor.capture(), any(CustomerDto.class));

    assertThat(uuidArgumentCaptor.getValue()).isEqualTo(customerDto.getId());
  }

  @Test
  void testCreateNewCustomer() throws Exception {
    CustomerDto customerDto = customerServiceImpl.listCustomers().getFirst();
    customerDto.setId(null);
    customerDto.setVersion(null);

    given(customerService.saveNewCustomer(any(CustomerDto.class)))
        .willReturn(customerServiceImpl.listCustomers().get(1));

    mockMvc
        .perform(
            post(customerPath)
                .with(httpBasic(BeerControllerTest.USERNAME, BeerControllerTest.PASSWORD))
                .with(csrf())
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(customerDto)))
        .andExpect(status().isCreated())
        .andExpect(header().exists("Location"));
  }

  @Test
  void getCustomerById() throws Exception {
    CustomerDto testCustomerDto = customerServiceImpl.listCustomers().getFirst();

    given(customerService.getCustomerById(testCustomerDto.getId()))
        .willReturn(Optional.of(testCustomerDto));

    mockMvc
        .perform(
            get(customerPath + "/{customerId}", testCustomerDto.getId())
                .with(httpBasic(BeerControllerTest.USERNAME, BeerControllerTest.PASSWORD))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id", is(testCustomerDto.getId().toString())))
        .andExpect(jsonPath("$.name", is(testCustomerDto.getName())));
  }

  @Test
  void getCustomerByIdNotFound() throws Exception {
    given(customerService.getCustomerById(any(UUID.class))).willReturn(Optional.empty());

    mockMvc
        .perform(
            get(customerPath + "/{customerId}", UUID.randomUUID())
                .with(httpBasic(BeerControllerTest.USERNAME, BeerControllerTest.PASSWORD))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  @Test
  void getCustomerList() throws Exception {
    given(customerService.listCustomers()).willReturn(customerServiceImpl.listCustomers());

    mockMvc
        .perform(
            get(customerPath)
                .with(httpBasic(BeerControllerTest.USERNAME, BeerControllerTest.PASSWORD))
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.length()", is(customerServiceImpl.listCustomers().size())));
  }
}
