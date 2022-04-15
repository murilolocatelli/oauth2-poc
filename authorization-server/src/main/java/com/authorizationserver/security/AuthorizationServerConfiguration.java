package com.authorizationserver.security;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.approval.JdbcApprovalStore;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;

@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

    // Necessário apenas para utilização do grant type Resource Owner Password Credentials
    @Autowired
    private AuthenticationManager authenticationManager;

    /*@Autowired
    private ClientDetailsService clientDetailsService;*/

    @Autowired
    private DataSource dataSource;

    @Bean
    public JdbcClientDetailsService clientDetailsService() {
        return new JdbcClientDetailsService(this.dataSource);
    }

    @Bean
    public TokenStore tokenStore() {
        return new JdbcTokenStore(this.dataSource);
    }

    @Bean
    public ApprovalStore approvalStore() {
        return new JdbcApprovalStore(this.dataSource);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) {
        security.checkTokenAccess("hasAuthority('introspection')");
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.jdbc(this.dataSource);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        DefaultOAuth2RequestFactory requestFactory = new DefaultOAuth2RequestFactory(this.clientDetailsService());
        requestFactory.setCheckUserScopes(true);

        endpoints
            .authenticationManager(this.authenticationManager)
            .requestFactory(requestFactory)
            .approvalStore(this.approvalStore())
            .tokenStore(this.tokenStore());
    }

}
