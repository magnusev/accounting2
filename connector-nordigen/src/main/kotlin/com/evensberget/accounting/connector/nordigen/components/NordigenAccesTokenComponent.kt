package com.evensberget.accounting.connector.nordigen.components

import com.evensberget.accounting.connector.nordigen.domain.AccessToken
import com.evensberget.accounting.connector.nordigen.dto.AccessTokenRequest
import com.evensberget.accounting.connector.nordigen.dto.AccessTokenResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class NordigenAccesTokenComponent(
    @Value("\${nordingen.secret.id}") val secretId: String,
    @Value("\${nordingen.secret.key}") val secretKey: String,
    private val accessTokenRepository: AccessTokenRepository,
    private val template: RestTemplate
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    private var token: AccessToken? = null

    fun getToken(): String {
        if (token == null) token = getAccessToken()
        return token?.access
            ?: throw IllegalStateException("Token is not null, but access token is")
    }

    private fun getAccessToken(): AccessToken {
        val databaseToken = accessTokenRepository.getAccessToken()

        if (databaseToken != null) {
            logger.info("Using cached Access Token")
            return databaseToken
        }

        val newToken = accessToken().toModel()
        accessTokenRepository.addReplaceAccessToken(newToken)

        return newToken

    }

    private fun accessToken(): AccessTokenResponse {
        logger.info("Getting Access Token from Nordigen...")

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
