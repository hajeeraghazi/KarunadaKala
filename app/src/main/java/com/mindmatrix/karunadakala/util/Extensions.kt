package com.mindmatrix.karunadakala.util

import java.text.SimpleDateFormat
import java.util.*

fun Long.toFormattedDate(pattern: String = "dd MMM yyyy"): String {
    return SimpleDateFormat(pattern, Locale.getDefault()).format(Date(this * 1000))
}

fun String.capitalizeWords(): String =
    split(" ").joinToString(" ") { it.replaceFirstChar { c -> c.uppercaseChar() } }
