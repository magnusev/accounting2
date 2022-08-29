package com.evensberget.accounting.service.rule.domain

import java.util.*

data class TransactionNordigenIdTranslation(
    val nordingenId: String,
    val id: UUID,
)
