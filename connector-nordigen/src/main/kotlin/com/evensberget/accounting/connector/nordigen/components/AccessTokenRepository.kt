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
            source = rs.getString("source"),
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
                                 refresh_expire_timestamp, source)
        VALUES (:createdAt,
                :access,
                :accessExpire,
                :accessExpireTimestamp,
                :refresh,
                :refreshExpire,
                :refreshExpireTimestamp,
                :source)
        ON CONFLICT (source) DO UPDATE SET created_at =:createdAt, 
                                           access = :access,
                                           access_expire = :accessExpire,
                                           access_expire_timestamp = :accessExpireTimestamp,
                                           refresh = :refresh,
                                           refresh_expire = :refreshExpire,
                                           refresh_expire_timestamp = :refreshExpireTimestamp
    """.trimIndent()

    fun addReplaceAccessToken(accessToken: AccessToken) {
        template.update(
            addReplaceAccessTokenSql,
            DbUtils.sqlParameters(
                "createdAt" to accessToken.createdAt,
                "access" to accessToken.access,
                "accessExpire" to accessToken.accessExpire,
                "accessExpireTimestamp" to accessToken.accessExpireTime,
                "refresh" to accessToken.refresh,
                "refreshExpire" to accessToken.refreshExpire,
                "refreshExpireTimestamp" to accessToken.refreshExpireTime,
                "source" to accessToken.source
            )
        )
    }

    fun getAccessToken(): AccessToken? {
        return template.query(
            "SELECT * FROM access_token",
            rowMapper
        ).firstOrNull()
    }
}
