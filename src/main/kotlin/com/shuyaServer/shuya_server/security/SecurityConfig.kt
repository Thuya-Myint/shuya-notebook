package com.shuyaServer.shuya_server.security

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Bean
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
class SecurityConfig {

@Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain{
        return http
            .csrf { csrf->csrf.disable() }
            .authorizeExchange {
                it
                    .pathMatchers("/api/v1/public/**").permitAll()
                    .pathMatchers("/api/v1/admin/**").permitAll()
                    .anyExchange().permitAll()
            }
            .httpBasic {  }
            .build()

    }
}