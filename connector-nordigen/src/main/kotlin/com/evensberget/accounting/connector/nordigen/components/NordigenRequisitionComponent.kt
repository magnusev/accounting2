package com.evensberget.accounting.connector.nordigen.components

import com.evensberget.accounting.connector.nordigen.domain.NordigenRequisition
import com.evensberget.accounting.connector.nordigen.dto.RequisitionRequest
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
class NordigenRequisitionComponent(
    private val accessToken: NordigenAccesTokenComponent,
    private val template: RestTemplate
) {

    private val baseUrl = "https://ob.nordigen.com/api/v2/requisitions/"

    fun create(ref: String, institutionId: String, agreementId: UUID): NordigenRequisition {

        val body = RequisitionRequest(
            redirect = "http://evensberget.com",
            institution_id = institutionId,
            reference = ref,
            agreement = agreementId
        )

        val entity = HttpEntity(body, getHeaders())

        return template.postForObject(baseUrl, entity, RequisitionResponse::class.java)
            .toModel()
    }

    fun get(limit: Int = 100, offset: Int = 0): List<NordigenRequisition> {
        val url = UriComponentsBuilder.fromUriString(baseUrl)
            .queryParam("limit", limit)
            .queryParam("offset", offset)
            .toUriString()

        val entity: HttpEntity<Void> = HttpEntity(getHeaders())

        val response = template.exchange(
            url,
            HttpMethod.GET,
            entity,
            RequisitionPaginationResponse::class.java
        )


        return response.body
            .results
            .map { it.toModel() }

    }

    fun get(id: UUID): NordigenRequisition {
        val url = UriComponentsBuilder.fromUriString(baseUrl)
            .pathSegment(id.toString())
            .toUriString()

        val entity: HttpEntity<Void> = HttpEntity(getHeaders())

        return template.exchange(url, HttpMethod.GET, entity, RequisitionResponse::class.java).body
            .toModel()
    }

    fun delete(id: UUID) {
        val url = UriComponentsBuilder.fromUriString(baseUrl)
            .pathSegment(id.toString())
            .toUriString()

        val entity: HttpEntity<Void> = HttpEntity(getHeaders())
        val response = template.exchange(url, HttpMethod.DELETE, entity, String::class.java)

        println(response?.body)
    }

    private fun getHeaders(): HttpHeaders {
        val headers = HttpHeaders()
        headers.set("accept", "application/json")
        headers.set("Content-Type", "application/json")
        headers.set("Authorization", "Bearer ${accessToken.getToken()}")

        return headers
    }

    private data class RequisitionPaginationResponse(
        @JsonProperty("count") val count: Int,
        @JsonProperty("next") val next: String?,
        @JsonProperty("previous") val previous: String?,
        @JsonProperty("results") val results: List<RequisitionResponse>
    )

    private data class RequisitionResponse(
        @JsonProperty("id") val id: UUID,
        @JsonProperty("created") val created: LocalDateTime,
        @JsonProperty("redirect") val redirect: String,
        @JsonProperty("status") val status: String,
        @JsonProperty("institution_id") val institutionId: String,
        @JsonProperty("agreement") val agreement: UUID,
        @JsonProperty("reference") val reference: String,
        @JsonProperty("accounts") val accounts: List<String>,
        @JsonProperty("user_language") val userLanguage: String,
        @JsonProperty("link") val link: String,
        @JsonProperty("ssn") val ssn: String?,
        @JsonProperty("account_selection") val accountSelection: Boolean,
        @JsonProperty("redirect_immediate") val redirectImmediate: Boolean,
    ) {
        fun toModel(): NordigenRequisition {
            return NordigenRequisition(
                id = id,
                created = created,
                redirect = redirect,
                status = status,
                institutionId = institutionId,
                agreement = agreement,
                reference = reference,
                accounts = accounts,
                userLanguagage = userLanguage,
                link = link,
                ssn = ssn,
                accountSelection = accountSelection,
                redirectImmediate = redirectImmediate
            )
        }
    }
}
