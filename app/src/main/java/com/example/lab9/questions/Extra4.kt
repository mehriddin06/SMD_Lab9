package com.example.lab9.questions.e4

import kotlinx.coroutines.delay

data class User(val id: Int, val name: String)

suspend fun loadUserById(id: Int): User {
    delay(100)
    return User(id, "User_$id")
}

suspend fun main() {
    val user = loadUserById(1)
    println("Loaded user: $user")
}
