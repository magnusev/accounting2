package com.evensberget.accounting.connector.nordigen.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class AccountsResponse (
    @JsonProperty("id") val id: String,
    @JsonProperty("created") val created: String,
    @JsonProperty("redirect") val redirect: String,
    @JsonProperty("status") val status: String,
    @JsonProperty("institution_id") val institutionId: String,
    @JsonProperty("agreement") val agreement: String,
    @JsonProperty("reference") val reference: String,
    @JsonProperty("accounts") val accounts: List<String>,
    @JsonProperty("user_language") val userLanguage: String,
    @JsonProperty("link") val link: String,
    @JsonProperty("ssn") val ssn: String?,
    @JsonProperty("account_selection") val accountSelection: Boolean,
    @JsonProperty("redirect_immediate") val redirectImmediate: Boolean,


        )
