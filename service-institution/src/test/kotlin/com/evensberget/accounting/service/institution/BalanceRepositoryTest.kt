package com.evensberget.accounting.service.institution

import com.evensberget.accounting.connector.nordigen.domain.NordigenAccount
import com.evensberget.accounting.connector.nordigen.domain.NordigenBalance
import com.evensberget.accounting.connector.nordigen.domain.NordigenInstitution
import com.evensberget.accounting.service.country.CountryRepository
import com.evensberget.accounting.service.country.CountryService
import com.evensberget.accounting.service.institution.repositories.AccountRepository
import com.evensberget.accounting.service.institution.repositories.BalanceRepository
import com.evensberget.accounting.service.institution.repositories.InstitutionRepository
import com.evensberget.accounting.service.user.UserRepository
import com.evensberget.accounting.test.database.DbTestDataUtils
import com.evensberget.accounting.test.database.SingletonPostgresContainer
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*

class BalanceRepositoryTest : FunSpec({

    val dataSource = SingletonPostgresContainer.getDataSource()

    lateinit var userRepository: UserRepository
    lateinit var institutionRepository: InstitutionRepository
    lateinit var accountRepository: AccountRepository
    lateinit var balanceRepository: BalanceRepository

    beforeEach {
        DbTestDataUtils.cleanDatabase(dataSource)

        val template = NamedParameterJdbcTemplate(dataSource)

        val countryService = CountryService(CountryRepository(template))

        userRepository = UserRepository(template)
        institutionRepository = InstitutionRepository(template, countryService)
        accountRepository = AccountRepository(template)
        balanceRepository = BalanceRepository(template)
    }

    test("Ingesting Balances of an account returns domain objects") {
        val user = userRepository.upsert("test@test.no")

        institutionRepository.upsertInstitutions(
            listOf(
                NordigenInstitution(
                    id = "TEST_INSTITUTION",
                    name = "TEST_INSTITUTION",
                    bic = "BIC",
                    transactionTotalDays = "200",
                    countries = setOf("NO"),
                    logo = "LOGO"
                )
            )
        )

        val account = accountRepository.upsertAccount(
            userId = user.id,
            nordigenAccount = NordigenAccount(
                id = UUID.randomUUID(),
                created = LocalDateTime.now(),
                lastAccessed = LocalDateTime.now(),
                institutionId = "TEST_INSTITUTION",
                status = "STATUS",
                owner = "TEST_OWNER",
                resourceId = "RESOURCE_ID",
                iban = "IBAN",
                bban = "BBAN",
                currency = "CURRENCY",
                name = "NAME",
                product = "PRODUCT",
                cashAccountType = null
            )
        )

        val balances = balanceRepository.upsertBalances(
            accountId = account.id,
            listOf(
                NordigenBalance(2.23, "NOK", "1", LocalDate.now().minusDays(2)),
                NordigenBalance(2.0, "NOK", "2", LocalDate.now().minusDays(2)),
                NordigenBalance(100.0, "NOK", "3", LocalDate.now().minusDays(2))
            )
        )

        balances.size shouldBe 3
    }

})
