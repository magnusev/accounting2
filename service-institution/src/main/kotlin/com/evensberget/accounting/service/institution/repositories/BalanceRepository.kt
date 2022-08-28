package com.evensberget.accounting.service.institution.repositories

import com.evensberget.accounting.common.database.DbUtils
import com.evensberget.accounting.common.database.getLocalDate
import com.evensberget.accounting.common.domain.Balance
import com.evensberget.accounting.connector.nordigen.domain.NordigenBalance
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component
import java.util.*

@Component
class BalanceRepository(
    private val template: NamedParameterJdbcTemplate
) {

    private val upsertSql = """
        INSERT INTO balance(account_id, amount, currency, balance_type, reference_date)
        VALUES ((SELECT id FROM account WHERE external_id = :accountId),
                :amount,
                :currency,
                :balanceType,
                :referenceDate)
        ON CONFLICT (account_id, balance_type) DO UPDATE SET amount         = :amount,
                                                             currency       = :currency,
                                                             reference_date = :referenceDate
    """.trimIndent()

    fun upsertBalances(accountId: UUID, balances: List<NordigenBalance>): List<Balance> {
        val params = balances.map { balance ->
            DbUtils.sqlParameters(
                "accountId" to accountId,
                "amount" to balance.balanceAmount.amount.toDouble(),
                "currency" to balance.balanceAmount.currency,
                "balanceType" to balance.balanceType,
                "referenceDate" to balance.referenceDate
            )
        }.toTypedArray()

        template.batchUpdate(upsertSql, params)

        return getBalancesForAccount(accountId)
    }

    fun getBalancesForAccount(accountId: UUID): List<Balance> {
        val sql = """
            SELECT amount,
                   balance.currency,
                   balance_type,
                   reference_date
            FROM balance
                     INNER JOIN account on account.id = balance.account_id
            WHERE account.external_id = :accountId
        """.trimIndent()

        return template.query(sql, DbUtils.sqlParameters("accountId" to accountId)) { rs, _ ->
            Balance(
                accountId = accountId,
                amount = rs.getDouble("amount"),
                currency = rs.getString("currency"),
                type = rs.getString("balance_type"),
                referenceDate = rs.getLocalDate("reference_date")
            )
        }
    }
}
