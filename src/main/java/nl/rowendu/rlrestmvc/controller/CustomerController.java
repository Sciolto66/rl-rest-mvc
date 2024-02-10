package nl.rowendu.rlrestmvc.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.rowendu.rlrestmvc.model.CustomerDto;
import nl.rowendu.rlrestmvc.services.CustomerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("${customer.api.path}")
public class CustomerController {

    private final CustomerService customerService;

    @Value("${customer.api.path}")
    private String customerPath;

    @PatchMapping("/{customerId}")
    public ResponseEntity<CustomerDto> patchCustomerById(@PathVariable("customerId") UUID customerId,
                                            @RequestBody CustomerDto customerDto){
        if (customerService.patchCustomerById(customerId, customerDto).isEmpty()) {
            throw new NotFoundException();
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomerById(@PathVariable("customerId") UUID customerId){
        if (!customerService.deleteCustomerById(customerId)) {
            throw new NotFoundException();
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/{customerId}")
    public ResponseEntity<CustomerDto> updateCustomerById(@PathVariable("customerId") UUID customerId,
                                                          @RequestBody CustomerDto customerDto){
        if (customerService.updateCustomerById(customerId, customerDto).isEmpty()) {
            throw new NotFoundException();
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @PostMapping()
    public ResponseEntity<CustomerDto> handlePost(@RequestBody CustomerDto customerDto) {
        CustomerDto savedCustomerDto = customerService.saveNewCustomer(customerDto);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", customerPath + "/" + savedCustomerDto.getId().toString());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    @GetMapping()
    public List<CustomerDto> listCustomers(){
        return customerService.listCustomers();
    }

    @GetMapping("/{customerId}")
    public CustomerDto getCustomerById(@PathVariable("customerId") UUID customerId){
        return customerService.getCustomerById(customerId)
                .orElseThrow(NotFoundException::new);
    }

}
