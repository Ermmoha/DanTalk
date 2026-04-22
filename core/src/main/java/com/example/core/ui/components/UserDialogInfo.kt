package com.example.core.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import com.example.core.design.theme.DanTalkTheme
import com.example.core.ui.model.UiUserData

@Composable
fun UserDialogInfo(
    onDismissRequest: () -> Unit,
    actionButtonContent: @Composable () -> Unit,
    onActionButtonClick: () -> Unit,
    onDownloadButtonClick: () -> Unit,
    user: UiUserData,
) {
    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .background(DanTalkTheme.colors.singleTheme, RoundedCornerShape(16.dp))
                .padding(horizontal = 10.dp)
                .padding(bottom = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box {
                AsyncImage(
                    model = user.avatar,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(4f / 3f)
                        .layout { measurable, constraints ->
                            val placeable = measurable.measure(
                                constraints.copy(
                                    maxWidth = constraints.maxWidth + 2 * 10.dp.roundToPx(),
                                )
                            )
                            layout(placeable.width, placeable.height) {
                                placeable.place(0, 0)
                            }
                        }
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop
                )
                IconButtonWithElevation(
                    onClick = { onDownloadButtonClick() },
                    modifier = Modifier.align(Alignment.TopEnd),
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = Color.White
                    ),
                    icon = Icons.Default.Download
                )
            }
            DialogUserInfoItem(
                title = "Полное имя",
                info = "${user.firstname} ${user.lastname} ${user.patronymic}".trim(),
            )
            DialogUserInfoItem(
                title = "Имя пользователя",
                info = user.username
            )
            DialogUserInfoItem(
                title = "Почта",
                info = user.email
            )
            TextButton(
                onClick = onActionButtonClick,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.textButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = DanTalkTheme.colors.main
                )
            ) {
                actionButtonContent()
            }
        }
    }
}

@Composable
private fun DialogUserInfoItem(
    title: String,
    info: String,
) {
    val context = LocalContext.current

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = info,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = DanTalkTheme.colors.oppositeTheme
                )
                Text(
                    text = title,
                    fontSize = 12.sp,
                    color = DanTalkTheme.colors.hint
                )
            }
            IconButton(
                onClick = {
                    copyToClipboard(title, info, context)
                    Toast.makeText(context, "Скопировано в буфер обмена", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.size(30.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = DanTalkTheme.colors.oppositeTheme
                )
            ) {
                Icon(
                    imageVector = Icons.Outlined.ContentCopy,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        HorizontalDivider(
            color = DanTalkTheme.colors.spacer
        )
    }
}

private fun copyToClipboard(title: String, info: String, context: Context) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(title, info)
    clipboard.setPrimaryClip(clip)
}