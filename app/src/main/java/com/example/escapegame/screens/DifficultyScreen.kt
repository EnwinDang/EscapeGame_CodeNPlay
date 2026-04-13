package com.example.escapegame.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
    MissionControlBackground {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 40.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.88f),
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
                        bullets = listOf(
                            stringResource(R.string.junior_bullet_1),
                            stringResource(R.string.junior_bullet_2),
                            stringResource(R.string.junior_bullet_3),
                            stringResource(R.string.junior_bullet_4),
                            stringResource(R.string.junior_bullet_5),
                        ),
                        borderColor = BrandYellow.copy(alpha = 0.25f),
                        onClick = { onDifficultySelected(Difficulty.KIDS) }
                    )

                    AgentCard(
                        modifier = Modifier.weight(1f).fillMaxHeight(),
                        icon = Icons.Filled.Bolt,
                        iconBg = Color(0xFF002035),
                        iconTint = BrandBlue,
                        title = stringResource(R.string.btn_teens).uppercase(),
                        titleColor = BrandBlue,
                        timerText = stringResource(R.string.senior_agent_description),
                        bullets = listOf(
                            stringResource(R.string.senior_bullet_1),
                            stringResource(R.string.senior_bullet_2),
                            stringResource(R.string.senior_bullet_3),
                            stringResource(R.string.senior_bullet_4),
                            stringResource(R.string.senior_bullet_5),
                        ),
                        borderColor = BrandBlue.copy(alpha = 0.25f),
                        onClick = { onDifficultySelected(Difficulty.TEENS) }
                    )
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
    bullets: List<String>,
    borderColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF0A1A0A).copy(alpha = 0.85f))
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
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

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                bullets.forEach { bullet ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "✦",
                            color = titleColor.copy(alpha = 0.7f),
                            fontSize = 13.sp,
                            modifier = Modifier.width(22.dp)
                        )
                        Text(
                            text = bullet,
                            color = Color.White.copy(alpha = 0.70f),
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
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
