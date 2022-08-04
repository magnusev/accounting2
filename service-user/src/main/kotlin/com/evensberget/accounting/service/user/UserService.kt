package com.evensberget.accounting.service.user

import com.evensberget.accounting.common.domain.User
import org.springframework.stereotype.Service

@Service
class UserService(
    private val repository: UserRepository
) {
    init {
        insertData()
    }

    fun get(email: String): User {
        return repository.get(email)
            ?: throw IllegalArgumentException("User $email does not exist")
    }

    private fun insertData() {
        repository.upsert("magnus.evensberget@gmail.com")
    }

}
