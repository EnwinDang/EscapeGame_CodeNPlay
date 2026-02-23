package com.example.escapegame.ui.game1

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.escapegame.R
import com.example.escapegame.logic.BinaryPuzzle

class BinaryGameActivity : AppCompatActivity() {

    private lateinit var puzzle: BinaryPuzzle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_binary_game)

        puzzle = BinaryPuzzle()

        val tvBinary = findViewById<TextView>(R.id.tvBinary)
        val etAnswer = findViewById<EditText>(R.id.etAnswer)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        val btnNewPuzzle = findViewById<Button>(R.id.btnNewPuzzle)
        val tvResult = findViewById<TextView>(R.id.tvResult)

        generateNewPuzzle(tvBinary, tvResult, etAnswer)

        btnSubmit.setOnClickListener {
            val userAnswer = etAnswer.text.toString()

            if (puzzle.checkAnswer(userAnswer)) {
                tvResult.text = "Correct!"
            } else {
                tvResult.text = "Wrong! Try again."
            }
        }

        btnNewPuzzle.setOnClickListener {
            generateNewPuzzle(tvBinary, tvResult, etAnswer)
        }
    }

    private fun generateNewPuzzle(
        tvBinary: TextView,
        tvResult: TextView,
        etAnswer: EditText
    ) {
        puzzle.generatePuzzle()
        tvBinary.text = puzzle.currentBinary
        tvResult.text = ""
        etAnswer.text.clear()
    }
}