package com.evensberget.accounting.common.domain

import java.time.LocalDate
import java.util.*

data class Balance(
    val accountId: UUID,
    val amount: Double,
    val currency: String,
    val type: String,
    val referenceDate: LocalDate
)
