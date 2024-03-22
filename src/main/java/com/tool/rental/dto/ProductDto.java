package com.tool.rental.dto;

public record ProductDto(Long id, String name, String description, String sku, Boolean available, String type, String brand) {

}
