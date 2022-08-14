package com.evensberget.accounting.service.institution.repositories

import com.evensberget.accounting.common.database.DbUtils
import com.evensberget.accounting.common.database.getUUID
import com.evensberget.accounting.common.domain.EnduserAgreement
import com.evensberget.accounting.connector.nordigen.dto.EndUserAgreementResponse
import com.evensberget.accounting.service.institution.repositories.queries.EnduserAgreementQuery
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component
import java.sql.Array
import java.util.*

@Component
class EnduserAgreementRepository(
    private val template: NamedParameterJdbcTemplate
) {

    private val upsertSql = """
        INSERT INTO enduser_agreement (external_id, nordigen_id, user_id,
                                       institution_id, created_at, max_historical_days,
                                       access_valid_for_days, valid_until, access_scope, accepted)
        VALUES (:externalId,
                :nordigenId,
                (SELECT id FROM user_account WHERE user_account.external_id = :userId),
                (SELECT id FROM institution WHERE institution.nordigen_id = :institutionNordigenId),
                :createdAt,
                :maxHistoricalDays,
                :accessValidForDays,
                :validUntil,
                :accessScope,
                :accepted)
    """.trimIndent()


    fun addEnduserAgreement(userId: UUID, agreement: EndUserAgreementResponse): EnduserAgreement {
        val parameters = DbUtils.sqlParameters(
            "externalId" to UUID.randomUUID(),
            "nordigenId" to agreement.id,
            "userId" to userId,
            "institutionNordigenId" to agreement.institutionId,
            "createdAt" to agreement.created,
            "maxHistoricalDays" to agreement.maxHistoricalDays,
            "accessValidForDays" to agreement.accessValidForDays,
            "validUntil" to agreement.created.plusDays(agreement.accessValidForDays.toLong()),
            "accessScope" to toSqlArray(agreement.accessScope),
            "accepted" to agreement.accepted
        )

        template.update(upsertSql, parameters)

        return EnduserAgreementQuery(template).getByNordigenId(agreement.id)
    }

    fun getEnduserAgreement(userId: UUID, institutionId: UUID): EnduserAgreement? {
        return EnduserAgreementQuery(template).getByUserIdAndInstitutionId(userId, institutionId)
    }

    fun getNordigenId(agreementId: UUID): UUID {
        return template.query(
            "SELECT nordigen_id FROM enduser_agreement WHERE external_id = :id",
            DbUtils.sqlParameters("id" to agreementId)
        ) { rs, _ -> rs.getUUID("nordigen_id") }
            .first()
    }

    private fun toSqlArray(items: List<String>): Array {
        return template.jdbcTemplate.dataSource.connection.createArrayOf("VARCHAR", items.toTypedArray())
    }

    fun getEnduserAgreementByAgreementId(userId: UUID, agreementId: UUID): EnduserAgreement {
        return EnduserAgreementQuery(template).getByUserIdAndAgreementId(userId, agreementId)
    }
}
