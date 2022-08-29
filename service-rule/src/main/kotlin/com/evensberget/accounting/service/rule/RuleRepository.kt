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
                RuleEntry("remittanceInformationUnstructured", RuleOperation.CONTAINS, "KIWI")
            ),
            fields = listOf(
                RuleField("description", "Kiwi"),
                RuleField("details", "Kiwi")
            ),
            category = "GROCERIES",
        ),
        Rule(
            rules = listOf(
                RuleEntry("remittanceInformationUnstructured", RuleOperation.CONTAINS, "JOKER")
            ),
            fields = listOf(
                RuleField("description", "Joker"),
                RuleField("details", "Joker")
            ),
            category = "GROCERIES",
        ),
        Rule(
            rules = listOf(
                RuleEntry("remittanceInformationUnstructured", RuleOperation.CONTAINS, "REMA")
            ),
            fields = listOf(
                RuleField("description", "REMA 1000"),
                RuleField("details", "REMA 1000")
            ),
            category = "GROCERIES",
        ),
        Rule(
            rules = listOf(
                RuleEntry("remittanceInformationUnstructured", RuleOperation.CONTAINS, "EXTRA")
            ),
            fields = listOf(
                RuleField("description", "Coop Extra"),
                RuleField("details", "Coop Extra")
            ),
            category = "GROCERIES",
        ),
        Rule(
            rules = listOf(
                RuleEntry("remittanceInformationUnstructured", RuleOperation.CONTAINS, "Oda.com")
            ),
            fields = listOf(
                RuleField("description", "oda.com"),
                RuleField("details", "oda.com")
            ),
            category = "GROCERIES",
        ),
        Rule(
            rules = listOf(
                RuleEntry("remittanceInformationUnstructured", RuleOperation.CONTAINS, "TIER")
            ),
            fields = listOf(
                RuleField("description", "Tier"),
                RuleField("details", "Tier")
            ),
            category = "TRANSPORTATION",
            subCategory = "KICKBOARD_RENTAL"
        ),
        Rule(
            rules = listOf(
                RuleEntry("remittanceInformationUnstructured", RuleOperation.CONTAINS, "Vipps*Ruter")
            ),
            fields = listOf(
                RuleField("description", "Ruter"),
                RuleField("details", "Ruter")
            ),
            category = "TRANSPORTATION",
            subCategory = "PUBLIC_TRANSPORTATION"
        ),
        Rule(
            rules = listOf(
                RuleEntry("remittanceInformationUnstructured", RuleOperation.CONTAINS, "SATS")
            ),
            fields = listOf(
                RuleField("description", "Sats"),
                RuleField("details", "Sats")
            ),
            category = "HEALTH_TREATMENTS_WELLBEING",
            subCategory = "SUPPLIMENTS"
        ),
        Rule(
            rules = listOf(
                RuleEntry("remittanceInformationUnstructured", RuleOperation.CONTAINS, "Edge Barbershop")
            ),
            fields = listOf(
                RuleField("description", "Frisør: Edge Barbershop"),
                RuleField("details", "Frisør: Edge Barbershop Lillestrøm")
            ),
            category = "HEALTH_TREATMENTS_WELLBEING",
            subCategory = "HAIRCUT"
        ),
        Rule(
            rules = listOf(
                RuleEntry("remittanceInformationUnstructured", RuleOperation.CONTAINS, "FLYT TRAFIKKSK")
            ),
            fields = listOf(
                RuleField("description", "Flyt: Kjøretime"),
                RuleField("details", "Flyt: Kjøretime")
            ),
            category = "TRANSPORTATION",
            subCategory = "CAR"
        ),
    )

    fun getRulesForUser(userId: UUID): List<Rule> {
        return rules
    }

}
