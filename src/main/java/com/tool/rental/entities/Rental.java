package com.tool.rental.entities;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "rentals")
public class Rental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false, referencedColumnName = "id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false, referencedColumnName = "id")
    private Customer customer;

    @Column(name = "rented_time", nullable = false)
    private Timestamp rentedTime;

    @Column(name = "expected_return_time")
    private Timestamp expectedReturnTime;

    @Column(name = "return_time")
    private Timestamp returnTime;

    @Column(name = "discount")
    private int discount;

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Timestamp getRentedTime() {
        return rentedTime;
    }

    public void setRentedTime(Timestamp rentedTime) {
        this.rentedTime = rentedTime;
    }

    public Timestamp getExpectedReturnTime() {
        return expectedReturnTime;
    }

    public void setExpectedReturnTime(Timestamp expectedReturnTime) {
        this.expectedReturnTime = expectedReturnTime;
    }

    public Timestamp getReturnTime() {
        return returnTime;
    }

    public void setReturnTime(Timestamp returnTime) {
        this.returnTime = returnTime;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }
}