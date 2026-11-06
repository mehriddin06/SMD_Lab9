package com.example.lab9.questions.q1

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    launch {
        delay(1000)
        println("Coffee ready")
    }
    println("Order accepted")
}
