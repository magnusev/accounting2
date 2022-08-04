package com.evensberget.accounting.common.domain

import java.util.*

data class Institution(
    val id: UUID,
    val name: String,
    val bic: String,
    val transactionTotalDays: Int,
    val logo: String,
    val countries: List<Country>
)
