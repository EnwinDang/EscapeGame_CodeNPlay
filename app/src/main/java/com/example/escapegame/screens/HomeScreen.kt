package com.example.escapegame.screens

import androidx.appcompat.app.AppCompatDelegate
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import com.example.escapegame.R
import java.util.Locale

// CodeNPlay brand colors — matched from the official logo
private val ColorCodeBlue  = Color(0xFF3EA8DC)
private val ColorNYellow   = Color(0xFFF5C516)
private val ColorPlayGreen = Color(0xFF7DC242)

@Composable
fun HomeScreen(onTap: () -> Unit) {
    val appLocales = AppCompatDelegate.getApplicationLocales()
    val initialTag = if (appLocales.isEmpty) Locale.getDefault().language else appLocales[0]?.language ?: "en"
    var activeLanguage by remember { mutableStateOf(initialTag) }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(modifier = Modifier.fillMaxSize().clickable { onTap() }) {

            // Language picker — top-right corner, does NOT trigger onTap
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .clickable(enabled = false) {},
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("en" to "EN", "nl" to "NL", "fr" to "FR").forEach { (tag, label) ->
                    if (tag == activeLanguage) {
                        Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
                            Text(label)
                        }
                    } else {
                        OutlinedButton(onClick = {
                            activeLanguage = tag
                            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))
                        }) {
                            Text(label)
                        }
                    }
                }
            }

            // Main content — tap anywhere to proceed
            Column(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logo image — save your PNG as res/drawable/logo_codenplay.png
                androidx.compose.foundation.Image(
                    painter = painterResource(R.drawable.logo_codenplay),
                    contentDescription = "CodeNPlay logo",
                    modifier = Modifier.size(width = 240.dp, height = 80.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Branded title text matching logo colors
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = ColorCodeBlue, fontWeight = FontWeight.Bold)) { append("Code") }
                        withStyle(SpanStyle(color = ColorNYellow, fontWeight = FontWeight.Bold)) { append("N") }
                        withStyle(SpanStyle(color = ColorPlayGreen, fontWeight = FontWeight.Bold)) { append("Play") }
                    },
                    style = MaterialTheme.typography.displaySmall,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = stringResource(R.string.home_subtitle),
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(48.dp))

                Text(
                    text = stringResource(R.string.home_tap_to_start),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
