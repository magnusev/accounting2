package com.evensberget.accounting.service.institution.repositories

import com.evensberget.accounting.common.database.DbUtils
import com.evensberget.accounting.common.database.getLocalDateTime
import com.evensberget.accounting.common.database.getUUID
import com.evensberget.accounting.common.domain.Account
import com.evensberget.accounting.connector.nordigen.domain.NordigenAccount
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component
import java.util.*

@Component
class AccountRepository(
    private val template: NamedParameterJdbcTemplate
) {

    private val rowMapper = RowMapper { rs, _ ->
        Account(
            id = rs.getUUID("id"),
            userId = rs.getUUID("user_id"),
            institutionId = rs.getUUID("institution_id"),
            name = rs.getString("name"),
            created = rs.getLocalDateTime("created"),
            lastAccessed = rs.getLocalDateTime("last_accessed"),
            status = rs.getString("status"),
            owner = rs.getString("owner"),
            resourceId = rs.getString("resource_id"),
            iban = rs.getString("iban"),
            bban = rs.getString("bban"),
            currency = rs.getString("currency"),
            product = rs.getString("product"),
            cashAccountType = rs.getString("cash_account_type")
        )
    }

    private val upsertSql = """
        INSERT INTO account(external_id, nordigen_id, user_id,
                            institution_id, created, last_accessed,
                            status, owner, name, resource_id, iban, bban,
                            currency, product, cash_account_type)
        VALUES (:externalId,
                :nordigenId,
                (SELECT id FROM user_account WHERE user_account.external_id = :userId),
                (SELECT id FROM institution WHERE institution.nordigen_id = :institutionNordigenId),
                :created,
                :lastAccessed,
                :status,
                :owner,
                :name,
                :resourceId,
                :iban,
                :bban,
                :currency,
                :product,
                :cashAccountType)
        ON CONFLICT (nordigen_id) DO UPDATE SET created           = :created,
                                                last_accessed     = :lastAccessed,
                                                status            = :status,
                                                owner             = :owner,
                                                name              = :name,
                                                resource_id       = :resourceId,
                                                iban              = :iban,
                                                bban              = :bban,
                                                currency          = :currency,
                                                product           = :product,
                                                cash_account_type = :cashAccountType
    """.trimIndent()

    fun upsertAccount(userId: UUID, nordigenAccount: NordigenAccount): Account {
        val parameters = DbUtils.sqlParameters(
            "externalId" to UUID.randomUUID(),
            "nordigenId" to nordigenAccount.id,
            "userId" to userId,
            "name" to nordigenAccount.name,
            "institutionNordigenId" to nordigenAccount.institutionId,
            "created" to nordigenAccount.created,
            "lastAccessed" to nordigenAccount.lastAccessed,
            "status" to nordigenAccount.status,
            "owner" to nordigenAccount.owner,
            "resourceId" to nordigenAccount.resourceId,
            "iban" to nordigenAccount.iban,
            "bban" to nordigenAccount.bban,
            "currency" to nordigenAccount.currency,
            "product" to nordigenAccount.product,
            "cashAccountType" to nordigenAccount.cashAccountType
        )

        template.update(upsertSql, parameters)

        return getByNordigenId(nordigenAccount.id)
    }

    private fun getByNordigenId(id: UUID): Account {
        val sql = """
            SELECT account.external_id      as id,
                   user_account.external_id as user_id,
                   institution.external_id  as institution_id,
                   created,
                   last_accessed,
                   status,
                   owner,
                   account.name as name,
                   resource_id,
                   iban,
                   bban,
                   currency,
                   product,
                   cash_account_type
            FROM account
                     INNER JOIN institution on institution.id = account.institution_id
                     INNER JOIN user_account on user_account.id = account.user_id
            WHERE account.nordigen_id = :id
        """.trimIndent()

        return template.query(sql, DbUtils.sqlParameters("id" to id), rowMapper)
            .first()
    }

}
