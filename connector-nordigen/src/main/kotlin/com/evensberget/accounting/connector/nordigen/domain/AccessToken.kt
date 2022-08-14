package com.evensberget.accounting.connector.nordigen.domain

import java.time.LocalDateTime

data class AccessToken(
    val source: String,
    val createdAt: LocalDateTime,
    val access: String,
    val accessExpire: Long,
    val accessExpireTime: LocalDateTime,
    val refresh: String,
    val refreshExpire: Long,
    val refreshExpireTime: LocalDateTime
)
