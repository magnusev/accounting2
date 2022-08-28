package com.evensberget.accounting.connector.nordigen.domain

import com.evensberget.accounting.common.domain.CurrencyAmount
import com.evensberget.accounting.common.domain.CurrencyExchange
import com.evensberget.accounting.common.domain.TransactionStatus
import java.time.LocalDate

data class NordigenRawTransaction(
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
