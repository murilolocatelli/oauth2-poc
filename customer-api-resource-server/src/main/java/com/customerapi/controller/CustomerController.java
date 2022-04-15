package com.customerapi.controller;

import com.customerapi.business.CustomerBusiness;
import com.customerapi.dto.OffsetLimitRequest;
import com.customerapi.dto.ResponseMeta;
import com.customerapi.model.Customer;

import java.util.Optional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
    value = {"/v1/customerapi/customers"},
    produces = MediaType.APPLICATION_JSON_VALUE)
public class CustomerController extends BaseController {

    @Autowired
    private CustomerBusiness customerBusiness;

    public CustomerController() {
        super(Customer.class);
    }

    @CrossOrigin
    @GetMapping
    public ResponseEntity<ResponseMeta> get(@Valid final String name, @Valid final OffsetLimitRequest pageable) {
        final Page<Customer> page = this.customerBusiness.findAll(name, pageable);

        return super.buildResponse(HttpStatus.OK, page, pageable);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseMeta> post(@RequestBody @Valid final Customer customer) {
        final Optional<Customer> optionalCustomer = this.customerBusiness.create(customer);

        return super.buildResponse(HttpStatus.CREATED, optionalCustomer.orElse(null), null);
    }

    @GetMapping(value = "/{id}")
    public @ResponseBody ResponseEntity<ResponseMeta> getById(@PathVariable final Long id) {
        final Optional<Customer> optionalCustomer = this.customerBusiness.findById(id);

        return super.buildResponse(HttpStatus.OK, optionalCustomer.orElse(null), null);
    }

    @PutMapping(value = "/{id}")
    public @ResponseBody ResponseEntity<ResponseMeta> put(
        @PathVariable final Long id, @RequestBody @Valid final Customer customer) {

        final Optional<Customer> optionalCustomer = this.customerBusiness.update(id, customer);

        return super.buildResponse(HttpStatus.OK, optionalCustomer.orElse(null), null);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<ResponseMeta> deleteById(@PathVariable final Long id) {
        this.customerBusiness.deleteById(id);

        return super.buildResponse(HttpStatus.NO_CONTENT, null, null);
    }

}
