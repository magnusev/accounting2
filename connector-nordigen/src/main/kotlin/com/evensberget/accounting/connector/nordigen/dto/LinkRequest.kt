package com.evensberget.accounting.connector.nordigen.dto

data class LinkRequest(

    val redirect: String,

    val institution_id: String,

    val reference: String,

    val agreement: String,

    val user_language: String = "EN",
)
