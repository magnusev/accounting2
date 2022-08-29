package com.evensberget.accounting.common.domain.rule

data class RuleEntry(
    val rawTransactionFieldName: String,
    val operation: RuleOperation,
    val fieldValue: String
)

enum class RuleOperation {
    EQUALS,
    CONTAINS
}
