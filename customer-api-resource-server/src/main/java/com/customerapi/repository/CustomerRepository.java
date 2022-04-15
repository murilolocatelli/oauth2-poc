package com.customerapi.repository;

import com.customerapi.model.Customer;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>  {

    @Query
    boolean existsByName(String name);

    @Query
    Page<Customer> findByName(String name, Pageable pageable);

    @Query
    Optional<Customer> findByEmail(final String email);

}
