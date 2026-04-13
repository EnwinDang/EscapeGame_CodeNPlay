package com.example.escapegame.screens

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import com.example.escapegame.R
import com.example.escapegame.theme.BrandBlue
import com.example.escapegame.theme.BrandYellow
import com.example.escapegame.theme.MatrixGreen
import com.example.escapegame.theme.MissionControlBackground
import java.util.Locale

private val languages = listOf("en", "nl", "fr")
private val languageLabels = mapOf("en" to "EN", "nl" to "NL", "fr" to "FR")

@Composable
fun HomeScreen(onTap: () -> Unit) {
    val appLocales = AppCompatDelegate.getApplicationLocales()
    val initialTag = if (appLocales.isEmpty) Locale.getDefault().language else appLocales[0]?.language ?: "en"
    var activeLanguage by remember { mutableStateOf(initialTag) }
    var showLangPicker by remember { mutableStateOf(false) }

    // Float animation
    val floatTransition = rememberInfiniteTransition(label = "float")
    val floatY by floatTransition.animateFloat(
        initialValue = 0f,
        targetValue = -16f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float_y"
    )

    MissionControlBackground {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    if (showLangPicker) showLangPicker = false
                    else onTap()
                }
        ) {
            // Logo — top-left
            Image(
                painter = painterResource(id = R.drawable.codenplay_logo),
                contentDescription = "CodeNPlay Logo",
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(24.dp)
                    .size(width = 200.dp, height = 64.dp)
            )

            // Language picker — top-right
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(20.dp)
            ) {
                // Globe pill button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.25f), RoundedCornerShape(50.dp))
                        .background(Color.White.copy(alpha = 0.06f))
                        .clickable { showLangPicker = !showLangPicker }
                        .padding(horizontal = 18.dp, vertical = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Language,
                            contentDescription = null,
                            tint = MatrixGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = languageLabels[activeLanguage] ?: "EN",
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                // Dropdown
                if (showLangPicker) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(y = 52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                            .background(Color(0xFF0A1A0F))
                            .padding(4.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        languages.forEach { lang ->
                            val isActive = lang == activeLanguage
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isActive) MatrixGreen.copy(alpha = 0.2f) else Color.Transparent)
                                    .clickable {
                                        activeLanguage = lang
                                        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(lang))
                                        showLangPicker = false
                                    }
                                    .padding(horizontal = 20.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = languageLabels[lang] ?: lang.uppercase(),
                                    color = if (isActive) MatrixGreen else Color.White.copy(alpha = 0.7f),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            // Center content — floating
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .offset(y = floatY.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "MISSION",
                    fontSize = 96.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.SansSerif,
                    color = MatrixGreen,
                    textAlign = TextAlign.Center,
                    letterSpacing = 2.sp
                )

                Text(
                    text = "CONTROL",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.SansSerif,
                    color = BrandYellow,
                    textAlign = TextAlign.Center,
                    letterSpacing = 10.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Gradient divider
                Box(
                    modifier = Modifier
                        .width(280.dp)
                        .height(2.dp)
                        .background(Brush.horizontalGradient(listOf(BrandBlue, MatrixGreen, BrandYellow)))
                )

                Spacer(modifier = Modifier.height(36.dp))

                Text(
                    text = stringResource(R.string.home_tap_to_start).uppercase(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.SansSerif,
                    color = Color.White.copy(alpha = 0.40f),
                    textAlign = TextAlign.Center,
                    letterSpacing = 3.sp
                )

                Spacer(modifier = Modifier.height(100.dp))
            }

            // Bottom dots
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 28.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val dotColors = listOf(
                    BrandBlue.copy(alpha = 0.5f),
                    MatrixGreen.copy(alpha = 0.5f),
                    BrandYellow.copy(alpha = 0.5f),
                    BrandBlue.copy(alpha = 0.5f),
                    MatrixGreen.copy(alpha = 0.5f),
                    BrandYellow.copy(alpha = 0.5f),
                )
                dotColors.forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }
        }
    }
}