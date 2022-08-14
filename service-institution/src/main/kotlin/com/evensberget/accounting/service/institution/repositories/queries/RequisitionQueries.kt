package com.evensberget.accounting.service.institution.repositories.queries

import com.evensberget.accounting.common.database.DbUtils
import com.evensberget.accounting.common.database.getList
import com.evensberget.accounting.common.database.getLocalDateTime
import com.evensberget.accounting.common.database.getUUID
import com.evensberget.accounting.common.domain.Requisition
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.util.*

class RequisitionQueries(
    private val template: NamedParameterJdbcTemplate
) {

    private val rowMapper = RowMapper { rs, _ ->
        Requisition(
            id = rs.getUUID("external_id"),
            institutionId = rs.getUUID("institution_id"),
            agreementId = rs.getUUID("agreement_id"),
            redirect = rs.getString("redirect"),
            status = rs.getString("status"),
            createdAt = rs.getLocalDateTime("created_at"),
            reference = rs.getString("reference"),
            accounts = rs.getList("accounts"),
            userLanguage = rs.getString("user_language"),
            link = rs.getString("link"),
            ssn = rs.getString("ssn"),
            accountSelection = rs.getBoolean("account_selection"),
            redirectImmediate = rs.getBoolean("redirect_immediate")
        )
    }

    private val baseQuery = """
        SELECT requisistion.id                 AS id,
               requisistion.external_id        AS external_id,
               requisistion.nordigen_id        AS nordigen_id,
               requisistion.redirect           AS redirect,
               requisistion.status             AS status,
               institution.external_id         AS institution_id,
               requisistion.created_at         AS created_at,
               enduser_agreement.external_id   AS agreement_id,
               requisistion.reference          AS reference,
               requisistion.user_language      AS user_language,
               requisistion.link               AS link,
               requisistion.ssn                AS ssn,
               requisistion.account_selection  AS account_selection,
               requisistion.redirect_immediate AS redirect_immediate,
               requisistion.accounts           AS accounts
        FROM requisistion
                 INNER JOIN enduser_agreement on enduser_agreement.id = requisistion.agreement_id
                 INNER JOIN institution on institution.id = requisistion.institution_id
    """.trimIndent()

    fun getByNordigenId(nordigenId: UUID): Requisition {
        val sql = """$baseQuery WHERE requisistion.nordigen_id = :nordigenId """

        return template.query(sql, DbUtils.sqlParameters("nordigenId" to nordigenId), rowMapper).first()
    }

    fun getByExternalId(id: UUID): Requisition {
        val sql = """$baseQuery WHERE requisistion.external_id = :id """

        return template.query(sql, DbUtils.sqlParameters("id" to id), rowMapper).first()
    }
}
