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

    private Map<UUID, CustomerDto> customerMap;

    public CustomerServiceImpl() {

        CustomerDto customerDto1 = CustomerDto.builder()
                .id(UUID.randomUUID())
                .version(1)
                .name("Bachus")
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .build();

        CustomerDto customerDto2 = CustomerDto.builder()
                .id(UUID.randomUUID())
                .version(1)
                .name("Markus")
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .build();

        CustomerDto customerDto3 = CustomerDto.builder()
                .id(UUID.randomUUID())
                .version(1)
                .name("Lazerus")
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
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
        log.debug("Get Customer by Id - in service. Id: " + id.toString());

        return Optional.of(customerMap.get(id));
    }

    @Override
    public CustomerDto saveNewCustomer(CustomerDto customerDto) {
        CustomerDto savedCustomerDto = CustomerDto.builder()
                .id(UUID.randomUUID())
                .version(1)
                .name(customerDto.getName())
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .build();
        customerMap.put(savedCustomerDto.getId(), savedCustomerDto);
        return savedCustomerDto;
    }

    @Override
    public void updateCustomerById(UUID customerId, CustomerDto customerDto) {
        CustomerDto customerDtoToUpdate = customerMap.get(customerId);
        customerDtoToUpdate.setName(customerDto.getName());
        customerDtoToUpdate.setLastModifiedDate(LocalDateTime.now());
    }

    @Override
    public void deleteCustomerById(UUID customerId) {
        customerMap.remove(customerId);
    }

    @Override
    public void patchCustomerById(UUID customerId, CustomerDto customerDto) {
        CustomerDto customerDtoToUpdate = customerMap.get(customerId);
        boolean isUpdated = false;

        if (StringUtils.hasText(customerDto.getName())) {
            customerDtoToUpdate.setName(customerDto.getName());
            isUpdated = true;
        }

        // Check if any field was updated
        if (isUpdated) {
            customerDtoToUpdate.setLastModifiedDate(LocalDateTime.now());
        }
    }
}
