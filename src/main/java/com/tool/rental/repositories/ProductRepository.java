package com.tool.rental.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.tool.rental.entities.Product;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByAvailable(Boolean available, Pageable pageable);
    Optional<Product> findProductById(Long id);

    List<Product> findProductsByIdIn(List<Long> ids);
}


