package com.example.lab9.questions.q15

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    println("Start")
    coroutineScope {
        launch {
            delay(300)
            println("Export X finished")
        }
        launch {
            delay(100)
            println("Export Y finished")
        }
        launch {
            delay(200)
            println("Export Z finished")
        }
    }
    println("All done")
}
