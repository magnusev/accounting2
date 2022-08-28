package com.evensberget.accounting.connector.nordigen.dto

import com.evensberget.accounting.common.domain.*
import com.evensberget.accounting.connector.nordigen.domain.NordigenRawTransaction
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate
import java.util.*

data class TransactionsResponse(
    @JsonProperty("transactions") val transactions: TransactionsInner
)

data class TransactionsInner(
    @JsonProperty("booked") val booked: List<NordigenTransactionResponse>,
    @JsonProperty("pending") val pending: List<NordigenTransactionResponse>
)

data class NordigenTransactionResponse(
    @JsonProperty("additionalInformation") val additionalInformation: String,
    @JsonProperty("bookingDate") val bookingDate: LocalDate,
    @JsonProperty("creditorName") val creditorName: String?,
    @JsonProperty("debtorName") val debtorName: String?,
    @JsonProperty("debtorAccount") val debtorAccount: Map<String, String>?,
    @JsonProperty("creditorAccount") val creditorAccount: Map<String, String>?,
    @JsonProperty("currencyExchange") val currencyExchange: NordigenCurrencyExchange?,
    @JsonProperty("entryReference") val entryReference: String,
    @JsonProperty("remittanceInformationUnstructured") val remittanceInformationUnstructured: String,
    @JsonProperty("transactionAmount") val transactionAmount: NordigenTransactionAmount,
    @JsonProperty("transactionId") val transactionId: String,
    @JsonProperty("valueDate") val valueDate: LocalDate
) {

    fun toTransaction(status: TransactionStatus): Transaction {
        return Transaction(
            id = UUID.randomUUID(),
            date = bookingDate,
            description = getDescription().removeIllegalCharacters(),
            amount = CurrencyAmount(
                amount = transactionAmount.amount,
                currency = transactionAmount.currency
            ),
            details = remittanceInformationUnstructured.removeIllegalCharacters(),
            account = -1,
            status = status,
            type = type(),
            source = source(),
            rawTransaction = null
        )
    }

    private fun getDescription(): String {
        return if (creditorName != null) creditorName
        else if (debtorName != null) debtorName
        else additionalInformation.replace("  ", " ")
    }

    private fun isCredit(): Boolean {
        return creditorName != null || (creditorAccount != null && creditorAccount.isNotEmpty())
    }

    private fun isDebitor(): Boolean {
        return debtorName != null || (debtorAccount != null && debtorAccount.isNotEmpty())
    }

    private fun type(): TransactionType {
        return if (isCredit()) TransactionType.CREDIT
        else if (isDebitor()) TransactionType.DEBIT
        else if (transactionAmount.amount.toDouble() < 0) TransactionType.CREDIT
        else if (transactionAmount.amount.toDouble() > 0) TransactionType.DEBIT
        else throw IllegalStateException("Should be either Credit or Debit")
    }


    private fun source(): String {
        if (isCredit()) {
            if (creditorName != null) return creditorName
            if (creditorAccount != null && creditorAccount.isNotEmpty()) return creditorAccount.entries.first().value
        } else if (isDebitor()) {
            if (debtorName != null) return debtorName
            if (debtorAccount != null && debtorAccount.isNotEmpty()) return debtorAccount.entries.first().value
        }

        return entryReference
    }

    fun toRawTransaction(status: TransactionStatus): NordigenRawTransaction {
        return NordigenRawTransaction(
            status = status,
            additionalInformation = additionalInformation.removeIllegalCharacters(),
            bookingDate = bookingDate,
            creditorName = creditorName,
            debtorName = debtorName,
            debtorAccount = debtorAccount,
            creditorAccount = creditorAccount,
            currencyExchange = currencyExchange?.currencyExchange(),
            entryReference = entryReference,
            remittanceInformationUnstructured = remittanceInformationUnstructured.removeIllegalCharacters(),
            transactionAmount = transactionAmount.currencyAmount(),
            transactionId = transactionId,
            valueDate = valueDate
        )
    }

    private fun String.removeIllegalCharacters(): String {
        return this
            .replace("\n", " ")
            .replace("\\s+".toRegex(), " ")
    }


}

data class NordigenCurrencyExchange(
    @JsonProperty("exchangeRate") val exchangeRate: Double,
    @JsonProperty("instructedAmount") val instructedAmount: NordigenTransactionAmount,
    @JsonProperty("sourceCurrency") val sourceCurrency: String,
    @JsonProperty("targetCurrency") val targetCurrency: String,
    @JsonProperty("unitCurrency") val unitCurrency: String
) {

    fun currencyExchange(): CurrencyExchange {
        return CurrencyExchange(
            exchangeRate = exchangeRate,
            instructedAmount = instructedAmount.currencyAmount(),
            sourceCurrency = sourceCurrency,
            targetCurrency = targetCurrency,
            unitCurrency = unitCurrency
        )
    }
}


data class NordigenTransactionAmount(
    @JsonProperty("amount") val amount: Double,
    @JsonProperty("currency") val currency: String
) {

    fun currencyAmount(): CurrencyAmount = CurrencyAmount(amount, currency)


}
