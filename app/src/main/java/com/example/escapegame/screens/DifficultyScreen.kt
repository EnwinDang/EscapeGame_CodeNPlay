package com.example.escapegame.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.escapegame.R
import com.example.escapegame.theme.BrandBlue
import com.example.escapegame.theme.BrandYellow
import com.example.escapegame.theme.EscapeGameTheme
import com.example.escapegame.theme.MissionControlBackground
import com.example.escapegame.viewmodel.Difficulty

@Composable
fun DifficultyScreen(
    onDifficultySelected: (Difficulty) -> Unit,
    onHome: () -> Unit = {},
) {
    var selected by remember { mutableStateOf(Difficulty.KIDS) }

    MissionControlBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(40.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.difficulty_title),
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.difficulty_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = Color.White.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(0.75f),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Junior Agent — Yellow (matches the golden sphere)
                    Button(
                        onClick = { selected = Difficulty.KIDS; onDifficultySelected(Difficulty.KIDS) },
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selected == Difficulty.KIDS)
                                BrandYellow.copy(alpha = 0.85f)
                            else
                                Color.Black.copy(alpha = 0.45f),
                            contentColor = if (selected == Difficulty.KIDS) Color.Black else BrandYellow
                        ),
                        border = BorderStroke(2.dp, BrandYellow)
                    ) {
                        Text(
                            text = stringResource(R.string.btn_kids),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )
                    }

                    // Senior Agent — Blue (matches the tech panels)
                    Button(
                        onClick = { selected = Difficulty.TEENS; onDifficultySelected(Difficulty.TEENS) },
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selected == Difficulty.TEENS)
                                BrandBlue.copy(alpha = 0.85f)
                            else
                                Color.Black.copy(alpha = 0.45f),
                            contentColor = if (selected == Difficulty.TEENS) Color.Black else BrandBlue
                        ),
                        border = BorderStroke(2.dp, BrandBlue)
                    ) {
                        Text(
                            text = stringResource(R.string.btn_teens),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )
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

@Preview(showBackground = true, widthDp = 900, heightDp = 560)
@Composable
private fun DifficultyScreenPreview() {
    EscapeGameTheme {
        DifficultyScreen(onDifficultySelected = {}, onHome = {})
    }
}