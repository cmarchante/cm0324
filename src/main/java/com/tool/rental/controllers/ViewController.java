package com.tool.rental.controllers;

import com.tool.rental.dto.CreateRentalDto;
import com.tool.rental.dto.ProductDto;
import com.tool.rental.dto.RentalDto;
import com.tool.rental.services.ApiService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Validated
public class ViewController {

    private final ApiService apiService;

    public ViewController(ApiService apiService){
        this.apiService = apiService;
    }

    @GetMapping("/products")
    public String showProducts(Model model) {
        return "main";
    }

    @GetMapping("/model/products/available")
    public String getAvailableProducts(Model model, @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "20") int size){
        var availableRentals = apiService.getAvailableProductsForRent(PageRequest.of(page, size));
        model.addAttribute("aproducts", availableRentals);
        return "partials/aproducts";
    }

    @PostMapping("/model/rental/create")
    public ResponseEntity<String> createRental(Model model, CreateRentalDto request){
        var today = LocalDate.now();
        //Workaround for dto validation not working
        if(request.rentalDays() > 0
                && (request.percentDiscount() >= 0 && request.percentDiscount() <= 100)
                && (request.rentDate() != null
                && (request.rentDate().equals(today) || request.rentDate().isAfter(today)))) {
            var rental = apiService.createRental(request.customerId(), request.productId(), request.rentalDays(), request.percentDiscount(), request.rentDate());
            String response = rental != null ? """
                    <div hx-swap-oob="true" hx-target="#product-%s" hx-swap="remove"></div>
                    """.formatted(rental.product().id()) : null;
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body("Validation Failed");
    }

    @PostMapping("/model/rental/return")
    public ResponseEntity<String> returnRental(Model model, @RequestParam Long productId, @RequestParam Long rentalId){
        var rental = apiService.returnRental(productId, rentalId);
        String result = null;
        if(rental != null){
            result = apiService.generateRentHtml(rental);
        }
        return ResponseEntity.ok(result);
    }


    @GetMapping("/model/rental/estimate")
    public ResponseEntity<String> getRentalEstimate(@Valid CreateRentalDto productRental){
        var today = LocalDate.now();
        //Workaround for dto validation not working
        if(productRental.rentalDays() > 0
                && (productRental.percentDiscount() >= 0 && productRental.percentDiscount() <= 100)
                && (productRental.rentDate() != null
                    && (productRental.rentDate().equals(today) || productRental.rentDate().isAfter(today)))){
            var estimates = apiService.getRentalEstimateCosts(List.of(productRental));
            var rental = estimates.getFirst();
            // Convert Timestamp to LocalDateTime
            LocalDateTime returnDate = rental.returnDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

            // Create a formatter for the returnDate
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

            // Format the returnDate using the formatter
            String formattedReturnDate = returnDate.format(formatter);
            String estimateHtml = """
                    <div><strong>Chargeable Days:</strong> %s</div>
                    <div><strong>Prediscount Price:</strong> $%.2f</div>
                    <div><strong>Final Price:</strong> $%.2f</div>
                    <div><strong>Discount:</strong> $%.2f</div>
                    <div><strong>Estimated Return:</strong> %s</div>""".formatted(rental.chargeableDays(), rental.rentalCost().add(rental.discountCost()),rental.rentalCost(), rental.discountCost(), formattedReturnDate);
            return ResponseEntity.ok(estimateHtml);
        }
        return ResponseEntity.badRequest().body("Validation Failed");
    }

    @GetMapping("/model/products")
    public String getProducts(Model model, @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "20") int size){
        var products = apiService.getProducts(PageRequest.of(page, size));
        model.addAttribute("products", products);
        return "partials/products";
    }

    @GetMapping("/model/rates")
    public String getRates(Model model, @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "20") int size){
        var rates = apiService.getRates(PageRequest.of(page, size));
        model.addAttribute("rates", rates);
        return "partials/rates";
    }

    @GetMapping("/model/rentals/customer/{id}")
    public String getRentalsForCustomer(Model model, @PathVariable Long id){
        var rentals = apiService.getRentalsForCustomer(id);
        model.addAttribute("mrentals", rentals);
        return "partials/mrentals";
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
