package com.evensberget.accounting.connector.nordigen.dto

data class EndUserAgreementRequest(
    val institution_id: String,
    val max_historical_days: Int,
    val access_valid_for_days: Int,
    val access_scope: List<String>
)
