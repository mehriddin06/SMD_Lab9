package com.example.lab9.questions.q10

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    coroutineScope {
        launch {
            delay(200)
            println("Avatar loaded")
        }
        launch {
            delay(100)
            println("Settings loaded")
        }
    }
    println("Profile ready")

    launch {
        delay(100)
        println("Background coroutine done")
    }
    println("end of main")
}
