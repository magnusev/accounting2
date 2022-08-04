package com.evensberget.accounting.common.database

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource

object DbUtils {

	fun <V> sqlParameters(vararg pairs: Pair<String, V>): MapSqlParameterSource {
		return MapSqlParameterSource().addValues(pairs.toMap())
	}

}
