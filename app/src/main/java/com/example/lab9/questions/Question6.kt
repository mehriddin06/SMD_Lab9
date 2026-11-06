package com.example.lab9.questions.q6

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    for (i in 1..10) {
        launch {
            println("download $i started on thread ${Thread.currentThread().name}")
            delay(100)
            println("download $i finished")
        }
    }
}
