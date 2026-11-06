package com.example.lab9.questions.q8

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    val parentJob = launch {
        launch {
            delay(200)
            println("child done")
        }
        println("parent body done")
    }
    parentJob.join()
    println("report completed")
}
