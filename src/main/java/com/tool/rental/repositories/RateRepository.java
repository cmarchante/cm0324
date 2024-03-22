package com.tool.rental.repositories;

import com.tool.rental.entities.Product;
import com.tool.rental.entities.Rate;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RateRepository extends JpaRepository<Rate, Long> {
    Optional<Rate> findRateById(Long id);
    Optional<Rate> findRateByType(String type);
}
