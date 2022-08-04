package com.evensberget.accounting.common.domain

import java.time.LocalDate
import java.util.*

data class Transaction(
    val id: UUID = UUID.randomUUID(),
    val date: LocalDate,
    val description: String,
    val category: String = "UNKNOWN",
    val subCategory: String? = null,
    val amount: CurrencyAmount,
    val details: String,
    val tags: Set<String> = emptySet(),
    val notes: String? = null,
    val account: Int,
    val ignored: Boolean = false,
    val status: TransactionStatus,
    val type: TransactionType,
    val source: String,
    val rawTransaction: RawTransaction? = null
)
