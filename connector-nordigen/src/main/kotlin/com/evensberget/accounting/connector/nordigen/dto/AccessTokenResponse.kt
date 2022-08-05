package com.evensberget.accounting.connector.nordigen.dto

import com.evensberget.accounting.connector.nordigen.domain.AccessToken
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

data class AccessTokenResponse(

    @JsonProperty("access")
    val access: String,

    @JsonProperty("access_expires")
    val access_expires: Long,

    @JsonProperty("refresh")
    val refresh: String,

    @JsonProperty("refresh_expires")
    val refresh_expires: Long,

    ) {

    fun toModel(): AccessToken = AccessToken(
        createdAt = LocalDateTime.now(),
        access = access,
        accessExpire = access_expires,
        accessExpireTime = LocalDateTime.now().plus(access_expires, ChronoUnit.SECONDS),
        refresh = refresh,
        refreshExpire = refresh_expires,
        refreshExpireTime = LocalDateTime.now().plus(refresh_expires, ChronoUnit.SECONDS)
    )
}
