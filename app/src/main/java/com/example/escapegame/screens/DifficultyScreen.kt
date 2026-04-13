package com.example.escapegame.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    var selected by remember { mutableStateOf<Difficulty?>(null) }

    MissionControlBackground {
        Box(modifier = Modifier.fillMaxSize()) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 48.dp, vertical = 32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title
                Text(
                    text = stringResource(R.string.difficulty_title),
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    letterSpacing = 2.sp
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.difficulty_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(32.dp))

                // Cards
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(if (selected != null) 0.72f else 0.80f),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    AgentCard(
                        label = stringResource(R.string.btn_kids),
                        icon = Icons.Filled.Shield,
                        accentColor = BrandYellow,
                        isSelected = selected == Difficulty.KIDS,
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        onClick = { selected = Difficulty.KIDS }
                    )
                    AgentCard(
                        label = stringResource(R.string.btn_teens),
                        icon = Icons.Filled.ElectricBolt,
                        accentColor = BrandBlue,
                        isSelected = selected == Difficulty.TEENS,
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        onClick = { selected = Difficulty.TEENS }
                    )
                }

                Spacer(Modifier.height(24.dp))

                // Confirm button — appears after selection
                AnimatedVisibility(
                    visible = selected != null,
                    enter = fadeIn(tween(250)) + slideInVertically(tween(250)) { it / 2 }
                ) {
                    val confirmColor = if (selected == Difficulty.KIDS) BrandYellow else BrandBlue
                    val confirmLabel = if (selected == Difficulty.KIDS)
                        "CONFIRM — ${stringResource(R.string.btn_kids).uppercase()}"
                    else
                        "CONFIRM — ${stringResource(R.string.btn_teens).uppercase()}"

                    Button(
                        onClick = { selected?.let { onDifficultySelected(it) } },
                        modifier = Modifier.fillMaxWidth().height(64.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = confirmColor,
                            contentColor = Color.Black
                        )
                    ) {
                        Text(
                            text = confirmLabel,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 2.sp
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

@Composable
private fun AgentCard(
    label: String,
    icon: ImageVector,
    accentColor: Color,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.03f else 1f,
        animationSpec = tween(200),
        label = "card_scale"
    )
    val borderAlpha by animateFloatAsState(
        targetValue = if (isSelected) 1f else 0.25f,
        animationSpec = tween(200),
        label = "border_alpha"
    )
    val bgAlpha by animateFloatAsState(
        targetValue = if (isSelected) 0.12f else 0.35f,
        animationSpec = tween(200),
        label = "bg_alpha"
    )

    Box(
        modifier = modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) accentColor.copy(alpha = bgAlpha)
                else Color.Black.copy(alpha = bgAlpha)
            )
            .border(2.dp, accentColor.copy(alpha = borderAlpha), RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Icon badge
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(accentColor.copy(alpha = 0.20f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = accentColor
                )
            }

            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = accentColor,
                letterSpacing = 2.sp
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
