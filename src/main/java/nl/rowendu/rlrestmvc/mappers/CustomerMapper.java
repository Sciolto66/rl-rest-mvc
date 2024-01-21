package nl.rowendu.rlrestmvc.mappers;

import nl.rowendu.rlrestmvc.entities.Customer;
import nl.rowendu.rlrestmvc.model.CustomerDto;
import org.mapstruct.Mapper;

@Mapper
public interface CustomerMapper {
    Customer customerDtoToCustomer(CustomerDto customerDto);
    CustomerDto customerToCustomerDto(Customer customer);
}
