package com.example.escapegame.logic

class BinaryPuzzle {

    private val words = listOf("DATA", "CODE", "SERVER", "ROBOT", "CLOUD")

    var currentWord: String = ""
        private set

    var currentBinary: String = ""
        private set

    fun generatePuzzle() {
        currentWord = words.random()
        currentBinary = wordToBinary(currentWord)
    }

    private fun wordToBinary(word: String): String {
        return word.map { char ->
            char.code.toString(2)
        }.joinToString(" ")
    }

    fun checkAnswer(answer: String): Boolean {
        return answer.trim().uppercase() == currentWord
    }
}