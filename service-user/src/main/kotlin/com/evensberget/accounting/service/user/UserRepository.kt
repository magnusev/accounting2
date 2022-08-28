package com.evensberget.accounting.service.user

import com.evensberget.accounting.common.database.DbUtils
import com.evensberget.accounting.common.database.getUUID
import com.evensberget.accounting.common.domain.User
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component
import java.util.*

@Component
class UserRepository(
    private val template: NamedParameterJdbcTemplate
) {

    private val rowMapper = RowMapper { rs, _ ->
        User(
            id = rs.getUUID("external_id"),
            email = rs.getString("email")
        )
    }

    private val upsertSql = """
        INSERT INTO user_account(external_id, email) 
        VALUES (:externalId,
                :email)
        ON CONFLICT (email) DO NOTHING
    """.trimIndent()

    fun upsert(email: String): User {
        template.update(
            upsertSql, DbUtils.sqlParameters(
                "externalId" to UUID.randomUUID(),
                "email" to email
            )
        )

        return get(email)
            ?: throw IllegalStateException("Expected User with email $email to exist")
    }

    fun get(email: String): User? {
        return template.query(
            "SELECT * from user_account WHERE email = :email",
            DbUtils.sqlParameters("email" to email),
            rowMapper
        ).firstOrNull()
    }

}
