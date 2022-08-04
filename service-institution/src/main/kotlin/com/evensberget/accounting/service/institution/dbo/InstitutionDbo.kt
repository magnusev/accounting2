package com.evensberget.accounting.service.institution.dbo

import java.util.*

data class InstitutionDbo(
    val id: Int,
    val externalId: UUID,
    val nordigenId: String,
    val bic: String,
    val transactionTotalDays: String,
    val logo: String
)
