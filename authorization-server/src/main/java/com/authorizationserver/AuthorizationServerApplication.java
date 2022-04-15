package com.authorizationserver;

import java.util.Collections;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(scanBasePackages = AuthorizationServerApplication.BASE_PACKAGE)
@EnableSwagger2
public class AuthorizationServerApplication {

    @Value("${application.name}")
    private String applicationName;

    @Value("${application.description}")
    private String applicationDescription;

    @Value("${application.version}")
    private String applicationVersion;

    static final String BASE_PACKAGE = "com.authorizationserver";

    private static final String CONTROLLER_PACKAGE = "com.authorizationserver.controller";

    public static void main(String[] args) {
        SpringApplication.run(AuthorizationServerApplication.class, args);
    }

    @Bean
    public Docket docket(final ApiInfo apiInfo) {
        return new Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.basePackage(CONTROLLER_PACKAGE))
            .paths(PathSelectors.any())
            .build()
            .apiInfo(apiInfo);
    }

    @Bean
    public ApiInfo apiInfo() {
        applicationVersion = Optional.ofNullable(this.applicationVersion)
            .map(t -> t.replace("-SNAPSHOT", ""))
            .orElse(this.applicationVersion);

        return new ApiInfo(this.applicationName, this.applicationDescription, applicationVersion,
            null, null, null, null, Collections.emptyList());
    }

}
