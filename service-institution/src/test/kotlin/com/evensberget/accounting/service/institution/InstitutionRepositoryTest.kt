package com.evensberget.accounting.service.institution

import com.evensberget.accounting.connector.nordigen.domain.NordigenInstitution
import com.evensberget.accounting.service.country.CountryRepository
import com.evensberget.accounting.service.country.CountryService
import com.evensberget.accounting.service.institution.repositories.InstitutionRepository
import com.evensberget.accounting.test.database.DbTestDataUtils
import com.evensberget.accounting.test.database.SingletonPostgresContainer
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class InstitutionRepositoryTest : FunSpec({

    val dataSource = SingletonPostgresContainer.getDataSource()

    lateinit var repository: InstitutionRepository

    beforeEach {
        DbTestDataUtils.cleanDatabase(dataSource)

        val countryService = CountryService(CountryRepository(NamedParameterJdbcTemplate(dataSource)))
        repository = InstitutionRepository(NamedParameterJdbcTemplate(dataSource), countryService)
    }

    test("Inserting an institution and get") {
        repository.upsertInstitutions(
            listOf(
                NordigenInstitution(
                    id = "TEST_ID",
                    name = "TEST_NAME",
                    bic = "TEST_BIC",
                    transactionTotalDays = "8",
                    countries = setOf("NO", "DK"),
                    logo = "TEST_LOGO.png"
                )
            )
        )

        val institution = repository.getByName("TEST_NAME")

        institution shouldNotBe null

        institution.name shouldBe "TEST_NAME"
        institution.bic shouldBe "TEST_BIC"
        institution.transactionTotalDays shouldBe 8
        institution.logo shouldBe "TEST_LOGO.png"

        institution.countries.size shouldBe 2

        institution.countries.find { it.name == "Norway" } shouldNotBe null
        institution.countries.find { it.name == "Denmark" } shouldNotBe null
    }

    test("Getting Institution from NordingenId") {
        repository.upsertInstitutions(
            listOf(
                NordigenInstitution(
                    id = "TEST_ID",
                    name = "TEST_NAME",
                    bic = "TEST_BIC",
                    transactionTotalDays = "8",
                    countries = setOf("NO", "DK"),
                    logo = "TEST_LOGO.png"
                )
            )
        )

        val institution = repository.getByName("TEST_NAME")

        val nordigenId = repository.getNordigenIdForInstitution(institution.id)

        nordigenId shouldBe "TEST_ID"
    }

})
