package com.example.lab9.questions.q9

import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    val parentJob = launch {
        launch {
            try {
                delay(1000)
            } finally {
                println("Child 1 cleanup message")
            }
        }
        launch {
            try {
                delay(1000)
            } finally {
                println("Child 2 cleanup message")
            }
        }
    }
    delay(100)
    parentJob.cancelAndJoin()
    println("end")
}
