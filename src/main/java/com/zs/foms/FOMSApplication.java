package com.zs.foms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springfox.documentation.oas.annotations.EnableOpenApi;

@Slf4j
@SpringBootApplication
@ServletComponentScan
@EnableOpenApi
@EnableTransactionManagement
public class FOMSApplication {
    public static void main(String[] args) {
        SpringApplication.run(FOMSApplication.class,args);
        log.info("The project was successfully launched...");
    }
}
