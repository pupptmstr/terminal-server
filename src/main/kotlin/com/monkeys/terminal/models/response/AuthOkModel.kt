package com.monkeys.terminal.models.response

data class AuthOkModel(
    val jwt: String,
    val location: String
) : OkModel
