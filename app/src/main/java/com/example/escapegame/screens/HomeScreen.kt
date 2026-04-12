package com.example.escapegame.screens

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import com.example.escapegame.R
import com.example.escapegame.theme.BrandYellow
import com.example.escapegame.theme.MatrixDeepGreen
import com.example.escapegame.theme.MatrixGreen
import com.example.escapegame.theme.MissionControlBackground
import java.util.Locale

@Composable
fun HomeScreen(onTap: () -> Unit) {
    val appLocales = AppCompatDelegate.getApplicationLocales()
    val initialTag = if (appLocales.isEmpty) Locale.getDefault().language else appLocales[0]?.language ?: "en"
    var activeLanguage by remember { mutableStateOf(initialTag) }

    MissionControlBackground {
        Box(modifier = Modifier.fillMaxSize().clickable { onTap() }) {
            // Logo afbeelding - alleen deze linksboven
            Image(
                painter = painterResource(id = R.drawable.codenplay_logo),
                contentDescription = "CodeNPlay Logo",
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(24.dp)
                    .size(width = 300.dp, height = 90.dp)
            )

            // Language picker — top-right corner
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .clickable(enabled = false) {},
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("en" to "EN", "nl" to "NL", "fr" to "FR").forEach { (tag, label) ->
                    if (tag == activeLanguage) {
                        Button(
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MatrixDeepGreen
                            )
                        ) {
                            Text(label, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        OutlinedButton(onClick = {
                            activeLanguage = tag
                            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))
                        }) {
                            Text(label, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Main content — Centraal uitgelijnd, maar iets naar boven verschoven
            Column(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.home_subtitle),
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 64.sp,
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = BrandYellow,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = stringResource(R.string.home_tap_to_start),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MatrixGreen.copy(alpha = 0.95f),
                    textAlign = TextAlign.Center
                )

                // Extra spacer onderaan om alles meer naar boven te duwen
                Spacer(modifier = Modifier.height(150.dp))
            }
        }
    }
}
