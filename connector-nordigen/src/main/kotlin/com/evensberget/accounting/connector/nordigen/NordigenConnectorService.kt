package com.evensberget.accounting.connector.nordigen

import com.evensberget.accounting.common.domain.Transaction
import com.evensberget.accounting.common.domain.TransactionStatus
import com.evensberget.accounting.common.json.JsonUtils
import com.evensberget.accounting.connector.nordigen.components.NordigenAccesTokenComponent
import com.evensberget.accounting.connector.nordigen.domain.NordigenInstitution
import com.evensberget.accounting.connector.nordigen.dto.InstitutionsResponse
import com.evensberget.accounting.connector.nordigen.dto.TransactionsResponse
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.io.File

@Service
class NordigenConnectorService(
    private val accessToken: NordigenAccesTokenComponent,
    private val template: RestTemplate
) {

    fun getInstitutions(): List<NordigenInstitution> {
        val url = "https://ob.nordigen.com/api/v2/institutions/?country=no"

        val headers = HttpHeaders()
        headers.set("accept", "application/json")
        headers.set("Authorization", "Bearer ${accessToken.getToken()}")

        val entity = HttpEntity(null, headers)

        val i = template.exchange(url, HttpMethod.GET, entity, Array<InstitutionsResponse>::class.java)

        val data = i.body?.toList()
            ?: throw UnsupportedOperationException("Body should not be null")

        return data.map { it.toNordigenInstitution() }
    }

    fun getTransactions(): List<Transaction> {
        val nordigenTransactions = JsonUtils.fromJson(getJson(), TransactionsResponse::class.java)
            .transactions

        return nordigenTransactions.booked
            .map { it.toTransaction(TransactionStatus.BOOKED) }
            .plus(nordigenTransactions.pending
                .map { it.toTransaction(TransactionStatus.PENDING) }
            )
    }

    private fun getJson(): String {
        return File("data/nordigen_transactions.json").readText(Charsets.UTF_8)
    }
}
