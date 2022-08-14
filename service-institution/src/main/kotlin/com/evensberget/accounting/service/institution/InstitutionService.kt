package com.evensberget.accounting.service.institution

import com.evensberget.accounting.common.cache.CacheUtils
import com.evensberget.accounting.common.domain.EnduserAgreement
import com.evensberget.accounting.common.domain.Institution
import com.evensberget.accounting.common.domain.Requisition
import com.evensberget.accounting.connector.nordigen.NordigenConnectorService
import com.evensberget.accounting.service.institution.repositories.EnduserAgreementRepository
import com.evensberget.accounting.service.institution.repositories.InstitutionRepository
import com.evensberget.accounting.service.institution.repositories.RequisitionRepository
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
    private val requisitionRepository: RequisitionRepository
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    private val enduserAgreementCache = Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofHours(6))
        .maximumSize(1000)
        .build<EnduserAgreementCacheKey, EnduserAgreement>()

    init {
        updateInstitutions()
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

    private data class EnduserAgreementCacheKey(
        val userId: UUID,
        val institutionId: UUID
    )
}
