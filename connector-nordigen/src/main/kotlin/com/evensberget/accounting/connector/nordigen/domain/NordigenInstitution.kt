package com.evensberget.accounting.connector.nordigen.domain

data class NordigenInstitution(
    val id: String,
    val name: String,
    val bic: String,
    val transactionTotalDays: String,
    val countries: Set<String>,
    val logo: String
)
