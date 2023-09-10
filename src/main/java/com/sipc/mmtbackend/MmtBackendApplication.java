package com.sipc.mmtbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication
@EnableTransactionManagement
@EnableCaching
public class MmtBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(MmtBackendApplication.class, args);
    }

}
