package com.evensberget.accounting.common.database

import java.sql.ResultSet
import java.time.*
import java.util.*
import java.util.stream.Collectors


fun ResultSet.getUUID(columnLabel: String): UUID {
    return getNullableUUID(columnLabel) ?: throw IllegalStateException("Expected $columnLabel not to be null")
}

fun ResultSet.getNullableUUID(columnLabel: String): UUID? {
    return this.getString(columnLabel)
        ?.let { UUID.fromString(it) }
}

fun <T> ResultSet.getSet(columnLabel: String): Set<T> {
    return Arrays.stream(this.getArray(columnLabel).array as Array<T>)
        .collect(Collectors.toSet())
}

fun <T> ResultSet.getList(columnLabel: String): List<T> {
    return Arrays.stream(this.getArray(columnLabel).array as Array<T>)
        .collect(Collectors.toList())
}

fun ResultSet.getLocalDateTime(columnLabel: String): LocalDateTime {
    return getNullableLocalDateTime(columnLabel) ?: throw IllegalStateException("Expected $columnLabel not to be null")
}

fun ResultSet.getNullableLocalDateTime(columnLabel: String): LocalDateTime? {
    return this.getTimestamp(columnLabel)?.toLocalDateTime()
}

fun ResultSet.getLocalDate(columnLabel: String): LocalDate {
    return getNullableLocalDate(columnLabel) ?: throw IllegalStateException("Expected $columnLabel not to be null")
}

fun ResultSet.getNullableLocalDate(columnLabel: String): LocalDate? {
    return this.getDate(columnLabel)?.toLocalDate()
}

fun ResultSet.getNullableZonedDateTime(columnLabel: String): ZonedDateTime? {
    val timestamp = this.getTimestamp(columnLabel) ?: return null
    return ZonedDateTime.ofInstant(Instant.ofEpochMilli(timestamp.time), ZoneOffset.systemDefault())
}

fun ResultSet.getZonedDateTime(columnLabel: String): ZonedDateTime {
    return getNullableZonedDateTime(columnLabel) ?: throw IllegalStateException("Expected $columnLabel not to be null")
}

fun ResultSet.getNullableString(columnLabel: String): String? {
    return this.getString(columnLabel)
}
