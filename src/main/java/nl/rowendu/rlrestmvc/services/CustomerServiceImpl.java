package nl.rowendu.rlrestmvc.services;

import lombok.extern.slf4j.Slf4j;
import nl.rowendu.rlrestmvc.model.CustomerDto;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.*;

@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService {

    private final Map<UUID, CustomerDto> customerMap;

    public CustomerServiceImpl() {

        CustomerDto customerDto1 = CustomerDto.builder()
                .id(UUID.randomUUID())
                .version(1)
                .name("Bachus")
                .createdDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();

        CustomerDto customerDto2 = CustomerDto.builder()
                .id(UUID.randomUUID())
                .version(1)
                .name("Markus")
                .createdDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();

        CustomerDto customerDto3 = CustomerDto.builder()
                .id(UUID.randomUUID())
                .version(1)
                .name("Lazerus")
                .createdDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();

        this.customerMap = new HashMap<>();
        customerMap.put(customerDto1.getId(), customerDto1);
        customerMap.put(customerDto2.getId(), customerDto2);
        customerMap.put(customerDto3.getId(), customerDto3);
    }

    @Override
    public List<CustomerDto> listCustomers() {
        return new ArrayList<>(customerMap.values());
    }

    @Override
    public Optional<CustomerDto> getCustomerById(UUID id) {
        return Optional.of(customerMap.get(id));
    }

    @Override
    public CustomerDto saveNewCustomer(CustomerDto customerDto) {
        CustomerDto savedCustomerDto = CustomerDto.builder()
                .id(UUID.randomUUID())
                .version(1)
                .name(customerDto.getName())
                .createdDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();
        customerMap.put(savedCustomerDto.getId(), savedCustomerDto);
        return savedCustomerDto;
    }

    @Override
    public Optional<CustomerDto> updateCustomerById(UUID customerId, CustomerDto customerDto) {
        CustomerDto customerDtoToUpdate = customerMap.get(customerId);
        customerDtoToUpdate.setName(customerDto.getName());
        customerDtoToUpdate.setUpdateDate(LocalDateTime.now());
        return Optional.of(customerDtoToUpdate);
    }

    @Override
    public boolean deleteCustomerById(UUID customerId) {
        customerMap.remove(customerId);
        return true;
    }

    @Override
    public Optional<CustomerDto> patchCustomerById(UUID customerId, CustomerDto customerDto) {
        CustomerDto customerDtoToUpdate = customerMap.get(customerId);

        if (StringUtils.hasText(customerDto.getName())) {
            customerDtoToUpdate.setName(customerDto.getName());
        }
        return Optional.of(customerDtoToUpdate);
    }
}
