package com.tool.rental.services;

import com.tool.rental.entities.Rental;
import com.tool.rental.entities.Rate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

@Service
public class CostCalculator {

    public CalculatedCost calculateRentalCost(Rental rental) {
        Timestamp expectedReturn = rental.getExpectedReturnTime();
        Timestamp rentalTime = rental.getRentedTime();
        var rate = rental.getProduct().getRate();

        return calculate(rate, rentalTime, expectedReturn, rental.getDiscount());
    }

    public CalculatedCost calculate(Rate rate, Timestamp rentalTime, Timestamp expectedReturn, int discountPercent){

        BigDecimal dailyRate = rate.getDailyCharge();

        // Convert Timestamps to LocalDate
        var start = rentalTime.toLocalDateTime().toLocalDate();
        var end = expectedReturn.toLocalDateTime().toLocalDate();
        long chargeableDays = 0;

        // Initialize total cost
        BigDecimal totalCost = new BigDecimal("0");
        BigDecimal discountCost = new BigDecimal("0");

        // Loop through each day of the rental period
        for (var date = start; !date.isAfter(end); date = date.plusDays(1)) {
            boolean isWeekday = !(date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY);
            boolean isWeekend = !isWeekday;
            boolean isHoliday = isHoliday(date);

            // If it's a holiday and we don't charge for holidays, skip this day.
            if (isHoliday && !rate.getHolidayCharge()) {
                continue;
            }

            // If it's a weekday and we charge for weekdays, or if it's a weekend and we charge for weekends,
            // or if it's a holiday and we charge for holidays (we know we charge for holidays if we reach here),
            // then add the daily rate to the total cost.
            if ((isWeekday && rate.getWeekdayCharge())
                    || (isWeekend && rate.getWeekendCharge())
                    || (isHoliday && rate.getHolidayCharge())) {
                totalCost = totalCost.add(dailyRate);
                chargeableDays++;
            }
        }

        //Apply discount if any
        if(discountPercent > 0){
            BigDecimal discountDecimal = (new BigDecimal(discountPercent)).divide(new BigDecimal("100"));
            BigDecimal discountAmount = totalCost.multiply(discountDecimal);
            totalCost = totalCost.subtract(discountAmount);
            discountCost = discountAmount;
        }

        return new CalculatedCost(totalCost, discountCost, chargeableDays);
    }

    private boolean isHoliday(LocalDate date) {
        // Independence Day
        var julyFourth = LocalDate.of(date.getYear(), 7, 4);

        // Adjust if July 4 is on a weekend
        if (julyFourth.getDayOfWeek() == DayOfWeek.SATURDAY) {
            julyFourth = julyFourth.minusDays(1);
        } else if (julyFourth.getDayOfWeek() == DayOfWeek.SUNDAY) {
            julyFourth = julyFourth.plusDays(1);
        }

        // Labor Day (first Monday of September)
        LocalDate laborDay = LocalDate.of(date.getYear(), 9, 1).with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY));

        return date.equals(julyFourth) || date.equals(laborDay);
    }
}
