package com.customerapi.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;

@Configuration
@EnableResourceServer
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    private static final String RESOURCE_ID = "customerapi";

    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @Autowired
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public RemoteTokenServices remoteTokenServices() {
        RemoteTokenServices remoteTokenServices = new RemoteTokenServices();
        remoteTokenServices.setClientId("resource-server");
        remoteTokenServices.setClientSecret("abc");
        remoteTokenServices.setCheckTokenEndpointUrl("http://localhost:8081/oauth/check_token");
        return remoteTokenServices;
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.resourceId(RESOURCE_ID);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
            .antMatchers(HttpMethod.GET, "/**").access("#oauth2.hasScope('read')")
            .antMatchers(HttpMethod.POST, "/**").access("#oauth2.hasScope('write')")
            .antMatchers(HttpMethod.PATCH, "/**").access("#oauth2.hasScope('write')")
            .antMatchers(HttpMethod.PUT, "/**").access("#oauth2.hasScope('write')")
            .antMatchers(HttpMethod.DELETE, "/**").access("#oauth2.hasScope('write')")
            .and()
            .exceptionHandling()
            .accessDeniedHandler(this.customAccessDeniedHandler)
            .authenticationEntryPoint(this.customAuthenticationEntryPoint)
            .and()
            .cors();
    }

}
