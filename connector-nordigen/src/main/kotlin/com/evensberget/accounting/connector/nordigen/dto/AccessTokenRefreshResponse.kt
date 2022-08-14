package com.evensberget.accounting.connector.nordigen.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class AccessTokenRefreshResponse(
    @JsonProperty("access")
    val access: String,

    @JsonProperty("access_expires")
    val access_expires: Long,
)
