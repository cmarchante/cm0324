package com.tool.rental.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class CreateRentalDtoTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void whenValidInput_thenNoConstraintViolations() {
        CreateRentalDto dto = new CreateRentalDto(1L, 1L, 10, 20, LocalDate.now());
        Set<ConstraintViolation<CreateRentalDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void whenNullCustomerId_thenConstraintViolation() {
        CreateRentalDto dto = new CreateRentalDto(null, 1L, 10, 20, LocalDate.now());
        Set<ConstraintViolation<CreateRentalDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenNullProductId_thenConstraintViolation() {
        CreateRentalDto dto = new CreateRentalDto(1L, null, 10, 20, LocalDate.now());
        Set<ConstraintViolation<CreateRentalDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenInvalidRentalDays_thenConstraintViolation() {
        CreateRentalDto dto = new CreateRentalDto(1L, 1L, 0, 20, LocalDate.now()); // rentalDays less than 1
        Set<ConstraintViolation<CreateRentalDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
    }

    @Test
    void whenInvalidPercentDiscount_thenConstraintViolation() {
        CreateRentalDto dto1 = new CreateRentalDto(1L, 1L, 10, -1, LocalDate.now()); // percentDiscount less than 0
        CreateRentalDto dto2 = new CreateRentalDto(1L, 1L, 10, 101, LocalDate.now()); // percentDiscount greater than 100
        Set<ConstraintViolation<CreateRentalDto>> violations1 = validator.validate(dto1);
        Set<ConstraintViolation<CreateRentalDto>> violations2 = validator.validate(dto2);
        assertFalse(violations1.isEmpty());
        assertFalse(violations2.isEmpty());
    }
}

