package com.evensberget.accounting

import com.evensberget.accounting.service.user.UserService
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class AccountingApplication(
    private val userService: UserService
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        val user = userService.get("magnus.evensberget@gmail.com")
        println()
    }
}

fun main(args: Array<String>) {
    runApplication<AccountingApplication>(*args)
}
