package com.evensberget.accounting.connector.nordigen.dto

import com.evensberget.accounting.connector.nordigen.domain.NordigenInstitution
import com.fasterxml.jackson.annotation.JsonProperty

data class InstitutionsResponse(

    @JsonProperty("id")
    val id: String,

    @JsonProperty("name")
    val name: String,

    @JsonProperty("bic")
    val bic: String,

    @JsonProperty("transaction_total_days")
    val transactionTotalDays: String,

    @JsonProperty("countries")
    val countries: Set<String>,

    @JsonProperty("logo")
    val logo: String

) {

    fun toNordigenInstitution(): NordigenInstitution {
        return NordigenInstitution(
            id = id,
            name = name,
            bic = bic,
            transactionTotalDays = transactionTotalDays,
            countries = countries,
            logo = logo
        )
    }

}
