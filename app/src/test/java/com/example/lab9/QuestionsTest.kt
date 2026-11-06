package com.example.lab9

import org.junit.Test

class QuestionsTest {

    @Test
    fun testAllQuestions() {
        println("=== Question 1 ===")
        com.example.lab9.questions.q1.main()

        println("\n=== Question 2 ===")
        com.example.lab9.questions.q2.main()

        println("\n=== Question 3 ===")
        kotlinx.coroutines.runBlocking {
            com.example.lab9.questions.q3.main()
        }

        println("\n=== Question 4 ===")
        com.example.lab9.questions.q4.main()

        println("\n=== Question 5 ===")
        com.example.lab9.questions.q5.main()

        println("\n=== Question 6 ===")
        com.example.lab9.questions.q6.main()

        println("\n=== Question 7 ===")
        com.example.lab9.questions.q7.main()

        println("\n=== Question 8 ===")
        com.example.lab9.questions.q8.main()

        println("\n=== Question 9 ===")
        com.example.lab9.questions.q9.main()

        println("\n=== Question 10 ===")
        com.example.lab9.questions.q10.main()

        println("\n=== Question 11 ===")
        com.example.lab9.questions.q11.main()

        println("\n=== Question 12 ===")
        com.example.lab9.questions.q12.main()

        println("\n=== Question 13 ===")
        com.example.lab9.questions.q13.main()

        println("\n=== Question 14 ===")
        com.example.lab9.questions.q14.main()

        println("\n=== Question 15 ===")
        com.example.lab9.questions.q15.main()

        println("\n=== Extra 1 ===")
        com.example.lab9.questions.e1.main()

        println("\n=== Extra 2 ===")
        com.example.lab9.questions.e2.main()

        println("\n=== Extra 3 ===")
        com.example.lab9.questions.e3.main()

        println("\n=== Extra 4 ===")
        kotlinx.coroutines.runBlocking {
            com.example.lab9.questions.e4.main()
        }

        println("\n=== Extra 5 ===")
        com.example.lab9.questions.e5.main()
    }
}
