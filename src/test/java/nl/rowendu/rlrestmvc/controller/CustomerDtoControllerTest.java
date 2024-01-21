package nl.rowendu.rlrestmvc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.rowendu.rlrestmvc.model.CustomerDto;
import nl.rowendu.rlrestmvc.services.CustomerService;
import nl.rowendu.rlrestmvc.services.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerDtoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CustomerService customerService;
    CustomerServiceImpl customerServiceImpl;

    @BeforeEach
    void setUp() {
        customerServiceImpl = new CustomerServiceImpl();
    }

    @Captor
    ArgumentCaptor<UUID> uuidArgumentCaptor;
    @Captor
    ArgumentCaptor<CustomerDto> customerArgumentCaptor;

    @Test
    void testPatchCustomerById() throws Exception {
        CustomerDto customerDto = customerServiceImpl.listCustomers().get(0);

        Map<String, Object> customerPatch = Map.of("name", "New Customer Name");

        mockMvc.perform(patch(CustomerController.CUSTOMER_PATH_ID, customerDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerPatch)))
                .andExpect(status().isNoContent());

        verify(customerService).patchCustomerById(uuidArgumentCaptor.capture(), customerArgumentCaptor.capture());

        assertThat(uuidArgumentCaptor.getValue()).isEqualTo(customerDto.getId());
        assertThat(customerArgumentCaptor.getValue().getName()).isEqualTo(customerPatch.get("name"));
    }

    @Test
    void testDeleteCustomerById() throws Exception {
        CustomerDto customerDto = customerServiceImpl.listCustomers().get(0);
        mockMvc.perform(delete(CustomerController.CUSTOMER_PATH_ID, customerDto.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(customerService).deleteCustomerById(uuidArgumentCaptor.capture());

        assertThat(uuidArgumentCaptor.getValue()).isEqualTo(customerDto.getId());
    }

    @Test
    void testUpdateCustomerById() throws Exception {
        CustomerDto customerDto = customerServiceImpl.listCustomers().get(0);
        mockMvc.perform(put(CustomerController.CUSTOMER_PATH_ID, customerDto.getId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDto)))
                .andExpect(status().isNoContent());
        verify(customerService).updateCustomerById(uuidArgumentCaptor.capture(), any(CustomerDto.class));

        assertThat(uuidArgumentCaptor.getValue()).isEqualTo(customerDto.getId());
    }

    @Test
    void testCreateNewCustomer() throws Exception {
        CustomerDto customerDto = customerServiceImpl.listCustomers().get(0);
        customerDto.setId(null);
        customerDto.setVersion(null);

        given(customerService.saveNewCustomer(any(CustomerDto.class)))
                .willReturn(customerServiceImpl.listCustomers().get(1));

        mockMvc.perform(post(CustomerController.CUSTOMER_PATH)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDto)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    void getCustomerById() throws Exception {
        CustomerDto testCustomerDto = customerServiceImpl.listCustomers().get(0);

        given(customerService.getCustomerById(testCustomerDto.getId()))
                .willReturn(Optional.of(testCustomerDto));

        mockMvc.perform(get(CustomerController.CUSTOMER_PATH_ID, testCustomerDto.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(testCustomerDto.getId().toString())))
                .andExpect(jsonPath("$.name", is(testCustomerDto.getName())));
    }

    @Test
    void getCustomerByIdNotFound() throws Exception {
        given(customerService.getCustomerById(any(UUID.class))).willReturn(Optional.empty());

        mockMvc.perform(get(CustomerController.CUSTOMER_PATH_ID, UUID.randomUUID())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCustomerList() throws Exception {
        given(customerService.listCustomers()).willReturn(customerServiceImpl.listCustomers());

        mockMvc.perform(get(CustomerController.CUSTOMER_PATH)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(customerServiceImpl.listCustomers().size())));
    }
}