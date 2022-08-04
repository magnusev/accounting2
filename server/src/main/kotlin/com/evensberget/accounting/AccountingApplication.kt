package com.evensberget.accounting

import com.evensberget.accounting.service.institution.InstitutionService
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class AccountingApplication(
    private val institutionService: InstitutionService
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        val institutions = institutionService.getAll()
        println()
    }
}

fun main(args: Array<String>) {
    runApplication<AccountingApplication>(*args)
}
