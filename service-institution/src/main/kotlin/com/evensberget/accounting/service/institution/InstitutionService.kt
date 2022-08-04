package com.evensberget.accounting.service.institution

import com.evensberget.accounting.common.domain.EnduserAgreement
import com.evensberget.accounting.common.domain.Institution
import com.evensberget.accounting.connector.nordigen.NordigenConnectorService
import com.evensberget.accounting.service.institution.repositories.EnduserAgreementRepository
import com.evensberget.accounting.service.institution.repositories.InstitutionRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service
import java.util.*

@Service
class InstitutionService(
    private val nordigenConnector: NordigenConnectorService,
    private val institutionRepository: InstitutionRepository,
    private val enduserAgreementRepository: EnduserAgreementRepository
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @EventListener(ApplicationReadyEvent::class)
    fun startup() {
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
        val institutionNordigenId = institutionRepository.getNordigenIdForInstitution(institutionId)
        val agreement = nordigenConnector.getEnduserAgreement(institutionNordigenId)

        return enduserAgreementRepository.addEnduserAgreement(userId, agreement)
    }

}
