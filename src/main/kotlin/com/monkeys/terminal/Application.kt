package com.monkeys.terminal

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.google.gson.FieldNamingPolicy
import com.monkeys.terminal.api.UserController
import com.monkeys.terminal.api.api
import com.monkeys.terminal.auth.AuthRepo
import com.monkeys.terminal.auth.auth
import com.monkeys.terminal.models.AuthModel
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.defaultheaders.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import org.slf4j.event.Level

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.configure() {

    install(ContentNegotiation) {
        gson {
            setPrettyPrinting()
            setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
            serializeNulls()
        }
    }


    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
    }

    install(DefaultHeaders) {
        header("X-Engine", "Ktor") // will send this header with each response
    }


    install(Authentication) {
        jwt("validate") {
            verifier(
                JWT
                    .require(Algorithm.HMAC256("[/aBJ}S!.c:{u3]"))
                    .withIssuer("pupptmstr")
                    .build()
            )
            validate { credential ->
                val login = credential.payload.getClaim("Login").asString()
                val role = credential.payload.getClaim("Role").asString()
                if (login != null && role != null) {
                    AuthModel(login, "hidden", role)
                } else {
                    null
                }
            }
        }
    }

    DatabaseFactory.init()

    val authRepo = AuthRepo()
    val userController = UserController()


    routing {
        route("/api/v1") {
            route("") {
                auth(authRepo, userController)
                api(userController)
            }
        }
    }


}
