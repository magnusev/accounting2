package com.evensberget.accounting.service.institution.repositories

import com.evensberget.accounting.common.database.DbUtils
import com.evensberget.accounting.common.database.getLocalDate
import com.evensberget.accounting.common.database.getNullableLocalDate
import com.evensberget.accounting.common.database.getUUID
import com.evensberget.accounting.common.domain.CurrencyAmount
import com.evensberget.accounting.common.domain.CurrencyExchange
import com.evensberget.accounting.common.domain.RawTransaction
import com.evensberget.accounting.common.domain.TransactionStatus
import com.evensberget.accounting.common.json.JsonUtils
import com.evensberget.accounting.connector.nordigen.domain.NordigenRawTransaction
import com.fasterxml.jackson.core.type.TypeReference
import org.slf4j.LoggerFactory
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component
import java.sql.ResultSet
import java.time.LocalDate
import java.util.*


@Component
class RawTransactionRepository(
    private val template: NamedParameterJdbcTemplate
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    private val rowMapper = RowMapper { rs, _ ->
        RawTransaction(
            id = rs.getUUID("external_id"),
            accountId = rs.getUUID("external_account_id"),
            status = TransactionStatus.valueOf(rs.getString("status")),
            additionalInformation = rs.getString("additional_information"),
            bookingDate = rs.getLocalDate("booking_date"),
            creditorName = rs.getString("creditor_name"),
            debtorName = rs.getString("debitor_name"),
            creditorAccount = rs.getStringMap("creditor_account"),
            debtorAccount = rs.getStringMap("debitor_account"),
            currencyExchange = rs.getCurrencyExchange(),
            entryReference = rs.getString("entry_reference"),
            remittanceInformationUnstructured = rs.getString("remittance_information_unstructured"),
            transactionAmount = CurrencyAmount(
                amount = rs.getDouble("transaction_amount"),
                currency = rs.getString("transaction_currency")
            ),
            transactionId = rs.getString("transaction_id"),
            valueDate = rs.getLocalDate("value_date")
        )
    }

    private val upsertSql = """
        INSERT INTO raw_transaction(external_id, account_id, status,
                                    additional_information, booking_date, creditor_name,
                                    debitor_name, creditor_account, debitor_account,
                                    exchange_rate, instructed_amount, instructed_currency,
                                    source_currency, target_currency, unit_currency,
                                    entry_reference, remittance_information_unstructured,
                                    transaction_amount, transaction_currency,
                                    transaction_id, value_date)
        VALUES (:externalId,
                (SELECT id FROM account where external_id = :accountId),
                :status,
                :additionalInformation,
                :bookingDate,
                :creditorName,
                :debitorName,
                :creditorAccount::jsonb,
                :debitorAccount::jsonb,
                :exchangeRate,
                :instructedAmount,
                :instructedCurrency,
                :sourceCurrency,
                :targetCurrency,
                :unitCurrency,
                :entryReference,
                :remittanceInformationUnstructured,
                :transactionAmount,
                :transactionCurrency,
                :transactionId,
                :valueDate)
        ON CONFLICT (transaction_id) DO UPDATE SET status                              = :status,
                                                   additional_information              = :additionalInformation,
                                                   booking_date                        = :bookingDate,
                                                   creditor_name                       = :creditorName,
                                                   debitor_name                        = :debitorName,
                                                   creditor_account                    = :creditorAccount::jsonb,
                                                   debitor_account                     = :debitorAccount::jsonb,
                                                   exchange_rate                       = :exchangeRate,
                                                   instructed_amount                   = :instructedAmount,
                                                   instructed_currency                 = :instructedCurrency,
                                                   source_currency                     = :sourceCurrency,
                                                   target_currency                     = :targetCurrency,
                                                   unit_currency                       = :unitCurrency,
                                                   entry_reference                     = :entryReference,
                                                   remittance_information_unstructured = :remittanceInformationUnstructured,
                                                   transaction_amount                  = :transactionAmount,
                                                   transaction_currency                = :transactionCurrency,
                                                   value_date                          = :valueDate
    """.trimIndent()

    fun upsertTransactions(accountId: UUID, transactions: List<NordigenRawTransaction>) {
        val params = transactions.map { transaction ->
            DbUtils.sqlParameters(
                "externalId" to UUID.randomUUID(),
                "accountId" to accountId,
                "status" to transaction.status.name,
                "additionalInformation" to transaction.additionalInformation,
                "bookingDate" to transaction.bookingDate,
                "creditorName" to transaction.creditorName,
                "debitorName" to transaction.debtorName,
                "creditorAccount" to if (transaction.creditorAccount != null) JsonUtils.toJson(transaction.creditorAccount!!) else null,
                "debitorAccount" to if (transaction.debtorAccount != null) JsonUtils.toJson(transaction.debtorAccount!!) else null,
                "exchangeRate" to transaction.currencyExchange?.exchangeRate,
                "instructedAmount" to transaction.currencyExchange?.instructedAmount?.amount,
                "instructedCurrency" to transaction.currencyExchange?.instructedAmount?.currency,
                "sourceCurrency" to transaction.currencyExchange?.sourceCurrency,
                "targetCurrency" to transaction.currencyExchange?.targetCurrency,
                "unitCurrency" to transaction.currencyExchange?.unitCurrency,
                "entryReference" to transaction.entryReference,
                "remittanceInformationUnstructured" to transaction.remittanceInformationUnstructured,
                "transactionAmount" to transaction.transactionAmount.amount,
                "transactionCurrency" to transaction.transactionAmount.currency,
                "transactionId" to transaction.transactionId,
                "valueDate" to transaction.valueDate
            )
        }.toTypedArray()

        template.batchUpdate(upsertSql, params)
    }

    fun getRawTransactionsForUser(userId: UUID): List<RawTransaction> {
        val sql = """
            SELECT *,
                   (SELECT account.external_id FROM account WHERE id = raw_transaction.account_id) as external_account_id
            FROM raw_transaction
            WHERE account_id IN (SELECT account.id
                                 FROM account
                                          INNER JOIN user_account on user_account.id = account.user_id
                                 WHERE user_account.external_id = :userId)
        """.trimIndent()


        return template.query(sql, DbUtils.sqlParameters("userId" to userId), rowMapper)
    }

    fun getLatestBookingDate(accountId: UUID): LocalDate? {
        val sql = """
            SELECT max(booking_date) AS booking_date
            FROM raw_transaction
            WHERE account_id = (SELECT id FROM account WHERE external_id = :accountId)
        """.trimIndent()

        return template.query(
            sql,
            DbUtils.sqlParameters("accountId" to accountId)
        ) { rs, _ -> rs.getNullableLocalDate("booking_date") }
            .firstOrNull()
    }


    private fun ResultSet.getCurrencyExchange(): CurrencyExchange? {
        val exchangeRate = getDouble("exchange_rate") ?: return null

        if(exchangeRate == null || exchangeRate == 0.0) return null

        return CurrencyExchange(
            exchangeRate = exchangeRate,
            instructedAmount = CurrencyAmount(
                amount = getDouble("instructed_amount"),
                currency = getString("instructed_currency"),
            ),
            sourceCurrency = getString("source_currency"),
            targetCurrency = getString("target_currency"),
            unitCurrency = getString("unit_currency")
        )
    }

    private fun ResultSet.getStringMap(columnLabel: String): Map<String, String>? {
        val string = getString(columnLabel) ?: return null

        val typeRef: TypeReference<HashMap<String, String>> = object : TypeReference<HashMap<String, String>>() {}

        return JsonUtils.objectMapper().readValue(string, typeRef)
    }

}
