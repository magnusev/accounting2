package com.evensberget.accounting.connector.nordigen.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.web.client.RestTemplate

@Configuration
open class ClientConfiguration {

    @Bean
    open fun restTemplate(): RestTemplate {
        val factory = SimpleClientHttpRequestFactory()
        factory.setConnectTimeout(60000) // 5 seconds
        factory.setReadTimeout(180000) // 60 seconds
        val template = RestTemplate(factory)
        template.interceptors = listOf(LoggingRequestInterceptor())
        return template
    }

}
