package com.tool.rental.services;

import com.tool.rental.dto.*;
import com.tool.rental.repositories.CustomerRepository;
import com.tool.rental.repositories.RateRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tool.rental.entities.Rental;
import com.tool.rental.entities.Product;
import com.tool.rental.repositories.ProductRepository;
import com.tool.rental.repositories.RentalRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class ApiService {
    private final ProductDtoMapper productDtoMapper;
    private final RentalDtoMapper rentalDtoMapper;
    private final CustomerDtoMapper customerDtoMapper;
    private final RentalCostDtoMapper rentalCostDtoMapper;
    private final RateDtoMapper rateDtoMapper;
    private final ProductRepository productRepository;
    private final RentalRepository rentalRepository;
    private final CustomerRepository customerRepository;
    private final RateRepository rateRepository;

    public ApiService(ProductRepository productRepository,
                      RentalRepository rentalRepository,
                      CustomerRepository customerRepository,
                      RateRepository rateRepository,
                      ProductDtoMapper productDtoMapper,
                      RentalDtoMapper rentalDtoMapper,
                      CustomerDtoMapper customerDtoMapper,
                      RentalCostDtoMapper rentalCostDtoMapper,
                      RateDtoMapper rateDtoMapper){
        this.rentalRepository = rentalRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.rateRepository = rateRepository;
        this.productDtoMapper = productDtoMapper;
        this.rentalDtoMapper = rentalDtoMapper;
        this.customerDtoMapper = customerDtoMapper;
        this.rentalCostDtoMapper = rentalCostDtoMapper;
        this.rateDtoMapper = rateDtoMapper;
    }

    public ProductDto getProduct(Long productId) {

        return productRepository.findProductById(productId)
                .map(productDtoMapper)
                .orElseThrow(() -> new RuntimeException("Product with id [%s] not found".formatted(productId)));
    }

    public List<ProductDto> getProducts(Pageable pageable) {

        return productRepository.findAll(pageable)
                .stream()
                .map(productDtoMapper).toList();
    }

    public List<RateDto> getRates(Pageable pageable) {

        return rateRepository.findAll(pageable)
                .stream()
                .map(rateDtoMapper).toList();
    }

    public List<ProductDto> getAvailableProductsForRent(Pageable pageable) {
        return productRepository.findByAvailable(true, pageable)
                .stream()
                .map(productDtoMapper).toList();
    }

    public CustomerDto getCustomer(Long customerId) {
        return customerRepository.findById(customerId)
                .map(customerDtoMapper)
                .orElseThrow(() -> new RuntimeException("Customer with id [%s] not found".formatted(customerId)));
    }

    @Transactional
    public RentalDto createRental(Long customerId, Long productId, int rentalDays, int percentDiscount, LocalDate rentDate) {
        RentalDto result = null;
        var product = productRepository.findProductById(productId).orElseThrow();
        if(product.getAvailable()) {
            var rental = new Rental();
            var customer = customerRepository.findCustomerById(customerId).orElseThrow();
            product.setAvailable(false);
            var productResult = productRepository.save(product);
            rental.setProduct(productResult);
            rental.setCustomer(customer);
            rental.setDiscount(percentDiscount);
            rental.setRentedTime(Timestamp.valueOf(rentDate.atStartOfDay()));
            rental.setExpectedReturnTime(Timestamp.valueOf(rentDate.plusDays(rentalDays).atStartOfDay()));
            result = rentalDtoMapper.apply(rentalRepository.save(rental));
        }
        return result;
    }

    public RentalDto getRental(Long rentalId) {
        return rentalRepository.findById(rentalId)
                .map(rentalDtoMapper)
                .orElseThrow(() -> new RuntimeException("Rental with id [%s] not found".formatted(rentalId)));
    }

    public List<RentalCostDto> getRentalEstimateCosts(List<CreateRentalDto> productRentals){

        var productMap = new HashMap<Long, CreateRentalDto>();
        productRentals.forEach(crd -> productMap.put(crd.productId(), crd));
        var products = productRepository.findProductsByIdIn(new ArrayList<>(productMap.keySet()));
        return products.stream().map(p -> {
            return rentalCostDtoMapper.apply(p, productMap.get(p.id()));
        }).toList();
    }

    @Transactional
    public RentalDto returnRental(Long productId, Long rentalId){
        RentalDto result = null;
        var productResult = productRepository.findProductById(productId);
        var rentalResult = rentalRepository.findById(rentalId);
        var product = productResult.orElseThrow();
        var rental = rentalResult.orElseThrow();
        if(!product.getAvailable() && rental.getReturnTime() == null){
            rental.setReturnTime(Timestamp.from(Instant.now()));
            product.setAvailable(true);
            productRepository.save(product);
            result = rentalDtoMapper.apply(rentalRepository.save(rental));

        }
        return result;
    }

    public List<RentalDto> getRentalsForCustomer(Long customerId) {

        return rentalRepository.findByCustomerIdOrderByReturnTime(customerId)
                .stream()
                .map(rentalDtoMapper).toList();
    }

    public String generateRentHtml(RentalDto rentalDto){
        String productAvailability = rentalDto.product().available() ? "Available" : "Not Available";
        String returnTime = rentalDto.returnTime() != null ? rentalDto.returnTime().toString() : "Not Returned Yet";
        String returnForm = rentalDto.returnTime() == null ?
                String.format("""
                    <form hx-post="/model/rental/return" method="post">
                        <input type="hidden" name="productId" value="%d">
                        <input type="hidden" name="rentalId" value="%d">
                        <button type="submit" class="mt-3 bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded cursor-pointer">Return Rental</button>
                    </form>
                    """, rentalDto.product().id(), rentalDto.id())
                : "";

        return String.format("""
                <h3 class="text-lg font-bold">Rental ID: %d</h3>
                <div><strong>Product:</strong> %s (%s - %s)</div>
                <div><strong>Description:</strong> %s</div>
                <div><strong>SKU:</strong> %s - %s</div>
                <div><strong>Customer:</strong> %s %s</div>
                <div><strong>Cost:</strong> $%.2f</div>
                <div><strong>Rented Date:</strong> %s</div>
                <div><strong>Expected Return Date:</strong> %s</div>
                <div><strong>Return Time:</strong> %s</div>
                %s <!-- Button appears only if returnTime is null or empty -->
            """,
                rentalDto.id(),
                rentalDto.product().name(), rentalDto.product().type(), rentalDto.product().brand(),
                rentalDto.product().description(),
                rentalDto.product().sku(), productAvailability,
                rentalDto.customer().firstName(), rentalDto.customer().lastName(),
                rentalDto.cost(),
                rentalDto.rentedTime(),
                rentalDto.expectedReturnTime(),
                returnTime, returnForm);
    }
}

