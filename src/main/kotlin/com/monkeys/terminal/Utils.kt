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
//{
//    DbConnection().getConnection(dbName, dbUsername, dbPassword).use { connection ->
//        val tzUTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
//        val instantMinusHour = Instant.now().minusMillis(3600000)
//        val timestampMinusHour = Timestamp.from(instantMinusHour)
//        val ps = connection!!.prepareStatement(
//            "SELECT * FROM users WHERE (LOGIN=? AND is_active=true AND (active_last, active_last) OVERLAPS (?, INTERVAL '1 hour'));"
//        ).apply {
//            setString(1, login)
//            setTimestamp(2, timestampMinusHour, tzUTC)
//        }
//
//        val resSet =
//            ps.executeQuery()
//        return resSet.next()
//    }
//}

suspend fun updateUserLastActive(login: String) = dbQuery {
    Users.update({Users.login eq login}) { it[activeLast] = timestampParam(Instant.now()) }
}
//{
//    DbConnection().getConnection(dbName, dbUsername, dbPassword).use { connection ->
//        val tzUTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
//        val instant = Instant.now()
//        val timestampNow = Timestamp.from(instant)
//        val ps = connection!!.prepareStatement("UPDATE users SET active_last=? WHERE LOGIN='$login';")
//        ps.setTimestamp(1, timestampNow, tzUTC)
//        ps.execute()
//    }
//}

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
//{
//    DbConnection().getConnection(dbName, dbUsername, dbPassword).use { connection ->
//        val tzUTC = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
//        val instantMinusHour = Instant.now().minusMillis(3600000)
//        val timestampMinusHour = Timestamp.from(instantMinusHour)
//        val ps = connection!!.prepareStatement(
//            "SELECT * FROM users WHERE (is_active=true AND (active_last, active_last) OVERLAPS (?, INTERVAL '1 hour'));"
//        ).apply {
//            setTimestamp(1, timestampMinusHour, tzUTC)
//        }
//
//        val resSet =
//            ps.executeQuery()
//        val res = mutableListOf<String>()
//        while (resSet.next()) {
//            res.add(resSet.getString("login"))
//        }
//        return res
//    }
//}

suspend fun killUser(userName: String) = dbQuery {
    Users.update({Users.login eq userName}) { it[isActive] = false }
}
//{
//    DbConnection().getConnection(dbName, dbUsername, dbPassword).use { connection ->
//        val ps = connection!!.prepareStatement("UPDATE users SET is_active=false WHERE LOGIN=?;").apply {
//            setString(1, userName)
//        }
//        ps.execute()
//    }
//}

suspend fun activateUser(userName: String)= dbQuery {
    Users.update({Users.login eq userName}) { it[isActive] = true }
}
//{
//    DbConnection().getConnection(dbName, dbUsername, dbPassword).use { connection ->
//        val statement = connection!!.prepareStatement("UPDATE users SET is_active=true WHERE LOGIN=?;").apply {
//            setString(1, userName)
//        }
//        statement.execute()
//    }
//}