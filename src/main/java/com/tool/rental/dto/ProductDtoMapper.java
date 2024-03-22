package com.tool.rental.dto;

import com.tool.rental.entities.Product;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class ProductDtoMapper implements Function<Product, ProductDto> {

    @Override
    public ProductDto apply(Product product){
        return new ProductDto (product.id(), product.name(),
                product.description(), product.sku(), product.getAvailable(), product.getRate().getType()+" ($%s daily)".formatted(product.getRate().getDailyCharge()), product.getBrand());
    }
}
