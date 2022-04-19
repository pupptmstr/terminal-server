package com.monkeys.terminal.db

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp
import java.sql.Timestamp

object Users: Table("users") {
    val login: Column<String> = text("login")
    val password: Column<String> = text("password")
    val role: Column<String> = text("role")
    val isActive: Column<Boolean> = bool("is_active").default(false)
    val activeLast = timestamp("active_last")
    override val primaryKey: PrimaryKey
        get() = PrimaryKey(login)
}