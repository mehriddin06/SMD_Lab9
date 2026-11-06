package com.example.lab9.questions.e1

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

fun main(): Unit = runBlocking {
    val timeDelay = measureTimeMillis {
        coroutineScope {
            launch { delay(300) }
            launch { delay(300) }
            launch { delay(300) }
        }
    }

    val timeSleep = measureTimeMillis {
        coroutineScope {
            launch { Thread.sleep(300) }
            launch { Thread.sleep(300) }
            launch { Thread.sleep(300) }
        }
    }

    println("Time with delay: $timeDelay ms (rounded: ${((timeDelay + 50) / 100) * 100} ms)")
    println("Time with Thread.sleep: $timeSleep ms (rounded: ${((timeSleep + 50) / 100) * 100} ms)")
}
