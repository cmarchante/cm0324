package com.tool.rental.services;

import com.tool.rental.dto.CustomerDtoMapper;
import com.tool.rental.dto.ProductDtoMapper;
import com.tool.rental.entities.Customer;
import com.tool.rental.entities.Product;
import com.tool.rental.entities.Rate;
import com.tool.rental.entities.Rental;
import com.tool.rental.services.CostCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class CostCalculatorTest {

    @Mock
    private CustomerDtoMapper customerDtoMapper;

    @Mock
    private ProductDtoMapper productDtoMapper;

    @Mock
    private Rental rental;

    @Mock
    private Rate rate;

    @Mock
    private Customer customer;

    @Mock
    private Product product;

    private CostCalculator costCalculator;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        costCalculator = new CostCalculator();
        when(rental.getProduct()).thenReturn(product);
        when(rental.getCustomer()).thenReturn(customer);
        when(rental.getProduct().getRate()).thenReturn(rate);
    }

    @Test
    void testCalculateCost_WeekdaysOnly() {
        // Setting rate conditions: charging only on weekdays
        when(rate.getDailyCharge()).thenReturn(new BigDecimal("10"));
        when(rate.getWeekdayCharge()).thenReturn(true);
        when(rate.getWeekendCharge()).thenReturn(false);
        when(rate.getHolidayCharge()).thenReturn(false);
        // Assuming rental is from Monday to Friday
        when(rental.getRentedTime()).thenReturn(Timestamp.valueOf(LocalDateTime.of(2023, 9, 4, 10, 0))); // Monday
        when(rental.getExpectedReturnTime()).thenReturn(Timestamp.valueOf(LocalDateTime.of(2023, 9, 8, 10, 0))); // Friday

        var costs = costCalculator.calculateRentalCost(rental);

        // Expected cost is 10 * 4 days = 40 Because of Labor Day Holiday
        assertEquals(new BigDecimal("40"), costs.totalCost());
    }

    @Test
    void testCalculateCost_WeekendsOnly() {
        when(rate.getDailyCharge()).thenReturn(new BigDecimal("15"));
        when(rate.getWeekdayCharge()).thenReturn(false);
        when(rate.getWeekendCharge()).thenReturn(true);
        when(rate.getHolidayCharge()).thenReturn(false);
        // Assuming rental is from Friday to Sunday
        when(rental.getRentedTime()).thenReturn(Timestamp.valueOf(LocalDateTime.of(2023, 9, 8, 10, 0))); // Friday
        when(rental.getExpectedReturnTime()).thenReturn(Timestamp.valueOf(LocalDateTime.of(2023, 9, 10, 10, 0))); // Sunday

        var costs = costCalculator.calculateRentalCost(rental);

        // Expected cost is 15 * 2 days = 30
        assertEquals(new BigDecimal("30"), costs.totalCost());
    }

    @Test
    void testCalculateCost_HolidaysOnly() {
        when(rate.getDailyCharge()).thenReturn(new BigDecimal("20"));
        when(rate.getWeekdayCharge()).thenReturn(false);
        when(rate.getWeekendCharge()).thenReturn(false);
        when(rate.getHolidayCharge()).thenReturn(true);
        // Assuming rental includes July 4th and Labor Day
        when(rental.getRentedTime()).thenReturn(Timestamp.valueOf(LocalDateTime.of(2023, 7, 3, 10, 0))); // Day before July 4th
        when(rental.getExpectedReturnTime()).thenReturn(Timestamp.valueOf(LocalDateTime.of(2023, 9, 5, 10, 0))); // Day after Labor Day

        var costs = costCalculator.calculateRentalCost(rental);

        // Expected cost is 20 * 2 days = 40 (since only July 4th and Labor Day should be charged)
        assertEquals(new BigDecimal("40"), costs.totalCost());
    }

    @Test
    void testCalculateCost_MixedConditions() {
        when(rate.getDailyCharge()).thenReturn(new BigDecimal("10"));
        when(rate.getWeekdayCharge()).thenReturn(true);
        when(rate.getWeekendCharge()).thenReturn(false);
        when(rate.getHolidayCharge()).thenReturn(true);
        // Assuming rental spans a week in July including July 4th
        when(rental.getRentedTime()).thenReturn(Timestamp.valueOf(LocalDateTime.of(2023, 7, 3, 10, 0))); // Monday
        when(rental.getExpectedReturnTime()).thenReturn(Timestamp.valueOf(LocalDateTime.of(2023, 7, 10, 10, 0))); // Next Monday

        var totalCost = costCalculator.calculateRentalCost(rental);

        // Expected cost is 10 * 5 weekdays + 10 * 1 holiday (July 4th) = 60
        assertEquals(new BigDecimal("60"), totalCost.totalCost());
    }

}

