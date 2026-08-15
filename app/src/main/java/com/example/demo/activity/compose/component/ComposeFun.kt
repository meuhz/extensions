package com.example.demo.activity.compose.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
fun Int.pxToDp(): Dp {
    return LocalDensity.current.run {
        toDp()
    }
}