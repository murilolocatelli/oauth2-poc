package com.customerapi.business.impl;

import com.customerapi.business.CustomerBusiness;
import com.customerapi.exception.EntityAlreadyExistsException;
import com.customerapi.exception.EntityNotFoundException;
import com.customerapi.model.Customer;
import com.customerapi.repository.CustomerRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerBusinessImpl implements CustomerBusiness {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public Page<Customer> findAll(final String name, final Pageable pageable) {
        final List<Customer> customers = new ArrayList<>();

        Page<Customer> page;

        if (name == null) {
            page = this.customerRepository.findAll(pageable);
        } else {
            page = this.customerRepository.findByName(name, pageable);
        }

        page.forEach(customers::add);

        return new PageImpl<>(customers, pageable, page.getTotalElements());
    }

    @Override
    @Transactional
    public Optional<Customer> create(Customer customer) {
        boolean customerExists = this.customerRepository.existsByName(customer.getName());

        if (customerExists) {
            throw new EntityAlreadyExistsException("Customer");
        }

        customer = this.customerRepository.save(customer);

        return Optional.of(customer);
    }

    @Override
    public Optional<Customer> findById(final Long id) {
        final Optional<Customer> optionalCustomer = this.customerRepository.findById(id);

        return Optional.ofNullable(optionalCustomer.orElse(null));
    }

    @Override
    public Optional<Customer> update(final Long id, final Customer customer) {
        final Optional<Customer> optionalCustomerSaved = this.customerRepository.findById(id);

        optionalCustomerSaved.orElseThrow(() -> new EntityNotFoundException("Customer"));

        customer.setId(id);

        this.customerRepository.save(customer);

        return Optional.of(customer);
    }

    @Override
    public void deleteById(final Long id) {
        final Optional<Customer> optionalCustomer = this.customerRepository.findById(id);

        optionalCustomer.orElseThrow(() -> new EntityNotFoundException("Customer"));

        this.customerRepository.deleteById(id);
    }

}
