package com.evensberget.accounting.connector.nordigen.components

import com.evensberget.accounting.connector.nordigen.domain.NordigenAccount
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import java.time.LocalDateTime
import java.util.*

@Component
class NordigenAccountsComponent(
    private val accessToken: NordigenAccesTokenComponent,
    private val template: RestTemplate
) {

    private val baseUrl = "https://ob.nordigen.com/api/v2/accounts/"

    fun getAccount(id: UUID): NordigenAccount {
        val basic = getAccountBasic(id)
        val details = getAccountDetails(id)

        return NordigenAccount(
            id = basic.id,
            created = basic.created,
            lastAccessed = basic.lastAccessed,
            institutionId = basic.institutionId,
            status = basic.status,
            owner = basic.ownerName,
            resourceId = details.resourceId,
            iban = details.iban,
            bban = details.bban,
            currency = details.currency,
            name = details.name,
            product = details.product,
            cashAccountType = details.cashAccountType
        )
    }

    private fun getAccountBasic(id: UUID): AccountResponse {
        val url = UriComponentsBuilder.fromUriString(baseUrl)
            .pathSegment(id.toString())
            .toUriString()

        val entity: HttpEntity<Void> = HttpEntity(getHeaders())

        return template.exchange(url, HttpMethod.GET, entity, AccountResponse::class.java).body
    }

    private fun getAccountDetails(id: UUID): DetailsResponse {
        val url = UriComponentsBuilder.fromUriString(baseUrl)
            .pathSegment(id.toString(), "details")
            .toUriString()

        val entity: HttpEntity<Void> = HttpEntity(getHeaders())

        return template.exchange(url, HttpMethod.GET, entity, DetailsResponseWrapper::class.java)
            .body.account
    }

    private fun getHeaders(): HttpHeaders {
        val headers = HttpHeaders()
        headers.set("accept", "application/json")
        headers.set("Content-Type", "application/json")
        headers.set("Authorization", "Bearer ${accessToken.getToken()}")

        return headers
    }

    private data class AccountResponse(
        @JsonProperty("id") val id: UUID,
        @JsonProperty("created") val created: LocalDateTime,
        @JsonProperty("last_accessed") val lastAccessed: LocalDateTime,
        @JsonProperty("iban") val iban: String,
        @JsonProperty("institution_id") val institutionId: String,
        @JsonProperty("status") val status: String,
        @JsonProperty("owner_name") val ownerName: String,
    )

    private data class DetailsResponseWrapper(
        val account: DetailsResponse
    )

    private data class DetailsResponse(
        @JsonProperty("resourceId") val resourceId: String,
        @JsonProperty("iban") val iban: String,
        @JsonProperty("bban") val bban: String,
        @JsonProperty("currency") val currency: String,
        @JsonProperty("ownerName") val ownerName: String,
        @JsonProperty("name") val name: String,
        @JsonProperty("product") val product: String?,
        @JsonProperty("cashAccountType") val cashAccountType: String?,
    )
}
