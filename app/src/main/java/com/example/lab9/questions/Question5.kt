package com.example.lab9.questions.q5

import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield

suspend fun getCurrentThreadName(): String {
    yield()
    return Thread.currentThread().name
}

fun main(): Unit = runBlocking {
    println("Before call thread: ${Thread.currentThread().name}")
    val returnedThread = getCurrentThreadName()
    println("Returned thread name: $returnedThread")
}
