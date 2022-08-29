package com.evensberget.accounting.service.institution.repositories

import com.evensberget.accounting.common.database.DbUtils
import com.evensberget.accounting.common.database.getUUID
import com.evensberget.accounting.common.domain.Requisition
import com.evensberget.accounting.connector.nordigen.domain.NordigenRequisition
import com.evensberget.accounting.service.institution.repositories.queries.RequisitionQueries
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component
import java.sql.Array
import java.util.*

@Component
class RequisitionRepository(
    private val template: NamedParameterJdbcTemplate
) {

    private val upsertSql = """
        INSERT INTO requisistion(external_id, nordigen_id, redirect,
                                 status, institution_id, created_at,
                                 agreement_id, reference, user_language,
                                 link, ssn, account_selection,
                                 redirect_immediate, accounts)
        VALUES (:externalId,
                :nordigenId,
                :redirect,
                :status,
                (SELECT id FROM institution WHERE external_id = :institutionId),
                :createdAt,
                (SELECT id FROM enduser_agreement WHERE external_id = :agreementId),
                :reference,
                :userLanguage,
                :link,
                :ssn,
                :accountSelection,
                :redirectImmediate,
                :accounts)
        ON CONFLICT (nordigen_id) DO UPDATE SET redirect           = :redirect,
                                                status             = :status,
                                                institution_id     = (SELECT id FROM institution WHERE external_id = :institutionId),
                                                created_at         = :createdAt,
                                                agreement_id       = (SELECT id FROM enduser_agreement WHERE external_id = :agreementId),
                                                reference          = :reference,
                                                user_language      = :userLanguage,
                                                link               = :link,
                                                ssn                = :ssn,
                                                account_selection  = :accountSelection,
                                                redirect_immediate = :redirectImmediate,
                                                accounts           = :accounts,
                                                modified           = current_timestamp
    """.trimIndent()

    fun upsertRequisition(
        requisition: NordigenRequisition,
        institutionId: UUID,
        agreementId: UUID
    ): Requisition {
        val parameters = DbUtils.sqlParameters(
            "externalId" to UUID.randomUUID(),
            "nordigenId" to requisition.id,
            "redirect" to requisition.redirect,
            "status" to requisition.status,
            "institutionId" to institutionId,
            "createdAt" to requisition.created,
            "agreementId" to agreementId,
            "reference" to requisition.reference,
            "userLanguage" to requisition.userLanguagage,
            "link" to requisition.link,
            "ssn" to requisition.ssn,
            "accountSelection" to requisition.accountSelection,
            "redirectImmediate" to requisition.redirectImmediate,
            "accounts" to toSqlArray(requisition.accounts)
        )

        template.update(upsertSql, parameters)
        return getByNordigenId(requisition.id)
    }

    fun get(id: UUID): Requisition {
        return RequisitionQueries(template).getByExternalId(id)
    }

    private fun getByNordigenId(nordigenId: UUID): Requisition {
        return RequisitionQueries(template).getByNordigenId(nordigenId)
    }

    private fun toSqlArray(items: List<String>): Array {
        return template.jdbcTemplate.dataSource.connection.createArrayOf("VARCHAR", items.toTypedArray())
    }

    fun getNordigenId(id: UUID): UUID {
        return template.query(
            "SELECT nordigen_id FROM requisistion WHERE external_id = :id",
            DbUtils.sqlParameters("id" to id)
        ) { rs, _ -> rs.getUUID("nordigen_id") }
            .first()

    }


}
