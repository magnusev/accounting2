package com.evensberget.accounting.connector.nordigen.components

import com.evensberget.accounting.common.database.DbUtils
import com.evensberget.accounting.common.database.getLocalDateTime
import com.evensberget.accounting.connector.nordigen.domain.AccessToken
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component

@Component
class AccessTokenRepository(
    private val template: NamedParameterJdbcTemplate
) {

    private val rowMapper = RowMapper { rs, _ ->
        AccessToken(
            createdAt = rs.getLocalDateTime("created_at"),
            access = rs.getString("access"),
            accessExpire = rs.getLong("access_expire"),
            accessExpireTime = rs.getLocalDateTime("access_expire_timestamp"),
            refresh = rs.getString("refresh"),
            refreshExpire = rs.getLong("refresh_expire"),
            refreshExpireTime = rs.getLocalDateTime("refresh_expire_timestamp")
        )
    }

    private val addReplaceAccessTokenSql = """
        INSERT INTO access_token(created_at, access, access_expire,
                                 access_expire_timestamp, refresh, refresh_expire,
                                 refresh_expire_timestamp)
        VALUES (:createdAt,
                :access,
                :accessExpire,
                :accessExpireTimestamp,
                :refresh,
                :refreshExpire,
                :refreshExpireTimestamp)                        
    """.trimIndent()

    fun addReplaceAccessToken(accessToken: AccessToken) {
        deleteTokens()

        template.update(
            addReplaceAccessTokenSql,
            DbUtils.sqlParameters(
                "createdAt" to accessToken.createdAt,
                "access" to accessToken.access,
                "accessExpire" to accessToken.accessExpire,
                "accessExpireTimestamp" to accessToken.accessExpireTime,
                "refresh" to accessToken.refresh,
                "refreshExpire" to accessToken.refreshExpire,
                "refreshExpireTimestamp" to accessToken.refreshExpireTime
            )
        )
    }

    fun getAccessToken(): AccessToken? {
        return template.query(
            "SELECT * FROM access_token",
            rowMapper
        ).firstOrNull()
    }

    private fun deleteTokens() {
        if(getAccessToken() != null) {
            template.query(
                "DELETE FROM access_token", rowMapper
            )
        }
    }

}
