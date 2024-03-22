package com.tool.rental.services;

import com.tool.rental.dto.*;
import com.tool.rental.entities.Product;
import com.tool.rental.entities.Rate;
import com.tool.rental.entities.Rental;
import com.tool.rental.entities.Customer;
import com.tool.rental.repositories.ProductRepository;
import com.tool.rental.repositories.RateRepository;
import com.tool.rental.repositories.RentalRepository;
import com.tool.rental.repositories.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(SpringExtension.class)
public class ApiServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private RateRepository rateRepository;

    @Mock
    private ProductDtoMapper productDtoMapper;

    @Mock
    private RentalDtoMapper rentalDtoMapper;

    @Mock
    private CustomerDtoMapper customerDtoMapper;

    @InjectMocks
    private ApiService apiService;

    // Sample DTOs for use in tests
    private final ProductDto sampleProductDto = new ProductDto(1L, "Hammer", "16oz claw hammer", "SKU123456", true, "Hand Tool", "Craftsman");
    private final CustomerDto sampleCustomerDto = new CustomerDto(1L, "John", "Doe");
    private final RentalDto sampleRentalDto = new RentalDto(1L, sampleProductDto, sampleCustomerDto, BigDecimal.ZERO,
            "",
            "",
            null);

    @BeforeEach
    void setUp() {
        // Setup necessary mock behavior here, if any
    }

    @Test
    void getProduct() {
        Product product = new Product(); // Assume this is a valid product entity
        when(productRepository.findProductById(1L)).thenReturn(Optional.of(product));
        when(productDtoMapper.apply(product)).thenReturn(sampleProductDto);

        ProductDto result = apiService.getProduct(1L);

        assertThat(result).isEqualTo(sampleProductDto);
    }

    @Test
    void getProducts() {
        Product product = new Product(); // Setup your product entity
        when(productRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Collections.singletonList(product)));
        when(productDtoMapper.apply(product)).thenReturn(sampleProductDto);

        List<ProductDto> result = apiService.getProducts(Pageable.unpaged());

        assertThat(result).containsExactly(sampleProductDto);
    }

    @Test
    void getAvailableProductsForRent() {
        Product product = new Product(); // Setup your product entity
        when(productRepository.findByAvailable(eq(true), any(Pageable.class))).thenReturn(Collections.singletonList(product));
        when(productDtoMapper.apply(product)).thenReturn(sampleProductDto);

        List<ProductDto> result = apiService.getAvailableProductsForRent(PageRequest.of(0, 10));

        assertThat(result).containsExactly(sampleProductDto);
    }

    @Test
    void getCustomer() {
        Customer customer = new Customer(); // Setup your customer entity
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerDtoMapper.apply(customer)).thenReturn(sampleCustomerDto);

        CustomerDto result = apiService.getCustomer(1L);

        assertThat(result).isEqualTo(sampleCustomerDto);
    }

    @Test
    void createRental() {
        Customer customer = new Customer();
        Product product = new Product();
        product.setAvailable(true);
        Rental rental = new Rental();
        Rate rate = new Rate();
        when(productRepository.findProductById(1L)).thenReturn(Optional.of(product));
        when(rateRepository.findRateByType("")).thenReturn(Optional.of(rate));
        when(customerRepository.findCustomerById(1L)).thenReturn(Optional.of(customer));
        when(rentalRepository.save(any(Rental.class))).thenReturn(rental);
        when(rentalDtoMapper.apply(rental)).thenReturn(sampleRentalDto);

        RentalDto result = apiService.createRental(1L, 1L, 1, 0, LocalDate.now());

        assertThat(result).isEqualTo(sampleRentalDto);
    }

    @Test
    void getRental() {
        Rental rental = new Rental();
        when(rentalRepository.findById(1L)).thenReturn(Optional.of(rental));
        when(rentalDtoMapper.apply(rental)).thenReturn(sampleRentalDto);

        RentalDto result = apiService.getRental(1L);

        assertThat(result).isEqualTo(sampleRentalDto);
    }

    @Test
    void getRentalsForCustomer() {
        Rental rental = new Rental();
        when(rentalRepository.findByCustomerIdOrderByReturnTime(1L)).thenReturn(Collections.singletonList(rental));
        when(rentalDtoMapper.apply(rental)).thenReturn(sampleRentalDto);

        List<RentalDto> result = apiService.getRentalsForCustomer(1L);

        assertThat(result).containsExactly(sampleRentalDto);
    }
}
