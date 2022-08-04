package com.evensberget.accounting.connector.nordigen.dto

import com.evensberget.accounting.common.json.JsonUtils
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class EndUserAgreementResponseTests : FunSpec({

    test("Deserialization of response") {
        val exampleResponseJson = """
            {
               "id":"3fa85f64-5717-4562-b3fc-2c963f66afa6",
               "created":"2021-10-25T16:41:09.753Z",
               "max_historical_days":180,
               "access_valid_for_days":30,
               "access_scope":[
                  "balances",
                  "details",
                  "transactions"
               ],
               "accepted":"",
               "institution_id":"REVOLUT_REVOGB21"
            }
        """.trimIndent()

        val response = JsonUtils.fromJson(exampleResponseJson, EndUserAgreementResponse::class.java)

        response.id shouldBe "3fa85f64-5717-4562-b3fc-2c963f66afa6"
        response.created.truncatedTo(ChronoUnit.SECONDS) shouldBe LocalDateTime.of(2021, 10, 25, 16, 41, 9)
    }

})
