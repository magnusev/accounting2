package com.evensberget.accounting.service.institution

import com.evensberget.accounting.connector.nordigen.domain.NordigenAccount
import com.evensberget.accounting.connector.nordigen.domain.NordigenInstitution
import com.evensberget.accounting.service.country.CountryRepository
import com.evensberget.accounting.service.country.CountryService
import com.evensberget.accounting.service.institution.repositories.AccountRepository
import com.evensberget.accounting.service.institution.repositories.InstitutionRepository
import com.evensberget.accounting.service.user.UserRepository
import com.evensberget.accounting.test.database.DbTestDataUtils
import com.evensberget.accounting.test.database.SingletonPostgresContainer
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldNotBe
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.time.LocalDateTime
import java.util.*

class AccountRepositoryTest : FunSpec({

    val dataSource = SingletonPostgresContainer.getDataSource()

    lateinit var userRepository: UserRepository
    lateinit var institutionRepository: InstitutionRepository
    lateinit var accountRepository: AccountRepository

    lateinit var userId: UUID

    fun setupTestData() {
        userRepository.upsert("test@test.com")
        userId = userRepository.get("test@test.com")?.id
            ?: throw IllegalStateException()

        institutionRepository.upsertInstitutions(
            listOf(
                NordigenInstitution(
                    id = "TEST",
                    name = "TEST",
                    bic = "TEST",
                    transactionTotalDays = "20",
                    countries = setOf("NO"),
                    logo = "TEST.png"
                )
            )
        )
    }

    beforeEach {
        DbTestDataUtils.cleanDatabase(dataSource)
        val template = NamedParameterJdbcTemplate(dataSource)

        userRepository = UserRepository(template)

        val countryService = CountryService(CountryRepository(template))
        institutionRepository = InstitutionRepository(template, countryService)

        accountRepository = AccountRepository(template)
        setupTestData()
    }


    test("Inserting Account ang get") {
        val account = accountRepository.upsertAccount(
            userId, NordigenAccount(
                id = UUID.randomUUID(),
                created = LocalDateTime.now(),
                lastAccessed = LocalDateTime.now(),
                institutionId = "TEST",
                name = "TEST_NAME",
                status = "TEST",
                owner = "TEST!",
                resourceId = "TEST",
                iban = "TEST",
                bban = "TEST",
                currency = "NOK",
                product = null,
                cashAccountType = "TEST"
            )
        )

        account shouldNotBe null
    }


})
