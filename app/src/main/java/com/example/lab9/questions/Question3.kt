package com.example.lab9.questions.q3

import kotlinx.coroutines.delay

suspend fun loadUser() {
    delay(100)
    println("Step 1: User loaded")
    checkPassword()
}

suspend fun checkPassword() {
    delay(100)
    println("Step 2: Password checked")
    openProfile()
}

suspend fun openProfile() {
    delay(100)
    println("Step 3: Profile opened")
}

suspend fun main() {
    loadUser()
}
