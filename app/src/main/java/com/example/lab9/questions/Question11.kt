package com.example.lab9.questions.q11

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun CoroutineScope.startCounter(name: String, max: Int): Job {
    return launch {
        for (i in 1..max) {
            println("$name: $i")
            delay(100)
        }
    }
}

fun main(): Unit = runBlocking {
    coroutineScope {
        startCounter("Counter A", 3)
        startCounter("Counter B", 2)
        println("launched")
    }
}
