package com.authorizationserver.security;

import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers("/webjars/**", "/resources/**");
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
            .antMatchers("/login","/logout").permitAll()
            .antMatchers("/**").authenticated()
            .and()
            .formLogin()
            .loginProcessingUrl("/login.do")
            /*.usernameParameter("username")
            .passwordParameter("password")*/  //Essa configuração é opcional
            .loginPage("/login")
            .and()
            /*.httpBasic()
            .and()*/ //Essa configuração é opcional
            .logout()
            /*.logoutRequestMatcher(new AntPathRequestMatcher("/logout.do"))*/ //Essa configuração é opcional
            .and()
            .csrf().disable();
    }

}
