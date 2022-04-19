package com.monkeys.terminal.models.response

data class OkResponseModel<T> (
    override val status: String,
    override val message: T,
) : ResponseModel<T>
