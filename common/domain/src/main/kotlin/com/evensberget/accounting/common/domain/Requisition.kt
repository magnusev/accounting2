package com.evensberget.accounting.common.domain

import java.time.LocalDateTime
import java.util.*

data class Requisition(
    val id: UUID,
    val institutionId: UUID,
    val agreementId: UUID,
    val redirect: String,
    val status: String,
    val createdAt: LocalDateTime,
    val reference: String,
    val accounts: List<String>,
    val userLanguage: String,
    val link: String,
    val ssn: String?,
    val accountSelection: Boolean,
    val redirectImmediate: Boolean
)
