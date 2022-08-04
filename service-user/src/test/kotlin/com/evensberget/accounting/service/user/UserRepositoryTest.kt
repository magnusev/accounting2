package com.evensberget.accounting.service.user

import com.evensberget.accounting.test.database.DbTestDataUtils
import com.evensberget.accounting.test.database.SingletonPostgresContainer
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate

class UserRepositoryTest : FunSpec({

    val dataSource = SingletonPostgresContainer.getDataSource()

    lateinit var userRepository: UserRepository

    beforeEach {
        DbTestDataUtils.cleanDatabase(dataSource)
        userRepository = UserRepository(NamedParameterJdbcTemplate(dataSource))
    }

    test("Can insert and get user") {
        val testEmail = "test@test.com"

        userRepository.upsert(testEmail)

        val user = userRepository.get(testEmail)

        user shouldNotBe null
        user!!.email shouldBe testEmail
    }

})
