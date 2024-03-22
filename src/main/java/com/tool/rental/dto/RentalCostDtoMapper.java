package com.tool.rental.dto;

import com.tool.rental.entities.Product;
import com.tool.rental.entities.Rental;
import com.tool.rental.services.CostCalculator;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;

@Service
public class RentalCostDtoMapper{


    private final ProductDtoMapper productDtoMapper;
    private final CostCalculator costCalculator;

    public RentalCostDtoMapper(ProductDtoMapper productDtoMapper, CostCalculator costCalculator) {
        this.productDtoMapper = productDtoMapper;
        this.costCalculator = costCalculator;
    }

    public RentalCostDto apply(Product product, CreateRentalDto createRentalDto){
        var rentalTime = Timestamp.valueOf(createRentalDto.rentDate().atStartOfDay());
        var returnDate = Timestamp.valueOf(createRentalDto.rentDate().plusDays(createRentalDto.rentalDays()).atStartOfDay());
        var cost = costCalculator.calculate(product.getRate(), rentalTime, returnDate, createRentalDto.percentDiscount());

        return new RentalCostDto (productDtoMapper.apply(product), cost.totalCost(), cost.discountedCost(), cost.chargeableDays(), returnDate);
    }
}

