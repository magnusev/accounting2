package com.evensberget.accounting.service.country

import Country
import com.evensberget.accounting.common.database.DbUtils
import com.evensberget.accounting.common.database.getUUID
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component

@Component
class CountryRepository(
    private val template: NamedParameterJdbcTemplate
) {

    private val rowMapper = RowMapper { rs, _ ->
        Country(
            id = rs.getUUID("external_id"),
            name = rs.getString("name"),
            region = rs.getString("region"),
            alpha2 = rs.getString("alpha_2"),
            alpha3 = rs.getString("alpha_3")
        )
    }

    private val insertSql = """
        INSERT INTO country(external_id, name, region, alpha_2, alpha_3)
        VALUES (:externalId,
                :name,
                :region,
                :alpha2,
                :alpha3)
    """.trimIndent()

    fun insert(country: Country) {
        val parameters = DbUtils.sqlParameters(
            "externalId" to country.id,
            "name" to country.name,
            "region" to country.region,
            "alpha2" to country.alpha2.uppercase(),
            "alpha3" to country.alpha3.uppercase()
        )

        template.update(insertSql, parameters)
    }

    fun getCountryByISO3166(code: String): Country? {
        val sql = """
            SELECT * FROM country
                WHERE alpha_2 = :alpha2
        """.trimIndent()

        val parameters = DbUtils.sqlParameters(
            "alpha2" to code.uppercase()
        )

        return template.query(sql, parameters, rowMapper)
            .firstOrNull()
    }

}
