package com.monkeys.terminal.api

import com.monkeys.terminal.getActiveUsers
import com.monkeys.terminal.killUser
import com.monkeys.terminal.models.KillRequest
import java.io.File

class UserController() {

    suspend fun ls(baseLocation: String, locationToLs: String): Map<String, Boolean>? {
        val path = resolvePath(baseLocation, locationToLs)
        return if (path != null) {
            val res = getCorrectListOfFiles(path)
            res?.toSortedMap()
        } else {
            null
        }
    }

    suspend fun cd(baseLocation: String, locationToCd: String): String? {
        val path = resolvePath(baseLocation, locationToCd)
        return path?.canonicalPath
    }

    suspend fun who(): List<String> = getActiveUsers()


    suspend fun kill(killRequest: KillRequest) {
        killUser(killRequest.userToKill)
    }

    suspend fun logout(userName: String) {
        killUser(userName)
    }

    private suspend fun getCorrectListOfFiles(dir: File): Map<String, Boolean>? {
        val list = dir.listFiles()
        return list?.toList()?.associateBy({ it.name }, { it.isDirectory })
    }

    private suspend fun resolvePath(base: String, changed: String): File? {
        var path = File(changed)
        if (!path.isAbsolute) {
            path = File(base, changed)
        }
        return if (!path.isDirectory) {
            null
        } else {
            path
        }

    }
}