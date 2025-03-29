package api

import dataclasses.Query
import dataclasses.User

actual suspend fun login(email: String, password: String) {
}

actual fun register(user: User) {
}

actual fun getHistory(): List<Query> {
    TODO("Not yet implemented")
}

actual fun login(email: String, password: String): Boolean {
    TODO("Not yet implemented")
}