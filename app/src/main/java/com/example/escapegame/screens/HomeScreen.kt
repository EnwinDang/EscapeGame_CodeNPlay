package com.example.escapegame.screens

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import com.example.escapegame.R
import com.example.escapegame.theme.BrandYellow
import com.example.escapegame.theme.MissionControlBackground
import java.util.Locale

@Composable
fun HomeScreen(onTap: () -> Unit) {
    val appLocales = AppCompatDelegate.getApplicationLocales()
    val initialTag = if (appLocales.isEmpty) Locale.getDefault().language else appLocales[0]?.language ?: "en"
    var activeLanguage by remember { mutableStateOf(initialTag) }
    var showLangPicker by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "home")

    val floatOffset = infiniteTransition.animateFloat(
        initialValue = -8f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    val pulseAlpha = infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    MissionControlBackground {
        Box(modifier = Modifier.fillMaxSize().clickable { onTap() }) {

            // Logo — top-left
            Image(
                painter = painterResource(id = R.drawable.codenplay_logo),
                contentDescription = "CodeNPlay",
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(24.dp)
                    .height(40.dp),
                contentScale = ContentScale.Fit
            )

            // Language picker — top-right
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                OutlinedButton(onClick = { showLangPicker = !showLangPicker }) {
                    Text("🌐  ${activeLanguage.uppercase()}", fontWeight = FontWeight.Bold)
                }
                DropdownMenu(
                    expanded = showLangPicker,
                    onDismissRequest = { showLangPicker = false }
                ) {
                    listOf("en" to "EN", "nl" to "NL", "fr" to "FR").forEach { (tag, label) ->
                        DropdownMenuItem(
                            text = { Text(label, fontWeight = FontWeight.Bold) },
                            onClick = {
                                activeLanguage = tag
                                AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))
                                showLangPicker = false
                            }
                        )
                    }
                }
            }

            // Center content — floats up and down
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
                    .graphicsLayer { translationY = floatOffset.value },
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "MISSION",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 80.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.sp
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "Control",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 8.sp
                    ),
                    color = BrandYellow,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Gradient divider
                Box(
                    modifier = Modifier
                        .width(192.dp)
                        .height(1.dp)
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color.Transparent,
                                    MaterialTheme.colorScheme.primary,
                                    Color.Transparent
                                )
                            )
                        )
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = stringResource(R.string.home_tap_to_start),
                    style = MaterialTheme.typography.bodyLarge.copy(letterSpacing = 3.sp),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = pulseAlpha.value),
                    textAlign = TextAlign.Center
                )
            }

            // Bottom decoration dots
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(5) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                CircleShape
                            )
                    )
                }
            }
        }
    }
}
