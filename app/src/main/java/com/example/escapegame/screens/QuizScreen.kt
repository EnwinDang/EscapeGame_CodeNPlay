package com.example.escapegame.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import com.example.escapegame.theme.BrandGreen
import com.example.escapegame.theme.MatrixRed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.escapegame.R
import com.example.escapegame.logic.AgentUiStyle
import com.example.escapegame.theme.MissionControlBackground

@Composable
fun QuizScreen(
    question: String,
    options: List<String>,
    correctIndex: Int,
    explanation: String,
    uiStyle: AgentUiStyle,
    onContinue: () -> Unit,
    onHome: () -> Unit,
) {
    BackHandler(enabled = true) { /* back disabled during game */ }

    // Shuffle options once on first composition, track where the correct answer ends up
    val (shuffledOptions, shuffledCorrectIndex) = remember {
        val indexed = options.mapIndexed { i, opt -> i to opt }.shuffled()
        val newCorrectIndex = indexed.indexOfFirst { it.first == correctIndex }
        indexed.map { it.second } to newCorrectIndex
    }

    var selectedIndex by remember { mutableStateOf<Int?>(null) }
    val answered = selectedIndex != null
    val buttonHeight = if (uiStyle == AgentUiStyle.JUNIOR) 72.dp else 64.dp

    MissionControlBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(40.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.quiz_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = question,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                shuffledOptions.forEachIndexed { index, option ->
                    val isSelected = selectedIndex == index
                    val isCorrect = index == shuffledCorrectIndex

                    if (answered && !isSelected && !isCorrect) {
                        OutlinedButton(
                            onClick = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .height(buttonHeight),
                            enabled = false
                        ) {
                            Text(option, style = MaterialTheme.typography.titleSmall)
                        }
                    } else {
                        val buttonColors = if (answered) {
                            when {
                                isSelected && isCorrect -> ButtonDefaults.buttonColors(
                                    disabledContainerColor = BrandGreen,
                                    disabledContentColor = Color.Black
                                )
                                isSelected -> ButtonDefaults.buttonColors(
                                    disabledContainerColor = MatrixRed,
                                    disabledContentColor = Color.White
                                )
                                else -> ButtonDefaults.buttonColors(
                                    disabledContainerColor = BrandGreen,
                                    disabledContentColor = Color.Black
                                )
                            }
                        } else ButtonDefaults.buttonColors()

                        Button(
                            onClick = { if (!answered) selectedIndex = index },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .height(buttonHeight),
                            enabled = !answered,
                            colors = buttonColors
                        ) {
                            Text(option, style = MaterialTheme.typography.titleSmall)
                        }
                    }
                }

                if (answered) {
                    Spacer(modifier = Modifier.height(24.dp))

                    val isCorrect = selectedIndex == shuffledCorrectIndex

                    if (isCorrect) {
                        Text(
                            text = stringResource(R.string.quiz_correct),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.secondary,
                            textAlign = TextAlign.Center
                        )
                    } else {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.errorContainer
                            )
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = stringResource(R.string.quiz_wrong_title),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = explanation,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onContinue,
                        modifier = Modifier.fillMaxWidth().height(60.dp)
                    ) {
                        Text(stringResource(R.string.btn_continue), style = MaterialTheme.typography.titleMedium)
                    }
                }
            }

            HomeButton(
                onHome = onHome,
                modifier = Modifier.align(Alignment.TopStart).padding(16.dp)
            )
        }
    }
}
