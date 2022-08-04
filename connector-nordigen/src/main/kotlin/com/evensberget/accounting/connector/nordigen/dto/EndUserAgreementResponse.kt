package com.evensberget.accounting.connector.nordigen.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class EndUserAgreementResponse(

    @JsonProperty("id")
    val id: String,

    @JsonProperty("created")
    val created: LocalDateTime,

    @JsonProperty("max_historical_days")
    val maxHistoricalDays: Int,

    @JsonProperty("access_valid_for_days")
    val accessValidForDays: Int,

    @JsonProperty("access_scope")
    val accessScope: List<String>,

    @JsonProperty("accepted")
    val accepted: String?,

    @JsonProperty("institution_id")
    val institutionId: String,

    )
