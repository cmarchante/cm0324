package com.tool.rental.repositories;

import com.tool.rental.entities.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {
    List<Rental> findByCustomerId(Long customerId);
    List<Rental> findByCustomerIdOrderByReturnTime(Long customerId);
}
