package com.evensberget.accounting.connector.nordigen.domain

import java.time.LocalDateTime
import java.util.*

data class NordigenRequisition(
    val id: UUID,
    val created: LocalDateTime,
    val redirect: String,
    val status: String,
    val institutionId: String,
    val agreement: UUID,
    val reference: String,
    val accounts: List<String>,
    val userLanguagage: String,
    val link: String,
    val ssn: String?,
    val accountSelection: Boolean,
    val redirectImmediate: Boolean
)
