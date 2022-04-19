package com.monkeys.terminal.models

import io.ktor.server.auth.*

data class AuthModel(
    val login: String,
    val password: String,
    val role: String
) : Principal