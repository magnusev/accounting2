package com.evensberget.accounting.common.domain

import java.time.LocalDate

data class RawTransaction(
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
    val exchangeRate: String,
    val instructedAmount: CurrencyAmount,
    val sourceCurrency: String,
    val targetCurrency: String,
    val unitCurrency: String
)
