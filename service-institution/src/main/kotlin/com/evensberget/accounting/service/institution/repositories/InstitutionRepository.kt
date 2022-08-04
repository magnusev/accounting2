package com.evensberget.accounting.service.institution.repositories

import com.evensberget.accounting.common.database.DbUtils
import com.evensberget.accounting.common.domain.Institution
import com.evensberget.accounting.connector.nordigen.domain.NordigenInstitution
import com.evensberget.accounting.service.country.CountryService
import com.evensberget.accounting.service.institution.repositories.queries.InstitutionQuery
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component
import java.util.*

@Component
class InstitutionRepository(
    private val template: NamedParameterJdbcTemplate,
    private val countryService: CountryService
) {

    private val upsertSql = """
        INSERT INTO institution(external_id, nordigen_id, bic, name, transaction_total_days, logo)
        VALUES (:externalId,
                :nordigenId,
                :bic,
                :name,
                :transactionTotalDays,
                :logo)
        ON CONFLICT (nordigen_id) DO UPDATE SET bic = :bic,
                                                name = :name,
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
                "name" to it.name,
                "bic" to it.bic,
                "transactionTotalDays" to it.transactionTotalDays,
                "logo" to it.logo
            )
        }.toTypedArray())

        updateInstitutuionCountryConnections(institutions)
    }

    fun getNordigenIdForInstitution(id: UUID): String {
        return template.query(
            "SELECT * FROM institution WHERE external_id = :id",
            DbUtils.sqlParameters("id" to id)
        ) { rs, _ -> rs.getString("nordigen_id") }.first()
    }

    private fun updateInstitutuionCountryConnections(institutions: List<NordigenInstitution>) {
        val pairs = mutableListOf<Pair<String, String>>()

        institutions.forEach { institution ->
            institution.countries.forEach { country ->
                pairs.add(Pair(institution.id, country))
            }
        }

        pairs.forEach { countryService.getCountryByISO3166(it.second) }

        val parameters = pairs.map {
            DbUtils.sqlParameters(
                "nordigenId" to it.first,
                "alpha2" to it.second
            )
        }.toTypedArray()

        template.batchUpdate(connectCountriesSql, parameters)
    }

    fun getByName(name: String): Institution {
        return InstitutionQuery(template)
            .getByName(name)
    }

}
