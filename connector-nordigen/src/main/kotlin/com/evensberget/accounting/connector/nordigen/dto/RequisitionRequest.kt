package com.evensberget.accounting.connector.nordigen.dto

import java.util.UUID

data class RequisitionRequest(

    val redirect: String,

    val institution_id: String,

    val reference: String,

    val agreement: UUID,

    val user_language: String = "EN",
)
