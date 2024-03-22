package com.tool.rental.dto;

import jakarta.validation.constraints.*;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;

@Validated
public record CreateRentalDto (@NotNull Long customerId,
                               @NotNull Long productId,
                               @DecimalMin(value="1") int rentalDays,
                               @DecimalMin(value="0") @DecimalMax(value="100") int percentDiscount, @NotNull @FutureOrPresent LocalDate rentDate) {
}
