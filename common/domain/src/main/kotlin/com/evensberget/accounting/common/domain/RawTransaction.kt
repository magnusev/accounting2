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
)

data class CurrencyExchange(
    val exchangeRate: Double,
    val instructedAmount: CurrencyAmount,
    val sourceCurrency: String,
    val targetCurrency: String,
    val unitCurrency: String
)
