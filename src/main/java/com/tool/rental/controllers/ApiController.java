package com.tool.rental.controllers;

import com.tool.rental.dto.*;
import com.tool.rental.services.ApiService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.List;

@RestController
@Validated
public class ApiController {
    private final ApiService apiService;

    public ApiController(ApiService apiService){
        this.apiService = apiService;
    }

    @GetMapping("/api/products/{id}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable Long id){
        var product = apiService.getProduct(id);
        return ResponseEntity.ok(product);
    }

    @GetMapping("/api/products")
    public ResponseEntity<List<ProductDto>> getProducts(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "20") int size){
        var products = apiService.getProducts(PageRequest.of(page, size));
        return ResponseEntity.ok(products);
    }

    @GetMapping("/api/products/available")
    public ResponseEntity<List<ProductDto>> getAvailableProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var availableRentals = apiService.getAvailableProductsForRent(PageRequest.of(page, size));
        return ResponseEntity.ok(availableRentals);
    }

    @GetMapping("/api/customer/{id}")
    public ResponseEntity<CustomerDto> getCustomer(@PathVariable Long id){
        var customer = apiService.getCustomer(id);
        return ResponseEntity.ok(customer);
    }

    @GetMapping("/api/rentals/{id}")
    public ResponseEntity<RentalDto> getRental(@PathVariable Long id){
        var rental = apiService.getRental(id);
        return ResponseEntity.ok(rental);
    }

    @PostMapping("/api/rentals/cost")
    public ResponseEntity<List<RentalCostDto>> getRentalsCost(@Valid @RequestBody List<CreateRentalDto> productRentals){
        var rentals = apiService.getRentalEstimateCosts(productRentals);
        return ResponseEntity.ok(rentals);
    }

    @GetMapping("/api/rentals/customer/{id}")
    public ResponseEntity<List<RentalDto>> getRentalsForCustomer(@PathVariable Long id){
        var rentals = apiService.getRentalsForCustomer(id);
        return ResponseEntity.ok(rentals);
    }

    @PostMapping("/api/rental/create")
    public ResponseEntity<RentalDto> createRental(CreateRentalDto request){
        var rental = apiService.createRental(request.customerId(), request.productId(), request.rentalDays(), request.percentDiscount(), request.rentDate());
        return ResponseEntity.ok(rental);
    }
}
