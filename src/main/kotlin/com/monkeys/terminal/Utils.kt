package com.monkeys.terminal

import com.monkeys.terminal.auth.toAuthModel
import com.monkeys.terminal.db.Users
import io.bkbn.kompendium.oas.OpenApiSpec
import io.bkbn.kompendium.oas.info.Info
import io.bkbn.kompendium.oas.server.Server
import org.jetbrains.exposed.sql.Between
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.javatime.timestampParam
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.update
import java.net.URI
import java.time.Instant

val dbName = System.getenv("POSTGRES_DB").takeUnless { it.isNullOrEmpty() }
    ?: throw IllegalStateException("No bot database's name was specified in the execution environment")
val dbUsername = System.getenv("POSTGRES_USER").takeUnless { it.isNullOrEmpty() }
    ?: throw IllegalStateException("No bot database user's name was specified in the execution environment")
val dbPassword = System.getenv("POSTGRES_PASSWORD").takeUnless { it.isNullOrEmpty() }
    ?: throw IllegalStateException("No bot database user's password was specified in the execution environment")
val baseSpec = OpenApiSpec(
    info = Info(
        title = "Terminal REST app",
        version = "1.0",
        description = "Wow isn't this cool?",
    ),
    servers = mutableListOf(
        Server(
            url = URI("http://ls-terminal.duckdns.org"),
            description = "Production instance of my API"
        )
    )
)


suspend fun isClientLogin(login: String): Boolean = dbQuery {
    val instantMinusHour = Instant.now().minusMillis(3600000)
    val instantNow = Instant.now()
    val res = Users.select(
        (Users.login eq login) and (Users.isActive eq true)
                and ( Between(Users.activeLast, timestampParam(instantMinusHour), timestampParam(instantNow)) )
    ).mapNotNull { toAuthModel(it) }.singleOrNull()
    res != null
}

suspend fun updateUserLastActive(login: String) = dbQuery {
    Users.update({Users.login eq login}) { it[activeLast] = timestampParam(Instant.now()) }
}

suspend fun getActiveUsers(): List<String> = dbQuery {
    val instantMinusHour = Instant.now().minusMillis(3600000)
    val instantNow = Instant.now()
    val sqlRes = Users.select(
        (Users.isActive eq true)
                and ( Between(Users.activeLast, timestampParam(instantMinusHour), timestampParam(instantNow)) )
    ).mapNotNull { toAuthModel(it) }
    val res = mutableListOf<String>()
    for(user in sqlRes) {
        res.add(user.login)
    }
    res
}

suspend fun killUser(userName: String) = dbQuery {
    Users.update({Users.login eq userName}) { it[isActive] = false }
}

suspend fun activateUser(userName: String)= dbQuery {
    Users.update({Users.login eq userName}) { it[isActive] = true }
}
