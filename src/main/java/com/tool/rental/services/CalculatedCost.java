package com.tool.rental.services;

import java.math.BigDecimal;

public record CalculatedCost (BigDecimal totalCost , BigDecimal discountedCost, long chargeableDays) {
}
