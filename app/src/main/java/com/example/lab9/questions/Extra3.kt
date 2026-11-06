package com.example.lab9.questions.e3

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    launch {
        delay(100)
        println("Fixed with runBlocking")
    }
}
