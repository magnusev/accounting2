package com.evensberget.accounting.service.institution

import com.evensberget.accounting.common.cache.CacheUtils
import com.evensberget.accounting.common.domain.*
import com.evensberget.accounting.connector.nordigen.NordigenConnectorService
import com.evensberget.accounting.service.institution.repositories.*
import com.github.benmanes.caffeine.cache.Caffeine
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.Duration
import java.util.*

@Service
class InstitutionService(
    private val nordigenConnector: NordigenConnectorService,
    private val institutionRepository: InstitutionRepository,
    private val enduserAgreementRepository: EnduserAgreementRepository,
    private val accountRepository: AccountRepository,
    private val requisitionRepository: RequisitionRepository,
    private val balanceRepository: BalanceRepository,
    private val rawTransactionRepository: RawTransactionRepository,
    private val transactionRepository: TransactionRepository
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    private val enduserAgreementCache = Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofHours(6))
        .maximumSize(1000)
        .build<EnduserAgreementCacheKey, EnduserAgreement>()

    init {
//        updateInstitutions()
    }

    fun updateInstitutions() {
        logger.info("Updating institutions...")
        val nordigenInstitutions = nordigenConnector.getInstitutions()
        institutionRepository.upsertInstitutions(nordigenInstitutions)
        logger.info("Institutions updated!")
    }

    fun getInstitutionByName(name: String): Institution {
        return institutionRepository.getByName(name)
    }

    fun enduserAgreement(userId: UUID, institutionId: UUID): EnduserAgreement {
        return CacheUtils.tryCacheFirstNotNull(enduserAgreementCache, EnduserAgreementCacheKey(userId, institutionId)) {
            val agreementFromDb = enduserAgreementRepository.getEnduserAgreement(userId, institutionId)

            if (agreementFromDb != null) {
                enduserAgreementCache.put(EnduserAgreementCacheKey(userId, institutionId), agreementFromDb)
                agreementFromDb
            } else {
                val institutionNordigenId = institutionRepository.getNordigenIdForInstitution(institutionId)
                val fromNordigen = nordigenConnector.createEnduserAgreement(institutionNordigenId)

                val newAgreement = enduserAgreementRepository.addEnduserAgreement(userId, fromNordigen)
                enduserAgreementCache.put(EnduserAgreementCacheKey(userId, institutionId), newAgreement)
                newAgreement
            }
        }
    }

    fun createRequisition(userId: UUID, agreementId: UUID): Requisition {
        val agreement = enduserAgreementRepository.getEnduserAgreementByAgreementId(userId, agreementId)

        val requisition = nordigenConnector.createRequisition(
            ref = """{"userId": "$userId", "agreementId": "$agreementId", "uniqueId": "${UUID.randomUUID()}"}""",
            institutionId = institutionRepository.getNordigenIdForInstitution(agreement.institutionId),
            agreementId = enduserAgreementRepository.getNordigenId(agreementId)
        )

        return requisitionRepository.upsertRequisition(requisition, agreement.institutionId, agreement.id)
    }

    fun updateRequisition(id: UUID): Requisition {
        val oldRequisition = requisitionRepository.get(id)
        val nordigenId = requisitionRepository.getNordigenId(id)
        val nordigenRequisition = nordigenConnector.getRequisition(nordigenId)

        return requisitionRepository.upsertRequisition(
            nordigenRequisition,
            oldRequisition.institutionId,
            oldRequisition.agreementId
        )
    }

    fun getRequisition(id: UUID): Requisition {
        return requisitionRepository.get(id)
    }

    fun addAccounts(userId: UUID, accounts: List<UUID>): List<Account> {
        return accounts.map { account ->
            accountRepository.upsertAccount(userId, nordigenConnector.getAccount(account))
        }
    }

    fun getAccounts(userId: UUID): List<Account> {
        return accountRepository.getAccountsForUser(userId)
    }

    fun updateBalances(accountId: UUID) {
        val nordigenAccountId = accountRepository.getNordigenId(accountId)
        val balances = nordigenConnector.getBalances(nordigenAccountId)
        balanceRepository.upsertBalances(accountId, balances)
    }

    fun updateRawTransactions(accountId: UUID) {
        val latestBookingDate = rawTransactionRepository.getLatestBookingDate(accountId)
        val nordigenAccountId = accountRepository.getNordigenId(accountId)

        val transactions = nordigenConnector.getTransactions(nordigenAccountId, latestBookingDate)
        rawTransactionRepository.upsertTransactions(accountId, transactions)
    }

    fun updateTransactions(transactions: List<Transaction>) {
        transactionRepository.upsertTransactions(transactions)
    }

    fun getRawTransactionsForUser(userId: UUID): List<RawTransaction> {
        return rawTransactionRepository.getRawTransactionsForUser(userId)
    }

    private data class EnduserAgreementCacheKey(
        val userId: UUID,
        val institutionId: UUID
    )
}
