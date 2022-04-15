package com.customerapi.business;

import com.customerapi.model.Customer;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerBusiness {

    Page<Customer> findAll(final String name, final Pageable pageable);

    Optional<Customer> create(Customer customer);

    Optional<Customer> findById(final Long id);

    Optional<Customer> update(final Long id, final Customer customer);

    void deleteById(final Long id);

}
