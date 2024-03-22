package com.tool.rental.dto;

import org.springframework.stereotype.Service;
import com.tool.rental.entities.Customer;

import java.util.function.Function;

@Service
public class CustomerDtoMapper implements Function<Customer, CustomerDto> {

    @Override
    public CustomerDto apply(Customer customer){
        return new CustomerDto (customer.getId(), customer.getFirstName(),
                customer.getLastName());
    }
}
