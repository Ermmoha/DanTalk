package com.example.core.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat

@SuppressLint("SimpleDateFormat")
fun Long.toTimeString(): String {
    val format = SimpleDateFormat("HH:mm")
    return format.format(this)
}

@SuppressLint("SimpleDateFormat")
fun Long.toDateString(): String {
    val format = SimpleDateFormat("dd.MM.yyyy")
    return format.format(this)
}