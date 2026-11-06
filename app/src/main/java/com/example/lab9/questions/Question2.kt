package com.example.lab9.questions.q2

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun runWithDelay() = runBlocking {
    println("--- With delay ---")
    launch {
        println("espresso start")
        delay(300)
        println("espresso end")
    }
    launch {
        println("tea start")
        delay(100)
        println("tea end")
    }
    launch {
        println("latte start")
        delay(200)
        println("latte end")
    }
}

fun runWithSleep() = runBlocking {
    println("--- With Thread.sleep ---")
    launch {
        println("espresso start")
        Thread.sleep(300)
        println("espresso end")
    }
    launch {
        println("tea start")
        Thread.sleep(100)
        println("tea end")
    }
    launch {
        println("latte start")
        Thread.sleep(200)
        println("latte end")
    }
}

fun main() {
    runWithDelay()
    runWithSleep()
}
