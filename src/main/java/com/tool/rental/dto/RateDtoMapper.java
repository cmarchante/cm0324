package com.tool.rental.dto;

import com.tool.rental.dto.RateDto;
import com.tool.rental.entities.Rate;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class RateDtoMapper implements Function<Rate, RateDto> {

    @Override
    public RateDto apply(Rate rate){
        return new RateDto (rate.getType(), rate.getDailyCharge(), rate.getWeekdayCharge(), rate.getWeekendCharge(), rate.getHolidayCharge());
    }
}