package com.evensberget.accounting.service.rule

import com.evensberget.accounting.common.domain.CurrencyAmount
import com.evensberget.accounting.common.domain.RawTransaction
import com.evensberget.accounting.common.domain.Transaction
import com.evensberget.accounting.common.domain.rule.Rule
import com.evensberget.accounting.common.domain.rule.RuleField
import com.evensberget.accounting.service.rule.engine.RuleEngine
import org.springframework.stereotype.Service
import java.util.*

@Service
class RuleService(
    private val ruleRepository: RuleRepository,
    private val idRepository: NordigenTransactionTranslationRepository,
    private val ruleEngine: RuleEngine
) {

    fun applyRule(userId: UUID, rawTransaction: RawTransaction): Transaction {
        val rules = ruleRepository.getRulesForUser(userId)
        val bestRule = ruleEngine.bestValidRule(rawTransaction, rules)

        return if (bestRule != null) {
            toTransaction(rawTransaction, bestRule)
        } else {
            toDefaultTransaction(rawTransaction)
        }
    }

    private fun toTransaction(rawTransaction: RawTransaction, rule: Rule): Transaction {
        return Transaction(
            id = idRepository.getIdForNordigenId(rawTransaction.transactionId),
            date = rawTransaction.bookingDate,
            description = stringField("description", rule.fields, rawTransaction.remittanceInformationUnstructured),
            category = rule.category,
            subCategory = rule.subCategory,
            amount = CurrencyAmount(
                rawTransaction.transactionAmount.amount,
                rawTransaction.transactionAmount.currency
            ),
            details = stringField("details", rule.fields, rawTransaction.remittanceInformationUnstructured),
            tags = rule.tags.toSet(),
            ignored = rule.ignored,
            status = rawTransaction.status,
            accountId = rawTransaction.accountId,
            type = rawTransaction.type(),
            source = rawTransaction.source(),
            rawTransactionId = rawTransaction.id
        )
    }

    private fun stringField(fieldName: String, ruleFields: List<RuleField>, default: String): String {
        return when (fieldName) {
            "description" -> ruleFields.find { it.transactionFieldName == "description" }?.fieldValue ?: default
            "details" -> ruleFields.find { it.transactionFieldName == "details" }?.fieldValue ?: default
            else -> throw UnsupportedOperationException("Field with name $fieldName is not supported")
        }
    }

    private fun toDefaultTransaction(rawTransaction: RawTransaction): Transaction {
        return Transaction(
            id = idRepository.getIdForNordigenId(rawTransaction.transactionId),
            date = rawTransaction.bookingDate,
            description = rawTransaction.getDescription(),
            amount = CurrencyAmount(
                rawTransaction.transactionAmount.amount,
                rawTransaction.transactionAmount.currency
            ),
            details = rawTransaction.remittanceInformationUnstructured,
            status = rawTransaction.status,
            accountId = rawTransaction.accountId,
            type = rawTransaction.type(),
            source = rawTransaction.source(),
            rawTransactionId = rawTransaction.id
        )
    }


}
