package com.example.lab9.questions.e2

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    coroutineScope {
        launch {
            launch {
                delay(200)
                println("grandchild done")
            }
            println("child 1 done")
        }
        launch {
            delay(100)
            println("child 2 done")
        }
    }
    println("scope done")
}
