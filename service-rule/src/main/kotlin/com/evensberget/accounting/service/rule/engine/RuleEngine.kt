package com.evensberget.accounting.service.rule.engine

import com.evensberget.accounting.common.domain.RawTransaction
import com.evensberget.accounting.common.domain.rule.Rule
import com.evensberget.accounting.common.domain.rule.RuleEntry
import com.evensberget.accounting.common.domain.rule.RuleOperation
import org.springframework.stereotype.Component

@Component
class RuleEngine {

    fun bestValidRule(transaction: RawTransaction, allRules: List<Rule>): Rule? {
        val data = validRules(transaction, allRules).firstOrNull()

        if(data == null || !data.allValid) {
            return null
        }

        return data?.rule
    }

    private fun validRules(transaction: RawTransaction, allRules: List<Rule>): List<RuleResultWrapper> {
        return allRules.map { isValid(transaction, it) }
            .sortedByDescending { it.nrRuleEntriesValid }
    }

    private fun isValid(transaction: RawTransaction, rule: Rule): RuleResultWrapper {
        val checkedEntries = mutableMapOf<RuleEntry, Boolean>()

        rule.rules.forEach { entry ->
            when (entry.rawTransactionFieldName) {
                "creditor_name" -> {
                    checkedEntries[entry] = stringCompare(entry.operation, transaction.creditorName, entry.fieldValue)
                }
                "remittanceInformationUnstructured" -> {
                    checkedEntries[entry] = stringCompare(entry.operation, transaction.remittanceInformationUnstructured, entry.fieldValue)
                }
                else -> throw UnsupportedOperationException("Field with name ${entry.rawTransactionFieldName} is not supported")
            }
        }

        return RuleResultWrapper(
            rule = rule,
            allValid = checkedEntries.filter { !it.value }.isEmpty(),
            nrRuleEntriesValid = checkedEntries.filter { it.value }.count()
        )
    }

    private fun stringCompare(operation: RuleOperation, fieldValue: String?, shouldBe: String): Boolean {
        if (fieldValue == null) return false

        return when (operation) {
            RuleOperation.EQUALS -> fieldValue == shouldBe
            RuleOperation.CONTAINS -> fieldValue.contains(shouldBe, true)
        }

    }

    private fun getfieldValue(fieldName: String, transaction: RawTransaction): String? {
        return when (fieldName) {
            "creditor_name" -> transaction.creditorName
            else -> throw UnsupportedOperationException("Field with name $fieldName is not supported")
        }
    }

    data class RuleResultWrapper(
        val rule: Rule,
        val allValid: Boolean,
        val nrRuleEntriesValid: Int
    )

}
