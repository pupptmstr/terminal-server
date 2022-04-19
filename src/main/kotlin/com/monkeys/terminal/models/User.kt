package com.monkeys.terminal.models

import java.sql.Timestamp

data class User(
    val login: String,
    val password: String,
    val role: String,
    val isActive: Boolean,
    val activeLast: Timestamp
)
