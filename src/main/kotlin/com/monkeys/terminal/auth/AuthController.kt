package com.monkeys.terminal.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.monkeys.terminal.activateUser
import com.monkeys.terminal.models.AuthModel
import com.monkeys.terminal.models.response.AuthOkModel
import com.monkeys.terminal.updateUserLastActive
import java.io.File

class AuthController(private val repo: AuthRepo) {
    suspend fun signIn(model: AuthModel): AuthOkModel? {
        val res = repo.signIn(model.login, model.password)
        return if (res != null) {
            val jwt = createJwt(res)
            activateUser(model.login)
            updateUserLastActive(model.login)
            val location = File(System.getProperty("user.dir")).absolutePath
            AuthOkModel(jwt, location)
        } else {
            null
        }
    }

    suspend fun signUp(model: AuthModel): Boolean = repo.signUp(model.login, model.password, "user")

    private fun createJwt(model: AuthModel): String = JWT.create()
        .withSubject("Authentication")
        .withIssuer("pupptmstr")
        .withClaim("Login", model.login)
        .withClaim("Role", model.role)
        .sign(Algorithm.HMAC256("[/aBJ}S!.c:{u3]"))
}