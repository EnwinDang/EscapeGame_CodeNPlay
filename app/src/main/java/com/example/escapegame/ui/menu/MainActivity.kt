package com.example.escapegame.ui.menu

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.escapegame.R
import com.example.escapegame.theme.EscapeGameTheme
import com.example.escapegame.ui.game1.BinaryGameActivity
import com.example.escapegame.ui.game2.ScratchGameActivity
import com.example.escapegame.ui.game3.RobotGameActivity

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EscapeGameTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MenuScreen(
                        modifier = Modifier.padding(innerPadding),
                        onBinaryClick = { startActivity(Intent(this, BinaryGameActivity::class.java)) },
                        onScratchClick = { startActivity(Intent(this, ScratchGameActivity::class.java)) },
                        onRobotClick = { startActivity(Intent(this, RobotGameActivity::class.java)) }
                    )
                }
            }
        }
    }
}

@Composable
fun MenuScreen(
    modifier: Modifier = Modifier,
    onBinaryClick: () -> Unit = {},
    onScratchClick: () -> Unit = {},
    onRobotClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = onBinaryClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(id = R.string.binary_game_button))
        }

        Button(
            onClick = onScratchClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(text = stringResource(id = R.string.scratch_game_button))
        }

        Button(
            onClick = onRobotClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text(text = stringResource(id = R.string.robot_game_button))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MenuScreenPreview() {
    EscapeGameTheme {
        MenuScreen()
    }
}
