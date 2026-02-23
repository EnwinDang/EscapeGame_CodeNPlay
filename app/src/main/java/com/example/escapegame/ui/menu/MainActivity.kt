package com.example.escapegame.ui.menu

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.escapegame.R
import com.example.escapegame.ui.game1.BinaryGameActivity
import com.example.escapegame.ui.game2.ScratchGameActivity
import com.example.escapegame.ui.game3.RobotGameActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnBinary = findViewById<Button>(R.id.btnBinary)
        val btnScratch = findViewById<Button>(R.id.btnScratch)
        val btnRobot = findViewById<Button>(R.id.btnRobot)

        btnBinary.setOnClickListener {
            startActivity(Intent(this, BinaryGameActivity::class.java))
        }

        btnScratch.setOnClickListener {
            startActivity(Intent(this, ScratchGameActivity::class.java))
        }

        btnRobot.setOnClickListener {
            startActivity(Intent(this, RobotGameActivity::class.java))
        }
    }
}