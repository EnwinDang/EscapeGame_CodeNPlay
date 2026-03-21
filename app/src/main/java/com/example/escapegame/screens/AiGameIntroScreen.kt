package com.example.escapegame.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.escapegame.R
import com.example.escapegame.theme.BlueContainer
import com.example.escapegame.theme.BrandBlue
import com.example.escapegame.theme.BrandGreen
import com.example.escapegame.theme.ErrorContainer
import com.example.escapegame.theme.ErrorRed
import com.example.escapegame.theme.GreenContainer
import com.example.escapegame.theme.MissionControlBackground
import com.example.escapegame.theme.YellowContainer
import com.example.escapegame.theme.YellowDark
import kotlinx.coroutines.delay

private data class StoryPage(
    val icon: ImageVector,
    val iconColor: Color,
    val iconBgColor: Color,
    val accentColor: Color,
    val titleRes: Int,
    val bodyRes: Int,
    val pulse: Boolean = false,
)

private val storyPages = listOf(
    StoryPage(
        icon       = Icons.Filled.Warning,
        iconColor  = ErrorRed,
        iconBgColor = ErrorContainer,
        accentColor = ErrorRed,
        titleRes   = R.string.ai_intro_alarm_title,
        bodyRes    = R.string.ai_intro_alarm_body,
        pulse      = true,
    ),
    StoryPage(
        icon       = Icons.Filled.BugReport,
        iconColor  = YellowDark,
        iconBgColor = YellowContainer,
        accentColor = YellowDark,
        titleRes   = R.string.ai_intro_threat_title,
        bodyRes    = R.string.ai_intro_threat_body,
    ),
    StoryPage(
        icon       = Icons.Filled.SmartToy,
        iconColor  = BrandBlue,
        iconBgColor = BlueContainer,
        accentColor = BrandBlue,
        titleRes   = R.string.ai_intro_mission_title,
        bodyRes    = R.string.ai_intro_mission_body,
    ),
)

@Composable
fun AiGameIntroScreen(
    onReady: () -> Unit,
    onHome: () -> Unit,
) {
    BackHandler(enabled = true) {}

    var pageIndex by remember { mutableIntStateOf(0) }
    var countdownLabel by remember { mutableStateOf<String?>(null) }
    var countdownStarted by remember { mutableStateOf(false) }
    val goLabel = stringResource(R.string.ai_game_intro_go)

    LaunchedEffect(countdownStarted) {
        if (!countdownStarted) return@LaunchedEffect
        for (step in listOf("1", "2", "3", goLabel)) {
            countdownLabel = step
            delay(900L)
        }
        onReady()
    }

    MissionControlBackground {
        AnimatedContent(
            targetState = countdownLabel,
            transitionSpec = {
                (fadeIn() + scaleIn(initialScale = 0.6f)) togetherWith
                        (fadeOut() + scaleOut(targetScale = 1.3f))
            },
            label = "countdown_anim"
        ) { label ->
            if (label == null) {
                AnimatedContent(
                    targetState = pageIndex,
                    transitionSpec = {
                        (slideInHorizontally { it } + fadeIn()) togetherWith
                                (slideOutHorizontally { -it } + fadeOut())
                    },
                    label = "page_anim"
                ) { idx ->
                    StoryPageContent(
                        page      = storyPages[idx],
                        pageIndex = idx,
                        totalPages = storyPages.size,
                        isLast    = idx == storyPages.lastIndex,
                        onNext    = { pageIndex++ },
                        onStart   = { countdownStarted = true },
                        onHome    = onHome,
                    )
                }
            } else {
                CountdownContent(label = label, isGo = label == goLabel)
            }
        }
    }
}

@Composable
private fun StoryPageContent(
    page: StoryPage,
    pageIndex: Int,
    totalPages: Int,
    isLast: Boolean,
    onNext: () -> Unit,
    onStart: () -> Unit,
    onHome: () -> Unit,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue  = 1.09f,
        animationSpec = InfiniteRepeatableSpec(
            animation  = tween(550),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulseScale"
    )

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 36.dp, vertical = 28.dp),
        horizontalArrangement = Arrangement.spacedBy(28.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // ── Left panel ──────────────────────────────────────────────
        Column(
            modifier = Modifier
                .weight(0.38f)
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Icon circle
            Box(
                modifier = Modifier
                    .size(148.dp)
                    .then(if (page.pulse) Modifier.scale(pulseScale) else Modifier)
                    .clip(CircleShape)
                    .background(page.iconBgColor)
                    .border(3.dp, page.accentColor, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = page.icon,
                    contentDescription = null,
                    modifier = Modifier.size(76.dp),
                    tint = page.iconColor,
                )
            }

            Spacer(Modifier.height(20.dp))

            Text(
                text = stringResource(page.titleRes),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
                color = page.accentColor,
                textAlign = TextAlign.Center,
                letterSpacing = 2.sp,
            )

            Spacer(Modifier.height(20.dp))

            // Page dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                repeat(totalPages) { i ->
                    Box(
                        modifier = Modifier
                            .size(if (i == pageIndex) 14.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (i == pageIndex) page.accentColor
                                else page.accentColor.copy(alpha = 0.22f)
                            )
                    )
                }
            }
        }

        // ── Vertical divider ────────────────────────────────────────
        Box(
            modifier = Modifier
                .width(1.dp)
                .fillMaxHeight(0.8f)
                .background(page.accentColor.copy(alpha = 0.20f))
        )

        // ── Right panel ─────────────────────────────────────────────
        Column(
            modifier = Modifier
                .weight(0.62f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
        ) {
            // Body card with left accent strip
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.92f))
                    .border(1.dp, page.accentColor.copy(alpha = 0.28f), RoundedCornerShape(16.dp))
            ) {
                Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                    Box(
                        modifier = Modifier
                            .width(6.dp)
                            .fillMaxHeight()
                            .background(page.accentColor)
                    )
                    Text(
                        text = stringResource(page.bodyRes),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Navigation buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                HomeButton(onHome = onHome)

                Button(
                    onClick = if (isLast) onStart else onNext,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isLast) BrandGreen else page.accentColor
                    ),
                ) {
                    Icon(
                        imageVector = if (isLast) Icons.Filled.SmartToy else Icons.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = stringResource(
                            if (isLast) R.string.ai_intro_btn_start else R.string.ai_intro_btn_next
                        ),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
private fun CountdownContent(label: String, isGo: Boolean) {
    val infiniteTransition = rememberInfiniteTransition(label = "glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue  = 0.35f,
        animationSpec = InfiniteRepeatableSpec(
            animation  = tween(400),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "glowAlpha"
    )

    val textColor = if (isGo) BrandGreen else BrandBlue
    val circleColor = if (isGo) GreenContainer else BlueContainer

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        // Pulsing background circle
        Box(
            modifier = Modifier
                .size(280.dp)
                .clip(CircleShape)
                .background(circleColor.copy(alpha = glowAlpha))
                .border(2.dp, textColor.copy(alpha = glowAlpha + 0.1f), CircleShape)
        )

        Text(
            text = label,
            fontSize = 140.sp,
            fontWeight = FontWeight.Black,
            color = textColor,
            textAlign = TextAlign.Center,
            letterSpacing = if (isGo) 4.sp else 0.sp,
        )
    }
}