package com.evensberget.accounting.service.institution

import com.evensberget.accounting.common.domain.Institution
import com.evensberget.accounting.common.domain.User
import com.evensberget.accounting.connector.nordigen.domain.NordigenInstitution
import com.evensberget.accounting.connector.nordigen.dto.EndUserAgreementResponse
import com.evensberget.accounting.service.country.CountryRepository
import com.evensberget.accounting.service.country.CountryService
import com.evensberget.accounting.service.institution.repositories.EnduserAgreementRepository
import com.evensberget.accounting.service.institution.repositories.InstitutionRepository
import com.evensberget.accounting.service.user.UserRepository
import com.evensberget.accounting.test.database.DbTestDataUtils
import com.evensberget.accounting.test.database.SingletonPostgresContainer
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.time.LocalDateTime

class EnduserAgreementRepositoryTest : FunSpec({

    val dataSource = SingletonPostgresContainer.getDataSource()

    lateinit var userRepository: UserRepository
    lateinit var institutionRepository: InstitutionRepository
    lateinit var repository: EnduserAgreementRepository

    val testEmail = "test@test.com"
    lateinit var testUser: User

    val testInstitutionInput = NordigenInstitution(
        id = "TEST_ID",
        name = "TEST_NAME",
        bic = "TEST_BIC",
        transactionTotalDays = "8",
        countries = setOf("NO", "DK"),
        logo = "TEST_LOGO.png"
    )
    lateinit var testInstitution: Institution


    beforeEach {
        DbTestDataUtils.cleanDatabase(dataSource)

        userRepository = UserRepository(NamedParameterJdbcTemplate(dataSource))
        userRepository.upsert(testEmail)
        testUser = userRepository.get(testEmail)!!


        val countryService = CountryService(CountryRepository(NamedParameterJdbcTemplate(dataSource)))
        institutionRepository = InstitutionRepository(NamedParameterJdbcTemplate(dataSource), countryService)

        institutionRepository.upsertInstitutions(listOf(testInstitutionInput))
        testInstitution = institutionRepository.getByName(testInstitutionInput.name)

        repository = EnduserAgreementRepository(NamedParameterJdbcTemplate(dataSource))
    }

    test("Access Scope is serialized and deserialized correctly") {
        val data = EndUserAgreementResponse(
            id = "TEST_ID",
            created = LocalDateTime.now(),
            maxHistoricalDays = 320,
            accessValidForDays = 30,
            accessScope = listOf("ONE", "TWO", "THREE"),
            accepted = "",
            institutionId = testInstitutionInput.id
        )

        val agreement = repository.addEnduserAgreement(testUser.id, data)

        agreement.accessScope.size shouldBe 3
    }

})
