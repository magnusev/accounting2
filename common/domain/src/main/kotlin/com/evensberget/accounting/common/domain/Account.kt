package com.evensberget.accounting.common.domain

import java.time.LocalDateTime
import java.util.*

data class Account(
    val id: UUID,
    val userId: UUID,
    val institutionId: UUID,
    val created: LocalDateTime,
    val lastAccessed: LocalDateTime,
    val name: String,
    val status: String,
    val owner: String,
    val resourceId: String,
    val iban: String,
    val bban: String,
    val currency: String,
    val product: String?,
    val cashAccountType: String?
)
