package com.evensberget.accounting.service.institution.repositories.queries

import com.evensberget.accounting.common.database.DbUtils
import com.evensberget.accounting.common.database.getUUID
import com.evensberget.accounting.common.domain.Country
import com.evensberget.accounting.common.domain.Institution
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.util.*

class InstitutionQuery(
    private val template: NamedParameterJdbcTemplate
) {

    private val institutionRowMapper = RowMapper { rs, _ ->
        InsitutionDbo(
            id = rs.getInt("id"),
            externalId = rs.getUUID("external_id"),
            nordigenId = rs.getString("nordigen_id"),
            name = rs.getString("name"),
            bic = rs.getString("bic"),
            transactionTotalDays = rs.getInt("transaction_total_days"),
            logo = rs.getString("logo")
        )
    }

    private val countryRowMapper = RowMapper { rs, _ ->
        CountryDbo(
            id = rs.getInt("id"),
            externalId = rs.getUUID("external_id"),
            name = rs.getString("name"),
            region = rs.getString("region"),
            alpha2 = rs.getString("alpha_2"),
            alpha3 = rs.getString("alpha_3")
        )
    }

    fun getByName(name: String): Institution {
        val institutionDbo = template.query(
            "SELECT * FROM institution WHERE name = :name",
            DbUtils.sqlParameters("name" to name),
            institutionRowMapper
        ).first()

        val countries = getCountriesForInstitution(institutionDbo.id)

        return institutionDbo.institution(countries)
    }

    private fun getCountriesForInstitution(institutionId: Int): List<CountryDbo> {
        return template.query(
            """
                SELECT country.id          as id,
                       country.external_id as external_id,
                       country.name        as name,
                       country.region      as region,
                       country.alpha_2     as alpha_2,
                       country.alpha_3     as alpha_3
                FROM country
                         INNER JOIN institution_country on country.id = institution_country.country_id
                WHERE institution_country.institution_id = :institutionId
            """.trimIndent(),
            DbUtils.sqlParameters("institutionId" to institutionId),
            countryRowMapper
        )
    }

    private data class CountryDbo(
        val id: Int,
        val externalId: UUID,
        val name: String,
        val region: String,
        val alpha2: String,
        val alpha3: String
    ) {

        fun country() = Country(
            id = externalId,
            name = name,
            region = region,
            alpha2 = alpha2,
            alpha3 = alpha3
        )

    }

    private data class InsitutionDbo(
        val id: Int,
        val externalId: UUID,
        val nordigenId: String,
        val name: String,
        val bic: String,
        val transactionTotalDays: Int,
        val logo: String
    ) {

        fun institution(countries: List<CountryDbo>) = Institution(
            id = externalId,
            name = name,
            bic = bic,
            transactionTotalDays = transactionTotalDays,
            logo = logo,
            countries = countries.map { it.country() }
        )

    }
}
