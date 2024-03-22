package com.tool.rental.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

public record RentalCostDto (ProductDto product, BigDecimal rentalCost, BigDecimal discountCost, long chargeableDays, Timestamp returnDate) {
}
