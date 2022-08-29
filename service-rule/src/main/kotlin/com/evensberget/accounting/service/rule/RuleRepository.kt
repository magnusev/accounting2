package com.evensberget.accounting.service.rule

import com.evensberget.accounting.common.domain.rule.Rule
import com.evensberget.accounting.common.domain.rule.RuleEntry
import com.evensberget.accounting.common.domain.rule.RuleField
import com.evensberget.accounting.common.domain.rule.RuleOperation
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Component
import java.util.*

@Component
class RuleRepository(
    private val template: NamedParameterJdbcTemplate
) {

    private val rules = listOf(
        Rule(
            rules = listOf(
                RuleEntry("creditor_name", RuleOperation.CONTAINS, "Oslo mekaniske")
            ),
            fields = listOf(
                RuleField("description", "Oslo Mekaniske Verksted"),
                RuleField("details", "Oslo Mekaniske Verksted")
            ),
            category = "EATING_OUT_TAKE_AWAY",
            subCategory = "BAR",
        ),
        Rule(
            rules = listOf(
                RuleEntry("details", RuleOperation.CONTAINS, "KIWI")
            ),
            fields = listOf(
                RuleField("description", "Kiwi"),
                RuleField("details", "Kiwi")
            ),
            category = "GROCERIES",
        )
    )

    fun getRulesForUser(userId: UUID): List<Rule> {
        return rules
    }

}
