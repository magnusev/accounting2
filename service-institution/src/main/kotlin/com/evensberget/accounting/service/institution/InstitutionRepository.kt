package com.evensberget.accounting.service.institution

import com.evensberget.accounting.common.database.DbUtils
import com.evensberget.accounting.connector.nordigen.domain.NordigenInstitution
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component
import java.util.*

@Component
class InstitutionRepository(
    private val template: NamedParameterJdbcTemplate
) {

    private val upsertSql = """
        INSERT INTO institution(external_id, nordigen_id, bic, transaction_total_days, logo)
        VALUES (:externalId,
                :nordigenId,
                :bic,
                :transactionTotalDays,
                :logo)
        ON CONFLICT (nordigen_id) DO UPDATE SET bic = :bic,
                                                transaction_total_days = :transactionTotalDays,
                                                logo = :logo
    """.trimIndent()

    private val connectCountriesSql = """
        INSERT INTO institution_country(country_id, institution_id) 
        VALUES (
            (SELECT id from country WHERE alpha_2 = :alpha2),
            (SELECT id from institution WHERE nordigen_id = :nordigenId)                    
        ) 
        ON CONFLICT(country_id, institution_id) DO NOTHING 
    """.trimIndent()

    fun upsertInstitutions(institutions: List<NordigenInstitution>) {
        template.batchUpdate(upsertSql, institutions.map {
            DbUtils.sqlParameters(
                "externalId" to UUID.randomUUID(),
                "nordigenId" to it.id,
                "bic" to it.bic,
                "transactionTotalDays" to it.transactionTotalDays,
                "logo" to it.logo
            )
        }.toTypedArray())


        val pairs = mutableListOf<Pair<String, String>>()

        institutions.forEach { institution ->
            institution.countries.forEach { country ->
                pairs.add(Pair(institution.id, country))
            }
        }

        val parameters = pairs.map {
            DbUtils.sqlParameters(
                "nordigenId" to it.first,
                "alpha2" to it.second
            )
        }.toTypedArray()

        template.batchUpdate(connectCountriesSql, parameters)
    }

}
