package com.monkeys.terminal.models.response

data class ErrorResponseModel<T> (
    override val status: String = "Error",
    override val message: T,
) : ResponseModel<T>
