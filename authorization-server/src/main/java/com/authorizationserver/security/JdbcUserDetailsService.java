package com.authorizationserver.security;

import static java.text.MessageFormat.format;

import com.authorizationserver.model.Credential;
import com.authorizationserver.repository.CredentialRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JdbcUserDetailsService implements UserDetailsService {

    @Autowired
    private CredentialRepository credentialRepository;

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {

        final Credential credential =
            this.credentialRepository.findByEmail(username).orElseThrow(() ->
                new UsernameNotFoundException(format("User {0} can not be found", username)));

        return new JdbcUserDetails(credential);
    }

}
