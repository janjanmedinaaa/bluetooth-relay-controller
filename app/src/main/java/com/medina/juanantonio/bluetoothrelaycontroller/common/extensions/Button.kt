package com.medina.juanantonio.bluetoothrelaycontroller.common.extensions

import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatButton

fun AppCompatButton.setOnPressedListener(onPressListener: (Boolean) -> Unit) {
    setOnTouchListener { _, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> onPressListener(true)
            MotionEvent.ACTION_UP,
            MotionEvent.ACTION_CANCEL -> onPressListener(false)
        }
        return@setOnTouchListener false
    }
}