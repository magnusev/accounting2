package com.evensberget.accounting.connector.nordigen.configuration

import org.slf4j.LoggerFactory
import org.springframework.http.HttpRequest
import org.springframework.http.client.ClientHttpRequestExecution
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.http.client.ClientHttpResponse
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.time.Instant

class LoggingRequestInterceptor : ClientHttpRequestInterceptor {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun intercept(
        request: HttpRequest,
        body: ByteArray,
        execution: ClientHttpRequestExecution
    ): ClientHttpResponse {
        val start = Instant.now()

        logRequest(request, body)
        val response = execution.execute(request, body)
        logResponse(response)

        logTimeOk(start, request)
        return response
    }

    private fun logRequest(request: HttpRequest, body: ByteArray) {
        log.info("============== Request ==============")
        log.info("URI         : {}", request.uri)
        log.info("Method      : {}", request.method)
        log.info("Headers     : {}", request.headers)
        log.info("Request body: {}", String(body, StandardCharsets.UTF_8))
    }

    private fun logResponse(response: ClientHttpResponse) {
        log.info("============== Response ==============")
        log.info("Status code  : {}", response.statusCode)
        log.info("Status text  : {}", response.statusText)
        log.info("Headers      : {}", response.headers)
        log.info("Response body: {}", response.body)
    }

    private fun logTimeOk(start: Instant, request: HttpRequest) {
        val time = Duration.between(start, Instant.now()).toMillis()
        val message = String.format("${request.method} [execution time: %d ms] [Url: ${request.uri}]", time)
        log.info(message)
    }
}
