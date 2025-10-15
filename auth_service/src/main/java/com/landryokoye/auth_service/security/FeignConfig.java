package com.landryokoye.auth_service.security;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean // Configuration to log all information of the feign request response over Http
    Logger.Level feignLogger(){
        return Logger.Level.FULL;
    }
}
