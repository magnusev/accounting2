package com.evensberget.accounting.connector.nordigen.domain

import java.time.LocalDateTime
import java.util.*

data class NordigenAccount(
    val id: UUID,
    val created: LocalDateTime,
    val lastAccessed: LocalDateTime,
    val institutionId: String,
    val status: String,
    val owner: String,
    val resourceId: String,
    val iban: String,
    val bban: String,
    val currency: String,
    val name: String,
    val product: String?,
    val cashAccountType: String?
)
