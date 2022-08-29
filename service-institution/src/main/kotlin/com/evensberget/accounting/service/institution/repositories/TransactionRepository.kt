package com.evensberget.accounting.service.institution.repositories

import com.evensberget.accounting.common.database.DbUtils
import com.evensberget.accounting.common.domain.Transaction
import com.evensberget.accounting.common.json.JsonUtils
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component

@Component
class TransactionRepository(
    private val template: NamedParameterJdbcTemplate
) {

    private val upsertSql = """
        INSERT INTO transaction(external_id, account_id, raw_transaction_id,
                                date, description, category,
                                sub_category, amount, currency,
                                details, tags, notes,
                                ignored, status, type,
                                source)
        VALUES (:externalId,
                (SELECT id FROM account WHERE external_id = :accountId),
                (SELECT id FROM raw_transaction WHERE external_id = :rawTransactionId),
                :date,
                :description,
                :category,
                :subCategory,
                :amount,
                :currency,
                :details,
                :tags::jsonb,
                :notes,
                :ignored,
                :status,
                :type,
                :source)
        ON CONFLICT (external_id) DO UPDATE SET date         = :date,
                                                description  = :description,
                                                category     = :category,
                                                sub_category = :subCategory,
                                                amount       = :amount,
                                                currency     = :currency,
                                                details      = :details,
                                                tags         = :tags::jsonb,
                                                notes        = :notes,
                                                ignored      = :ignored,
                                                status       = :status,
                                                type         = :type,
                                                source       = :source,
                                                modified     = current_timestamp
    """.trimIndent()

    fun upsertTransactions(transactions: List<Transaction>) {
        val params = transactions.map { transaction ->
            DbUtils.sqlParameters(
                "externalId" to transaction.id,
                "accountId" to transaction.accountId,
                "rawTransactionId" to transaction.rawTransactionId,
                "date" to transaction.date,
                "description" to transaction.description,
                "category" to transaction.category,
                "subCategory" to transaction.subCategory,
                "amount" to transaction.amount.amount,
                "currency" to transaction.amount.currency,
                "details" to transaction.details,
                "tags" to JsonUtils.toJson(transaction.tags.toList()),
                "notes" to transaction.notes,
                "ignored" to transaction.ignored,
                "status" to transaction.status.name,
                "type" to transaction.type.name,
                "source" to transaction.source
            )
        }.toTypedArray()

        template.batchUpdate(upsertSql, params)
    }
}
