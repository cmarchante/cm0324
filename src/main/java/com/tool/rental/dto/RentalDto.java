package com.tool.rental.dto;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;

public record RentalDto (Long id, ProductDto product, CustomerDto customer, BigDecimal cost,
                         String rentedTime, String expectedReturnTime, Timestamp returnTime) {
}
