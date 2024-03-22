package com.tool.rental.dto;

import com.tool.rental.entities.Rental;
import com.tool.rental.services.CostCalculator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

@Service
public class RentalDtoMapper implements Function<Rental, RentalDto> {

    private final CustomerDtoMapper customerDtoMapper;
    private final ProductDtoMapper productDtoMapper;
    private final CostCalculator costCalculator;

    public RentalDtoMapper(CustomerDtoMapper customerDtoMapper, ProductDtoMapper productDtoMapper, CostCalculator costCalculator){
        this.customerDtoMapper = customerDtoMapper;
        this.productDtoMapper = productDtoMapper;
        this.costCalculator = costCalculator;
    }

    @Override
    public RentalDto apply(Rental rental){
        // Convert Timestamps to LocalDateTime
        LocalDateTime rentedDate = rental.getRentedTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime expectedReturnDate = rental.getExpectedReturnTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        // Create a formatter for the dates
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        var costs = costCalculator.calculateRentalCost(rental);

        return new RentalDto (rental.getId(),
                productDtoMapper.apply(rental.getProduct()),
                        customerDtoMapper.apply(rental.getCustomer()), costs.totalCost(),
                        rentedDate.format(formatter),
                        expectedReturnDate.format(formatter),
                        rental.getReturnTime());
    }

}
