package com.evensberget.accounting.connector.nordigen.components

import com.evensberget.accounting.connector.nordigen.dto.AccessTokenRequest
import com.evensberget.accounting.connector.nordigen.dto.AccessTokenResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class NordigenAccesTokenComponent(
    @Value("\${nordingen.secret.id}") val secretId: String,
    @Value("\${nordingen.secret.key}") val secretKey: String,
    private val template: RestTemplate
) {

    private var token: AccessTokenResponse? = null

    fun getToken(): String {
        if (token == null) token = accessToken()
        return token?.access
            ?: throw IllegalStateException("Token is not null, but access token is")
    }

    private fun accessToken(): AccessTokenResponse {
        val url = "https://ob.nordigen.com/api/v2/token/new/"

        val body = AccessTokenRequest(
            secret_id = secretId,
            secret_key = secretKey
        )

        val headers = HttpHeaders()
        headers.set("accept", "application/json")
        headers.set("Content-Type", "application/json")

        val entity = HttpEntity(body, headers)


        return template.postForObject(url, entity, AccessTokenResponse::class.java)
    }


}
