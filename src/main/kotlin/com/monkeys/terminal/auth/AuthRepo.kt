package com.monkeys.terminal.auth

import com.monkeys.terminal.*
import com.monkeys.terminal.db.Users
import com.monkeys.terminal.models.AuthModel
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

class AuthRepo {

    suspend fun signIn(login: String, password: String): AuthModel? = dbQuery {
        try {
            Users.select(
                (Users.login eq login) and (Users.password eq password)
            )
                .mapNotNull { toAuthModel(it) }
                .singleOrNull()
        } catch (e: Exception) {
            null
        }
    }
//    {
//        DbConnection().getConnection(dbName, dbUsername, dbPassword).use { connection ->
//            val ps = connection!!.prepareStatement("SELECT * FROM users WHERE (LOGIN=? AND PASSWORD=?);").apply {
//                setString(1, login)
//                setString(2, password)
//            }
//            val resSet = ps.executeQuery()
//            return if (resSet.next()) {
//                val role = resSet.getString("role")
//                AuthModel(login, "", role)
//            } else
//                null
//
//        }
//    }

    suspend fun signUp(login: String, password: String, role: String): Boolean = dbQuery {
        try {
            Users.insert {
                it[Users.login] = login
                it[Users.password] = password
                it[Users.role] = role
            }
            true
        } catch (e: Exception) {
            println("Some problems with registration new user $login")
            false
        }
    }
//    {
//        DbConnection().getConnection(dbName, dbUsername, dbPassword).use { connection ->
//            return try {
//                val ps =
//                    connection!!.prepareStatement("INSERT INTO users(LOGIN, PASSWORD, ROLE) VALUES (?, ?, ?);").apply {
//                        setString(1, login)
//                        setString(2, password)
//                        setString(3, role)
//                    }
//                ps.execute()
//                true
//            } catch (e: Exception) {
//                println("Some problems with registration new user $login")
//                false
//            }
//        }
//    }
}

fun toAuthModel(row: ResultRow): AuthModel =
    AuthModel(
        login = row[Users.login],
        password = "",
        role = row[Users.password]
    )
