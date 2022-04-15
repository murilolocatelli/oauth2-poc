package com.authorizationserver.repository;

import com.authorizationserver.model.Credential;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CredentialRepository extends JpaRepository<Credential, Long>  {

    @Query
    Optional<Credential> findByEmail(final String email);

}
