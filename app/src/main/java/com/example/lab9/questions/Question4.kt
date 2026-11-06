package com.example.lab9.questions.q4

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

suspend fun loadUserData(): String {
    delay(100)
    return "User Data"
}

suspend fun fix1CallerSuspend() {
    val data = loadUserData()
    println("Fix 1 result: $data")
}

fun fix2RunBlocking() {
    runBlocking {
        val data = loadUserData()
        println("Fix 2 result: $data")
    }
}

fun main(): Unit = runBlocking {
    fix1CallerSuspend()
    fix2RunBlocking()
}
