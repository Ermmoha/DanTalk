package com.example.core.design.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

val lightColorPalette = ColorPalette(
    main = MainColor,
    hint = HintColor,
    singleTheme = Color.White,
    altSingleTheme = LightAltSingleColor,
    oppositeTheme = Color.Black,
    red = RedColor,
    extras = LightTopBarColor,
    spacer = HintColor.copy(alpha = 0.1f)
)

val darkColorPalette = ColorPalette(
    main = AltMainColor,
    hint = AltHintColor,
    singleTheme = DarkSingleColor,
    altSingleTheme = DarkAltSingleColor,
    oppositeTheme = Color.White,
    red = RedColor,
    extras = DarkTopBarColor,
    spacer = Color.Black.copy(alpha = 0.5f)
)

@Composable
fun DanTalkTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if(darkTheme) darkColorPalette
    else lightColorPalette

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    CompositionLocalProvider(
        LocalColors provides colors,
        content = content
    )
}

object DanTalkTheme {
    val colors: ColorPalette
        @Composable @ReadOnlyComposable
        get() = LocalColors.current
}

internal val LocalColors = staticCompositionLocalOf<ColorPalette> {
    error("Colors composition error")
}