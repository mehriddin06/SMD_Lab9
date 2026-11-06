package com.example.lab9.questions.q12

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun CoroutineScope.greetInBackground(name: String) = launch {
    delay(100)
    println("Hello, $name (background)")
}

suspend fun greetAll(names: List<String>) = coroutineScope {
    for (name in names) {
        launch {
            delay(100)
            println("Hello, $name (greetAll)")
        }
    }
}

fun main(): Unit = runBlocking {
    println("Calling helper 1...")
    greetInBackground("Alice")
    println("Helper 1 returned immediately")

    println("Calling helper 2...")
    greetAll(listOf("Bob", "Charlie"))
    println("Helper 2 returned after completion")
}
