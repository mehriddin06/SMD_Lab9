package com.example.lab9.questions.q13

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() {
    val runBlockingResult: String = runBlocking {
        val launchJob = launch {
            delay(50)
            println("Inside launch")
        }
        println("launch returned: $launchJob")

        val asyncDeferred = async {
            delay(50)
            "Result from async"
        }
        println("async returned: $asyncDeferred")

        val scopeResult: String = coroutineScope {
            delay(50)
            "Result from coroutineScope"
        }
        println("coroutineScope returned: $scopeResult")

        "Result from runBlocking"
    }
    println("runBlocking returned: $runBlockingResult")
}
