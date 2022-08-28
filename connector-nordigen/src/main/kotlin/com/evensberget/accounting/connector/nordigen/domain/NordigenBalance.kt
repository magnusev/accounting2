package com.evensberget.accounting.connector.nordigen.domain

import java.time.LocalDate

data class NordigenBalance(
    val balanceAmount: NordigenCurrencyAmount,
    val balanceType: String,
    val referenceDate: LocalDate
)
