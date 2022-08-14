package com.evensberget.accounting.service.institution.repositories.queries

import com.evensberget.accounting.common.database.DbUtils
import com.evensberget.accounting.common.database.getLocalDateTime
import com.evensberget.accounting.common.database.getSet
import com.evensberget.accounting.common.database.getUUID
import com.evensberget.accounting.common.domain.EnduserAgreement
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.util.*

class EnduserAgreementQuery(
    private val template: NamedParameterJdbcTemplate,
) {

    private val rowMapper = RowMapper { rs, _ ->
        EnduserAgreement(
            id = rs.getUUID("id"),
            userId = rs.getUUID("user_id"),
            institutionId = rs.getUUID("institution_id"),
            createdAt = rs.getLocalDateTime("created_at"),
            maxHistoricalDays = rs.getInt("max_historical_days"),
            accessValidForDays = rs.getInt("access_valid_for_days"),
            validUntil = rs.getLocalDateTime("valid_until"),
            accessScope = rs.getSet("access_scope"),
            accepted = rs.getString("accepted")
        )
    }

    fun getByNordigenId(id: String): EnduserAgreement {
        val sql = """
            SELECT enduser_agreement.external_id as id,
                   user_account.external_id      as user_id,
                   institution.external_id       as institution_id,
                   enduser_agreement.created_at,
                   enduser_agreement.max_historical_days,
                   enduser_agreement.access_valid_for_days,
                   enduser_agreement.valid_until,
                   enduser_agreement.access_scope,
                   enduser_agreement.accepted
            FROM enduser_agreement
                     INNER JOIN institution on institution.id = enduser_agreement.institution_id
                     INNER JOIN user_account on user_account.id = enduser_agreement.user_id
            WHERE enduser_agreement.nordigen_id = :nordigenId
        """.trimIndent()

        return template.query(
            sql,
            DbUtils.sqlParameters("nordigenId" to id),
            rowMapper
        ).first()
    }

    fun getByUserIdAndInstitutionId(userId: UUID, institutionId: UUID): EnduserAgreement? {
        val sql = """
            SELECT enduser_agreement.external_id as id,
                   user_account.external_id      as user_id,
                   institution.external_id       as institution_id,
                   enduser_agreement.created_at,
                   enduser_agreement.max_historical_days,
                   enduser_agreement.access_valid_for_days,
                   enduser_agreement.valid_until,
                   enduser_agreement.access_scope,
                   enduser_agreement.accepted
            FROM enduser_agreement
                     INNER JOIN institution on institution.id = enduser_agreement.institution_id
                     INNER JOIN user_account on user_account.id = enduser_agreement.user_id
            WHERE user_account.external_id = :userId
            AND institution.external_id = :institutionId
        """.trimIndent()

        return template.query(
            sql,
            DbUtils.sqlParameters(
                "userId" to userId,
                "institutionId" to institutionId
            ),
            rowMapper
        ).firstOrNull()
    }

    fun getByUserIdAndAgreementId(userId: UUID, agreementId: UUID): EnduserAgreement {
        val sql = """
            SELECT enduser_agreement.external_id as id,
                   user_account.external_id      as user_id,
                   institution.external_id       as institution_id,
                   enduser_agreement.created_at,
                   enduser_agreement.max_historical_days,
                   enduser_agreement.access_valid_for_days,
                   enduser_agreement.valid_until,
                   enduser_agreement.access_scope,
                   enduser_agreement.accepted
            FROM enduser_agreement
                     INNER JOIN institution on institution.id = enduser_agreement.institution_id
                     INNER JOIN user_account on user_account.id = enduser_agreement.user_id
            WHERE user_account.external_id = :userId
            AND enduser_agreement.external_id = :agreementId
        """.trimIndent()

        return template.query(
            sql,
            DbUtils.sqlParameters(
                "userId" to userId,
                "agreementId" to agreementId
            ),
            rowMapper
        ).first()
    }
}
