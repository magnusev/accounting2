package com.evensberget.accounting

import com.evensberget.accounting.connector.nordigen.NordigenConnectorService
import com.evensberget.accounting.service.institution.InstitutionService
import com.evensberget.accounting.service.user.UserService
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class AccountingApplication(
    private val userService: UserService,
    private val connectorService: NordigenConnectorService,
    private val institutionService: InstitutionService
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        val user = userService.get("magnus.evensberget@gmail.com")
//        val institution = institutionService.getInstitutionByName("Danske Bank Private")
//
//        val enduserAgreement = institutionService.enduserAgreement(
//            userId = user.id,
//            institutionId = institution.id
//        )

//        val test = connectorService.getEnduserAgreement(UUID.fromString("7c32850f-f391-4e46-b34e-cae33db9cedd"))
//        val test = connectorService.getEnduserAgreement(UUID.fromString("5341f886-cff8-443f-abdb-cc9b811da52a"))
//        val test = connectorService.getAllEnduserAgreements()

//        val test = connectorService.getAllRequisitions()
//        val test = connectorService.getRequisition(UUID.fromString("ea3d4301-bee4-4496-b073-ac23e70096ea"))
//        connectorService.deleteRequisition(UUID.fromString("ea3d4301-bee4-4496-b073-ac23e70096ea"))
//        connectorService.deleteEnduserAgreement(UUID.fromString("5341f886-cff8-443f-abdb-cc9b811da52a"))
//        institutionService.createRequisition(user.id, enduserAgreement.id)

//        val requisistion = institutionService.getRequisition(UUID.fromString("890caafe-5e3f-4fc0-b249-0159cb863079"))

//        val updatedRequisistion = institutionService.updateRequisition(UUID.fromString("890caafe-5e3f-4fc0-b249-0159cb863079"))
//        val accounts = institutionService.addAccounts(user.id, requisistion.accounts.map { UUID.fromString(it) })
//        connectorService.getBalances(UUID.fromString("8472c8ca-ce41-4d0a-969e-b839ca690d9c"))
//        val account = connectorService.getAccount(UUID.fromString(updatedRequisistion.accounts.first()))
//        val accounts = institutionService.getAccounts(user.id)
//
//        accounts.forEach { account ->
//            institutionService.updateTransactions(accountId = account.id)
//        }


        println()
    }
}

fun main(args: Array<String>) {
    runApplication<AccountingApplication>(*args)
}
