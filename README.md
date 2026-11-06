# Practical Work: Introduction to Kotlin Coroutines

**Date:** 5 Nov 2026  
**Project:** Kotlin / Coroutines Practical Questions (`kotlinx-coroutines-core 1.10.2`)  

---

## Core Concept: Suspending vs. Blocking

```kotlin
/**
 * DIFFERENCE BETWEEN SUSPENDING AND BLOCKING IN KOTLIN COROUTINES:
 *
 * 1. Blocking (e.g., Thread.sleep(ms), blocking I/O, runBlocking on main thread):
 *    - Halts the underlying OS thread completely.
 *    - While blocked, the thread cannot execute any other work or coroutine assigned to it.
 *    - In UI frameworks (like Android), blocking the main thread causes UI freezes, jank,
 *      and ANR (Application Not Responding) errors.
 *
 * 2. Suspending (e.g., delay(ms), yield(), withContext, suspending I/O):
 *    - Pauses the execution of the current coroutine without stopping or locking the underlying thread.
 *    - The underlying thread is immediately freed (unblocked) to pick up and process other scheduled
 *      coroutines or handle system events.
 *    - When the asynchronous/delay operation finishes, the suspended coroutine is resumed on an available thread.
 */
```

---

## Question 1: First coroutine

### Context
A cafe app must confirm the order at once, while the coffee itself needs one second to prepare. The confirmation must not wait for the coffee.

### Task Code
```kotlin
package com.example.lab9.questions.q1

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    launch {
        delay(1000)
        println("Coffee ready")
    }
    println("Order accepted")
}
```

### Program Output
```text
Order accepted
Coffee ready
```

### Written Explanation
`launch` creates and schedules a new coroutine concurrently. Because `delay(1000)` inside the launched coroutine suspends it for 1 second without blocking the main thread, execution in `runBlocking` immediately continues down to print `"Order accepted"`. `"Coffee ready"` is printed only after the 1-second delay completes.

---

## Question 2: Three baristas

### Context
Three baristas make drinks at the same time on one thread: espresso needs 300 ms, tea 100 ms and latte 200 ms. A teammate says it is the same if you use sleep instead of suspension.

### Task Code
```kotlin
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
```

### Program Output
```text
--- With delay ---
espresso start
tea start
latte start
tea end
latte end
espresso end
--- With Thread.sleep ---
espresso start
espresso end
tea start
tea end
latte start
latte end
```

### Written Explanation
With `delay`, the thread suspends the active coroutine and is freed to interleave execution with other scheduled coroutines; with `Thread.sleep`, the single thread is completely blocked by the active coroutine, preventing all other coroutines on that thread from executing until sleeping completes.

---

## Question 3: Chain of suspend functions

### Context
A login flow has three steps: load the user, check the password and open the profile. Each step takes some time and must run strictly after the previous one.

### Task Code
```kotlin
package com.example.lab9.questions.q3

import kotlinx.coroutines.delay

suspend fun loadUser() {
    delay(100)
    println("Step 1: User loaded")
    checkPassword()
}

suspend fun checkPassword() {
    delay(100)
    println("Step 2: Password checked")
    openProfile()
}

suspend fun openProfile() {
    delay(100)
    println("Step 3: Profile opened")
}

suspend fun main() {
    loadUser()
}
```

### Program Output
```text
Step 1: User loaded
Step 2: Password checked
Step 3: Profile opened
```

### Written Explanation
Suspending functions execute sequentially within a single coroutine unless explicitly launched into concurrent coroutine builders (`launch`/`async`); thus, suspension pauses execution at each step without reordering sequential lines.

---

## Question 4: Calling suspend from normal code

### Context
A teammate wrote a normal function that calls a suspend function to load a user, and the project does not compile.

### Compile Error Message
`Suspend function 'loadUserData' should be called only from a coroutine or another suspend function`

### Task Code
```kotlin
package com.example.lab9.questions.q4

import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

suspend fun loadUserData(): String {
    delay(100)
    return "User Data"
}

// Fix 1: Make caller a suspend function
suspend fun fix1CallerSuspend() {
    val data = loadUserData()
    println("Fix 1 result: $data")
}

// Fix 2: Bridge with runBlocking builder
fun fix2RunBlocking() {
    runBlocking {
        val data = loadUserData()
        println("Fix 2 result: $data")
    }
}

fun main(): Unit = runBlocking {
    fix1CallerSuspend()
    fix2RunBlocking()
}
```

### Program Output
```text
Fix 1 result: User Data
Fix 2 result: User Data
```

### Written Explanation
We **never** use `runBlocking` (Fix 2) on the Android UI thread because `runBlocking` halts/blocks the main UI thread until all internal coroutines complete, causing UI freezes, touch unresponsiveness, and ANR (Application Not Responding) crashes.

---

## Question 5: Suspend is not a thread

### Context
A junior developer believes that marking a function `suspend` moves its work to a background thread.

### Task Code
```kotlin
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
```

### Program Output
```text
Before call thread: main
Returned thread name: main
```

### Written Explanation
- **Disproven:** Marking a function `suspend` does not automatically switch execution to a background thread; both caller and suspend function executed on the `main` thread.
- **Hidden Parameter:** The compiler adds a hidden parameter of type `Continuation<T>` (`kotlin.coroutines.Continuation`) to every suspend function signature for managing suspension state and continuation callbacks.

---

## Question 6: Ten downloads, one thread

### Context
A download manager has ten files, but the app is allowed to use only the main thread. All downloads must make progress without waiting for each other.

### Task Code
```kotlin
package com.example.lab9.questions.q6

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    for (i in 1..10) {
        launch {
            println("download $i started on thread ${Thread.currentThread().name}")
            delay(100)
            println("download $i finished")
        }
    }
}
```

### Program Output
```text
download 1 started on thread main
download 2 started on thread main
download 3 started on thread main
download 4 started on thread main
download 5 started on thread main
download 6 started on thread main
download 7 started on thread main
download 8 started on thread main
download 9 started on thread main
download 10 started on thread main
download 1 finished
download 2 finished
download 3 finished
download 4 finished
download 5 finished
download 6 finished
download 7 finished
download 8 finished
download 9 finished
download 10 finished
```

### Written Explanation
This is **concurrency**, not parallelism. Concurrency is about dealing with multiple tasks at once by interleaving execution on a single thread (enabled by non-blocking `delay`), whereas parallelism requires executing tasks simultaneously across multiple physical CPU threads/cores.

---

## Question 7: Order of launch

### Context
A screen prints a loading message, starts a background step and then continues with its own work. The team lead is surprised that the background step does not run at the moment it is started.

### Task Code
```kotlin
package com.example.lab9.questions.q7

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    println("1")
    launch {
        println("2")
        delay(100)
        println("3")
    }
    println("4")
    delay(50)
    println("5")
}
```

### Program Output
```text
1
4
2
5
3
```

### Written Explanation
When `launch` is called in `runBlocking`, the new coroutine is added to the event loop execution queue. The main coroutine continues running synchronously until it hits its own suspension point (`delay(50)`), at which point the main thread yields and the dispatcher runs the queued coroutine (`"2"`).

---

## Question 8: Parent waits for child

### Context
A report task starts a sub task that compresses attachments for 200 ms. The report must be marked completed only after the sub task is finished, even if the report body itself finishes earlier.

### Task Code
```kotlin
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
```

### Program Output
```text
parent body done
child done
report completed
```

### Written Explanation
**Structured Concurrency Rule:** A parent coroutine scope/job will not complete until all of its child coroutines have completed execution. Therefore, `parentJob.join()` waits for both the parent body and the child coroutine to finish.

---

## Question 9: Screen closed, requests cancelled

### Context
A user closes a screen while two network requests are still running. Both requests must stop at once and must print a cleanup message.

### Task Code
```kotlin
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
```

### Program Output
```text
Child 1 cleanup message
Child 2 cleanup message
end
```

### Written Explanation
In structured concurrency, cancelling a parent job automatically propagates cancellation recursively down to all of its children. The suspended children throw `CancellationException`, which executes their `finally` blocks for resource cleanup.

---

## Question 10: Profile ready

### Context
A profile screen loads avatar (200 ms) and settings (100 ms) at the same time. The message "Profile ready" must appear only after both are loaded.

### Task Code
```kotlin
package com.example.lab9.questions.q10

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    coroutineScope {
        launch {
            delay(200)
            println("Avatar loaded")
        }
        launch {
            delay(100)
            println("Settings loaded")
        }
    }
    println("Profile ready")

    launch {
        delay(100)
        println("Background coroutine done")
    }
    println("end of main")
}
```

### Program Output
```text
Settings loaded
Avatar loaded
Profile ready
end of main
Background coroutine done
```

### Written Explanation
`coroutineScope` is a suspending scope builder that suspends the caller until all coroutines launched within its block complete. In contrast, `launch` starts a fire-and-forget coroutine asynchronously and returns immediately, allowing the outer coroutine to print `"end of main"` before the background coroutine finishes.

---

## Question 11: Reusable counter

### Context
Several screens start the same kind of ticking counter, and the team wants one reusable function instead of copying the launch code. The function must return at once and the new coroutine must belong to the caller scope.

### Task Code
```kotlin
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
```

### Program Output
```text
launched
Counter A: 1
Counter B: 1
Counter A: 2
Counter B: 2
Counter A: 3
```

### Written Explanation
The `CoroutineScope` receiver is required so that `launch` attaches the new child coroutine to the caller's coroutine lifecycle and context, enforcing structured concurrency and lifecycle management.

---

## Question 12: Returns at once or waits

### Context
The team needs two helpers for greetings: one that starts a greeter in the background and returns immediately, and another that greets several people and returns only when all greetings are printed.

### Task Code
```kotlin
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
```

### Program Output
```text
Calling helper 1...
Helper 1 returned immediately
Calling helper 2...
Hello, Bob (greetAll)
Hello, Charlie (greetAll)
Helper 2 returned after completion
Hello, Alice (background)
```

### Written Explanation
Helper 1 (`CoroutineScope.greetInBackground`) starts a background coroutine with `launch` and returns the `Job` object immediately. Helper 2 (`suspend fun greetAll`) uses `coroutineScope` and suspends the caller until all greetings finish printing before returning.

---

## Question 13: Five builders

### Context
A new student mixes up the builders and does not know what each one returns or whether it blocks the thread.

### Task Code
```kotlin
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
```

### Program Output
```text
launch returned: StandaloneCoroutine{Active}@...
async returned: DeferredCoroutine{Active}@...
Inside launch
coroutineScope returned: Result from coroutineScope
runBlocking returned: Result from runBlocking
```

### Cheat Sheet: 5 Coroutine Builders

| Builder | Return Value | Blocks Thread? | Waits for Children? | Description |
| :--- | :--- | :--- | :--- | :--- |
| `launch` | `Job` | No | Yes (as parent) | Starts a fire-and-forget background coroutine without returning a value. |
| `async` | `Deferred<T>` | No | Yes (as parent) | Starts a concurrent coroutine that computes and returns a result value `T`. |
| `runBlocking` | Result `T` | **Yes** | Yes | Bridges non-coroutine blocking code to coroutines by blocking the current thread. |
| `coroutineScope` | Result `T` | No (suspends) | Yes | Grouping scope builder; suspends caller until all launched child coroutines complete. |
| `withContext` | Result `T` | No (suspends) | Yes | Suspends caller and switches execution `CoroutineContext`/dispatcher (e.g., `Dispatchers.IO`). |

---

## Question 14: Job or Deferred

### Context
A developer needs the result of a calculation (six times seven), started it with `launch` and cannot get the value out of it.

### Task Code
```kotlin
package com.example.lab9.questions.q14

import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    val job = launch {
        delay(100)
        val calculated = 6 * 7
        println("Inside launch calculated: $calculated")
    }
    println("Launch returned Job object: $job (Cannot extract result value from Job)")

    val deferred = async {
        delay(100)
        6 * 7
    }
    val answer = deferred.await()
    println("Async result via await(): $answer")
}
```

### Program Output
```text
Launch returned Job object: StandaloneCoroutine{Active}@... (Cannot extract result value from Job)
Inside launch calculated: 42
Async result via await(): 42
```

### Written Explanation
- `Job` represents a background task handle (controls lifecycle, cancellation, `join()`), but has no return value.
- `Deferred<T>` extends `Job` and represents a future value result of type `T`, accessed via the suspending function `await()`.

---

## Question 15: Three exports in a scope

### Context
A report exports three files at the same time. Export X takes 300 ms, Y takes 100 ms and Z takes 200 ms. The message "All done" must come after all three exports and nothing may be joined by hand.

### Task Code
```kotlin
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
```

### Program Output
```text
Start
Export Y finished
Export Z finished
Export X finished
All done
```

### Written Explanation
`coroutineScope` automatically waits for all three launched child coroutines to complete without requiring manual `join()` calls. Since Y finishes in 100 ms, Z in 200 ms, and X in 300 ms, `"All done"` is printed strictly after all exports finish.

---

## Extra Question 1: Delay or sleep: measure it

### Context
A project manager claims that three waits of 300 ms always cost 900 ms, no matter how they are written.

### Task Code
```kotlin
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
```

### Program Output
```text
Time with delay: 312 ms (rounded: 300 ms)
Time with Thread.sleep: 915 ms (rounded: 900 ms)
```

### Written Explanation
With `delay`, all three coroutines suspend non-blockingly, allowing the single thread to interleave starting all three tasks so they wait concurrently in ~300 ms total time. With `Thread.sleep`, each call blocks the single thread completely for 300 ms, forcing sequential execution ($3 \times 300\text{ ms} = 900\text{ ms}$).

---

## Extra Question 2: Three levels of coroutines

### Context
A scope starts two children, and one of them starts a grandchild. You must predict when each message appears and when the scope is finished.

### Task Code
```kotlin
package com.example.lab9.questions.e2

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    coroutineScope {
        launch {
            launch {
                delay(200)
                println("grandchild done")
            }
            println("child 1 done")
        }
        launch {
            delay(100)
            println("child 2 done")
        }
    }
    println("scope done")
}
```

### Program Output
```text
child 1 done
child 2 done
grandchild done
scope done
```

### Coroutine Tree Diagram
```text
coroutineScope (Parent Scope)
├── Child Coroutine 1
│   └── Grandchild Coroutine (delay 200ms)
└── Child Coroutine 2 (delay 100ms)
```

### Written Explanation
Child 1 starts and immediately launches the Grandchild coroutine before printing `"child 1 done"`. Child 2 finishes its 100 ms delay next (`"child 2 done"`). The Grandchild finishes its 200 ms delay (`"grandchild done"`). `coroutineScope` waits for all descendants (including grandchildren) to complete before resuming, so `"scope done"` is printed last.

---

## Extra Question 3: No scope, no launch

### Context
A new teammate writes a plain main function and calls the launch builder directly. The compiler refuses it.

### Compile Error Message
`Unresolved reference: launch` (or `Unresolved reference: CoroutineScope` because `launch` requires a `CoroutineScope` receiver context).

### Task Code
```kotlin
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
```

### Program Output
```text
Fixed with runBlocking
```

### Written Explanation
`runBlocking` bridges non-coroutine blocking code to coroutines by creating a top-level `CoroutineScope` and blocking the current thread until its block and all child coroutines finish.

---

## Extra Question 4: What the compiler sees

### Context
In an interview you are asked what the compiler generates for a suspend function that loads a user by id and returns a user.

### Task Code
```kotlin
package com.example.lab9.questions.e4

import kotlinx.coroutines.delay

data class User(val id: Int, val name: String)

suspend fun loadUserById(id: Int): User {
    delay(100)
    return User(id, "User_$id")
}

suspend fun main() {
    val user = loadUserById(1)
    println("Loaded user: $user")
}
```

### Decompiled Java Method Signature
```java
@Nullable
public static final Object loadUserById(int id, @NotNull Continuation<? super User> $completion)
```

### Written Explanation
- **Extra Parameter:** `Continuation<? super User> $completion`
- **Return Type:** `Object` (returns either the actual `User` or the special marker `COROUTINE_SUSPENDED` if suspended)
- **Parameter Purpose:** The `Continuation` parameter holds the execution state machine, local variable state, suspension point label, and callback (`resumeWith`) used to resume execution when the asynchronous work completes.

---

## Extra Question 5: Greet everybody

### Context
A greeting service gets a list of names and must greet them one by one with growing pauses, but it must report "after" only when the last greeting is done.

### Task Code
```kotlin
package com.example.lab9.questions.e5

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

suspend fun greetEverybody(names: List<String>) = coroutineScope {
    for ((index, name) in names.withIndex()) {
        val i = index + 1
        launch {
            delay(i * 100L)
            println("Hello, $name")
        }
    }
}

fun main(): Unit = runBlocking {
    println("before")
    greetEverybody(listOf("Ada", "Linus", "Grace"))
    println("after")
}
```

### Program Output
```text
before
Hello, Ada
Hello, Linus
Hello, Grace
after
```

### Written Explanation
`greetEverybody` wraps its work in `coroutineScope`, which suspends until all launched child coroutines (Ada at 100 ms, Linus at 200 ms, Grace at 300 ms) finish execution. Control returns to `main` only after the scope finishes, ensuring `"after"` is printed last.
