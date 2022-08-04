package com.evensberget.accounting.service.institution

import com.evensberget.accounting.common.domain.Institution
import com.evensberget.accounting.connector.nordigen.NordigenConnectorService
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Service

@Service
class InstitutionService(
    private val nordigenConnector: NordigenConnectorService,
    private val repository: InstitutionRepository
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    @EventListener(ApplicationReadyEvent::class)
    fun startup() {
//        updateInstitutions()
    }

    fun updateInstitutions() {
        logger.info("Updating institutions...")
        val nordigenInstitutions = nordigenConnector.getInstitutions()
        repository.upsertInstitutions(nordigenInstitutions)
        logger.info("Institutions updated!")
    }

    fun getAll(): List<Institution> {
        return emptyList()
    }

}
