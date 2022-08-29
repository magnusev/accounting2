package com.evensberget.accounting.common.domain

import java.time.LocalDate
import java.util.*

data class RawTransaction(
    val id: UUID,
    val accountId: UUID,
    val status: TransactionStatus,
    val additionalInformation: String,
    val bookingDate: LocalDate,
    val creditorName: String?,
    val debtorName: String?,
    val debtorAccount: Map<String, String>?,
    val creditorAccount: Map<String, String>?,
    val currencyExchange: CurrencyExchange?,
    val entryReference: String,
    val remittanceInformationUnstructured: String,
    val transactionAmount: CurrencyAmount,
    val transactionId: String,
    val valueDate: LocalDate
) {
    fun getDescription(): String {
        return if (creditorName != null) creditorName
        else if (debtorName != null) debtorName
        else additionalInformation.replace("  ", " ")
    }

    fun isCredit(): Boolean {
        return creditorName != null || (creditorAccount != null && creditorAccount.isNotEmpty())
    }

    fun isDebitor(): Boolean {
        return debtorName != null || (debtorAccount != null && debtorAccount.isNotEmpty())
    }

    fun type(): TransactionType {
        return if (isCredit()) TransactionType.CREDIT
        else if (isDebitor()) TransactionType.DEBIT
        else if (transactionAmount.amount.toDouble() < 0) TransactionType.CREDIT
        else if (transactionAmount.amount.toDouble() > 0) TransactionType.DEBIT
        else throw IllegalStateException("Should be either Credit or Debit")
    }


    fun source(): String {
        if (isCredit()) {
            if (creditorName != null) return creditorName
            if (creditorAccount != null && creditorAccount.isNotEmpty()) return creditorAccount.entries.first().value
        } else if (isDebitor()) {
            if (debtorName != null) return debtorName
            if (debtorAccount != null && debtorAccount.isNotEmpty()) return debtorAccount.entries.first().value
        }

        return entryReference
    }
}

data class CurrencyExchange(
    val exchangeRate: Double,
    val instructedAmount: CurrencyAmount,
    val sourceCurrency: String,
    val targetCurrency: String,
    val unitCurrency: String
)
