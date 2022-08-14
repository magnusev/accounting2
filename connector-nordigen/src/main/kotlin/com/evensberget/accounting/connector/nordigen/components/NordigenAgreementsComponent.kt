package com.evensberget.accounting.connector.nordigen.components

import com.evensberget.accounting.connector.nordigen.dto.EndUserAgreementResponse
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.util.*

@Component
class NordigenAgreementsComponent(
    private val accessToken: NordigenAccesTokenComponent,
    private val template: RestTemplate
) {

    private val baseUrl = "https://ob.nordigen.com/api/v2/agreements/enduser/"

    fun create(institutionId: String): EndUserAgreementResponse {

        val body = CreateEnduserAgreementRequest(
            institution_id = institutionId,
            max_historical_days = 730,
            access_valid_for_days = 30,
            access_scope = listOf("balances", "details", "transactions")
        )

        val entity = HttpEntity(body, getHeaders())

        return template.postForObject(baseUrl, entity, EndUserAgreementResponse::class.java)
    }

    fun get(limit: Int = 100, offset: Int = 0): List<EndUserAgreementResponse> {

        val url = UriComponentsBuilder.fromUriString(baseUrl)
            .queryParam("limit", limit)
            .queryParam("offset", offset)
            .toUriString()

        val entity: HttpEntity<Void> = HttpEntity(getHeaders())

        return template.exchange(
            url,
            HttpMethod.GET,
            entity,
            EnduserAgreementPaginationResponse::class.java
        ).body
            .results
    }

    fun get(id: UUID): EndUserAgreementResponse {
        val url = UriComponentsBuilder.fromUriString(baseUrl)
            .pathSegment(id.toString())
            .toUriString()

        val entity: HttpEntity<Void> = HttpEntity(getHeaders())

        return template.exchange(url, HttpMethod.GET, entity, EndUserAgreementResponse::class.java).body
    }

    fun delete(id: UUID) {
        val url = UriComponentsBuilder.fromUriString(baseUrl)
            .pathSegment(id.toString())
            .toUriString()

        val entity: HttpEntity<Void> = HttpEntity(getHeaders())
        val response = template.exchange(url, HttpMethod.DELETE, entity, String::class.java)

        println(response?.body)
    }

    fun acceptAgreement(id: UUID): EndUserAgreementResponse {
        TODO()
    }

    private fun getHeaders(): HttpHeaders {
        val headers = HttpHeaders()
        headers.set("accept", "application/json")
        headers.set("Content-Type", "application/json")
        headers.set("Authorization", "Bearer ${accessToken.getToken()}")

        return headers
    }

    private data class EnduserAgreementPaginationResponse(
        @JsonProperty("count") val count: Int,
        @JsonProperty("next") val next: String?,
        @JsonProperty("previous") val previous: String?,
        @JsonProperty("results") val results: List<EndUserAgreementResponse>
    )

    private data class CreateEnduserAgreementRequest(
        val institution_id: String,
        val max_historical_days: Int,
        val access_valid_for_days: Int,
        val access_scope: List<String>
    )

    private data class GetAllEnduserAgreementsRequest(
        val limit: Int,
        val offset: Int
    )


}
