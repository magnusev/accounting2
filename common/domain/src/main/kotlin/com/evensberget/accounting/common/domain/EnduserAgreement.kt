package com.evensberget.accounting.common.domain

import java.time.LocalDateTime
import java.util.*

data class EnduserAgreement(
    val id: UUID,
    val userId: UUID,
    val institutionId: UUID,
    val createdAt: LocalDateTime,
    val maxHistoricalDays: Int,
    val accessValidForDays: Int,
    val validUntil: LocalDateTime,
    val accessScope: Set<String>,
    val accepted: String?
)
