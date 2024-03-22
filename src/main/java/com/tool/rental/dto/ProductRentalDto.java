package com.tool.rental.dto;

import jakarta.validation.constraints.*;

public record ProductRentalDto (@NotNull Long productId, @Max(value=100) @Min(value=0) int rentalDays) {
}

