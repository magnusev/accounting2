package com.evensberget.accounting.service.rule

import com.evensberget.accounting.common.database.DbUtils
import com.evensberget.accounting.common.database.getUUID
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component
import java.util.*

@Component
class NordigenTransactionTranslationRepository(
    private val template: NamedParameterJdbcTemplate
) {

    private val map = mutableMapOf<String, UUID>()

    private val rowMapper = RowMapper { rs, _ ->
        Pair(rs.getString("nordigen_id"), rs.getUUID("accounting_id"))
    }

    fun getIdForNordigenId(nordigenId: String): UUID {
        if (map.isEmpty()) {
            map.putAll(getAll())
        }

        if (map[nordigenId] != null) return map[nordigenId]!!
        return insert(nordigenId)
    }

    fun insert(nordigenId: String): UUID {
        val id = UUID.randomUUID()

        val sql = """
            INSERT INTO nordigen_transaction_id_to_internal_id(nordigen_id, accounting_id) 
            VALUES(:nordigenId, :accountingId)
        """.trimIndent()

        template.update(
            sql,
            DbUtils.sqlParameters(
                "nordigenId" to nordigenId,
                "accountingId" to id
            )
        )

        map[nordigenId] = id
        return id
    }

    private fun get(nordigenId: String): UUID? {
        return template.query(
            "SELECT * FROM nordigen_transaction_id_to_internal_id WHERE nordigen_id = :nordigenId",
            DbUtils.sqlParameters("nordigenId" to nordigenId)
        ) { rs, _ -> rs.getUUID("accounting_id") }.firstOrNull()
    }

    private fun getAll(): List<Pair<String, UUID>> {
        return template.query(
            "SELECT * FROM nordigen_transaction_id_to_internal_id",
            rowMapper
        )
    }

}
