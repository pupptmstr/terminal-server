package com.monkeys.terminal

import org.jetbrains.exposed.sql.Database
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class DbConnection {

    fun getConnection(dbName: String, user: String, password: String): Connection? {
        try {
            val dbUrl = "jdbc:postgresql://database:5432/$dbName"
            Database.connect(dbUrl, "")
            return DriverManager.getConnection(dbUrl, user, password)
        } catch (e: SQLException) {
            println("Error while connecting to DB")
            e.printStackTrace()
        }
        return null
    }

}
