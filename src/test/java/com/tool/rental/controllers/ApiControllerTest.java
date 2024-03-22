package com.tool.rental.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tool.rental.dto.*;
import com.tool.rental.services.ApiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ApiController.class)
class ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ApiService apiService;

    @InjectMocks
    private ApiController apiController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        //Fixes LocalDate serialization issue
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    @Test
    void getProductTest() throws Exception {
        ProductDto productDto = new ProductDto(1L, "Hammer", "16oz claw hammer", "SKU123456", true, "Hand Tool", "DeWalt");
        when(apiService.getProduct(1L)).thenReturn(productDto);

        mockMvc.perform(get("/api/products/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(productDto.id()))
                .andExpect(jsonPath("$.name").value(productDto.name()));
    }

    @Test
    void getProductsTest() throws Exception {
        ProductDto productDto = new ProductDto(1L, "Drill", "Power drill", "SKU654321", true, "Power Tool", "DeWalt");
        List<ProductDto> productDtos = Collections.singletonList(productDto);
        when(apiService.getProducts(any(PageRequest.class))).thenReturn(productDtos);

        mockMvc.perform(get("/api/products")
                        .param("page", "0")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(productDto.id()))
                .andExpect(jsonPath("$[0].name").value(productDto.name()));
    }

    @Test
    void getAvailableProductsTest() throws Exception {
        ProductDto productDto = new ProductDto(2L, "Saw", "Hand saw", "SKU789123", true, "Hand Tool", "Makita");
        List<ProductDto> productDtos = Collections.singletonList(productDto);
        when(apiService.getAvailableProductsForRent(any(PageRequest.class))).thenReturn(productDtos);

        mockMvc.perform(get("/api/products/available")
                        .param("page", "0")
                        .param("size", "20")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(productDto.id()))
                .andExpect(jsonPath("$[0].name").value(productDto.name()));
    }

    @Test
    void getCustomerTest() throws Exception {
        CustomerDto customerDto = new CustomerDto(1L, "John", "Doe");
        when(apiService.getCustomer(1L)).thenReturn(customerDto);

        mockMvc.perform(get("/api/customer/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customerDto.id()))
                .andExpect(jsonPath("$.firstName").value(customerDto.firstName()));
    }

    @Test
    void getRentalTest() throws Exception {
        RentalDto rentalDto = new RentalDto(1L, null, null, null, null, null, null);
        when(apiService.getRental(1L)).thenReturn(rentalDto);

        mockMvc.perform(get("/api/rentals/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(rentalDto.id()));
    }

    @Test
    void getRentalsForCustomerTest() throws Exception {
        RentalDto rentalDto = new RentalDto(2L, null, null, null, null, null, null);
        List<RentalDto> rentalDtos = Collections.singletonList(rentalDto);
        when(apiService.getRentalsForCustomer(1L)).thenReturn(rentalDtos);

        mockMvc.perform(get("/api/rentals/customer/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(rentalDto.id()));
    }

    @Test
    void createRentalTest() throws Exception {
        // Setup test data
        LocalDate rentDate = LocalDate.now();
        RentalDto rentalDto = new RentalDto(1L, null, null, null, null, null, null);
        when(apiService.createRental(1L, 1L, 1, 0, rentDate)).thenReturn(rentalDto);
        mockMvc.perform(post("/api/rental/create")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("customerId", "1")
                        .param("productId", "1")
                        .param("rentalDays", "1")
                        .param("percentDiscount", "0")
                        .param("rentDate", rentDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(rentalDto.id()));
    }

    @Test
    void getRentalsCostTest() throws Exception {
        List<CreateRentalDto> createRentalDtos = new ArrayList<>();
        createRentalDtos.add(new CreateRentalDto(1L, 2L, 5, 10, LocalDate.now())); // Sample data

        List<RentalCostDto> rentalCostDtos = new ArrayList<>();
        ProductDto productDto = new ProductDto(2L, "Saw", "Hand saw", "SKU789123", true, "Hand Tool", "Makita");
        rentalCostDtos.add(new RentalCostDto(productDto, new BigDecimal(1), BigDecimal.ZERO, 2, Timestamp.valueOf(LocalDate.now().plusDays(2).atStartOfDay())));

        when(apiService.getRentalEstimateCosts(createRentalDtos)).thenReturn(rentalCostDtos);

        mockMvc.perform(post("/api/rentals/cost")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRentalDtos)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(rentalCostDtos)));
    }
}
