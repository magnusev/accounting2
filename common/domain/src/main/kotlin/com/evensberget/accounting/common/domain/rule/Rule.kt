package com.evensberget.accounting.common.domain.rule

import java.util.*

data class Rule(
    val id: UUID = UUID.randomUUID(),
    val userId: UUID = UUID.randomUUID(),
    val rules: List<RuleEntry>,
    val fields: List<RuleField>,
    val category: String,
    val subCategory: String? = null,
    val tags: List<String> = emptyList(),
    val ignored: Boolean = false,
    val fixedExpense: Boolean = false
)
