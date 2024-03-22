package com.tool.rental.dto;

import java.math.BigDecimal;

public record RateDto (String type, BigDecimal dailyCharge, Boolean weekdayCharge, Boolean weekendCharge, Boolean holidayCharge ) {
}
