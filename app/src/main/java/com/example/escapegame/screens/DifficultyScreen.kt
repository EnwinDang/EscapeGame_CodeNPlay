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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.VerifiedUser
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
import androidx.compose.ui.text.font.FontFamily
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
                    .padding(horizontal = 40.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title
                Text(
                    text = stringResource(R.string.difficulty_title).uppercase(),
                    fontSize = 52.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontFamily = FontFamily.SansSerif,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.difficulty_subtitle),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White.copy(alpha = 0.55f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Cards
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(if (selected != null) 0.72f else 0.88f),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    AgentCard(
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        icon = Icons.Filled.VerifiedUser,
                        iconBg = Color(0xFF3D3000),
                        iconTint = BrandYellow,
                        title = stringResource(R.string.btn_kids).uppercase(),
                        titleColor = BrandYellow,
                        timerText = stringResource(R.string.junior_agent_description),
                        borderColor = BrandYellow.copy(alpha = 0.25f),
                        isSelected = selected == Difficulty.KIDS,
                        onClick = { selected = Difficulty.KIDS }
                    )

                    AgentCard(
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        icon = Icons.Filled.Bolt,
                        iconBg = Color(0xFF002035),
                        iconTint = BrandBlue,
                        title = stringResource(R.string.btn_teens).uppercase(),
                        titleColor = BrandBlue,
                        timerText = stringResource(R.string.senior_agent_description),
                        borderColor = BrandBlue.copy(alpha = 0.25f),
                        isSelected = selected == Difficulty.TEENS,
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
                        "${stringResource(R.string.difficulty_confirm)} — ${stringResource(R.string.btn_kids).uppercase()}"
                    else
                        "${stringResource(R.string.difficulty_confirm)} — ${stringResource(R.string.btn_teens).uppercase()}"

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
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
private fun AgentCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    titleColor: Color,
    timerText: String,
    borderColor: Color,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.03f else 1f,
        animationSpec = tween(200),
        label = "card_scale"
    )

    Box(
        modifier = modifier
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF0A1A0A).copy(alpha = 0.85f))
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) titleColor.copy(alpha = 0.7f) else borderColor,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon box
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = title,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.SansSerif,
                color = titleColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = timerText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White.copy(alpha = 0.50f),
                textAlign = TextAlign.Center
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