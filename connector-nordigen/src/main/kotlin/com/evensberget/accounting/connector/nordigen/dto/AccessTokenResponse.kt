package com.evensberget.accounting.connector.nordigen.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class AccessTokenResponse(

    @JsonProperty("access")
    val access: String,

    @JsonProperty("access_expires")
    val access_expires: Long,

    @JsonProperty("refresh")
    val refresh: String,

    @JsonProperty("refresh_expires")
    val refresh_expires: Long,

    )
