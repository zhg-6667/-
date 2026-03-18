package com.hk.simba.license.service;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import com.github.ltsopensource.spring.boot.annotation.EnableTaskTracker;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;

@EnableApolloConfig
@EnableDubbo(scanBasePackages = "com.hk.simba.license")
@SpringBootApplication(scanBasePackages = {"com.hk.simba.license"}, exclude = JpaRepositoriesAutoConfiguration.class)

@EnableCaching
@EnableTaskTracker
public class LicenseServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LicenseServiceApplication.class, args);
    }

}
