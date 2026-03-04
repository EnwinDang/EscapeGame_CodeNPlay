package com.example.escapegame.ui.game1

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.escapegame.logic.BinaryPuzzle
import com.example.escapegame.theme.EscapeGameTheme
import com.example.escapegame.ui.game2.ScratchGameActivity

class BinaryGameActivity : ComponentActivity() {
    private val puzzle = BinaryPuzzle()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        puzzle.generatePuzzle()
        setContent {
            EscapeGameTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BinaryGameScreen(
                        modifier = Modifier.padding(innerPadding),
                        puzzle = puzzle,
                        onSuccess = {
                            startActivity(Intent(this, ScratchGameActivity::class.java))
                            finish()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun BinaryGameScreen(
    modifier: Modifier = Modifier,
    puzzle: BinaryPuzzle,
    onSuccess: () -> Unit = {}
) {
    var binaryText by remember { mutableStateOf(puzzle.currentBinary) }
    var userAnswer by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Decode the Binary:",
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = binaryText,
            modifier = Modifier.padding(vertical = 16.dp),
            style = MaterialTheme.typography.bodyLarge
        )

        OutlinedTextField(
            value = userAnswer,
            onValueChange = { userAnswer = it },
            label = { Text("Your Answer") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                if (puzzle.checkAnswer(userAnswer)) {
                    onSuccess()
                } else {
                    resultText = "Wrong! Try again."
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("Submit")
        }

        Button(
            onClick = {
                puzzle.generatePuzzle()
                binaryText = puzzle.currentBinary
                userAnswer = ""
                resultText = ""
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("New Puzzle")
        }

        if (resultText.isNotEmpty()) {
            Text(
                text = resultText,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BinaryGameScreenPreview() {
    val puzzle = BinaryPuzzle()
    puzzle.generatePuzzle()
    EscapeGameTheme {
        BinaryGameScreen(puzzle = puzzle)
    }
}
