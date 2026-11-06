package com.example.lab9.questions.q14

import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    val job = launch {
        delay(100)
        val calculated = 6 * 7
        println("Inside launch calculated: $calculated")
    }
    println("Launch returned Job object: $job (Cannot extract result value from Job)")

    val deferred = async {
        delay(100)
        6 * 7
    }
    val answer = deferred.await()
    println("Async result via await(): $answer")
}
