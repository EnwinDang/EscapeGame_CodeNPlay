package com.example.escapegame.logic

import android.content.Context
import android.media.MediaPlayer

fun playSuccessSfx(context: Context) = playSfx(context, "sfx/success.wav")
fun playFailSfx(context: Context)    = playSfx(context, "sfx/fail.wav")

private fun playSfx(context: Context, path: String) {
    try {
        MediaPlayer().apply {
            val afd = context.assets.openFd(path)
            setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            prepare()
            start()
            setOnCompletionListener { release() }
        }
    } catch (_: Exception) { }
}