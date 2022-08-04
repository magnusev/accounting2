package com.evensberget.accounting.common.domain

import Country
import java.util.*

data class Institution(
    val id: UUID,
    val bic: String,
    val transactionTotalDays: String,
    val logo: String,
    val countries: List<Country>
)
