package com.example.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.core.design.theme.AltMainColor
import com.example.core.design.theme.DanTalkTheme

@Composable
fun AvatarImage(
    model: String?,
    name: String,
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    contentScale: ContentScale = ContentScale.Crop,
    placeholderIconSize: Dp = 28.dp,
    initialsFontSize: TextUnit = 18.sp,
    colorFilter: ColorFilter? = null
) {
    var loadFailed by remember(model) { mutableStateOf(false) }
    val hasImage = !model.isNullOrBlank() && !loadFailed
    val initials = remember(name) { name.avatarInitials() }
    val placeholderColor = AltMainColor

    Box(
        modifier = modifier
            .clip(shape)
            .background(placeholderColor.copy(alpha = 0.14f), shape),
        contentAlignment = Alignment.Center
    ) {
        if (hasImage) {
            AsyncImage(
                model = model,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = contentScale,
                colorFilter = colorFilter,
                onError = { loadFailed = true }
            )
        } else if (initials.isNotBlank()) {
            Text(
                text = initials,
                color = placeholderColor,
                fontSize = initialsFontSize,
                fontWeight = FontWeight.SemiBold
            )
        } else {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = null,
                modifier = Modifier
                    .size(placeholderIconSize)
                    .padding(2.dp),
                tint = placeholderColor
            )
        }
    }
}

private fun String.avatarInitials(): String {
    val parts = trim()
        .split(Regex("\\s+"))
        .filter { it.isNotBlank() }

    return parts
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar()?.toString() }
        .joinToString("")
        .ifBlank { trim().take(1).uppercase() }
}
