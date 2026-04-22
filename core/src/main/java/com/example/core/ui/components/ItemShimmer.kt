package com.example.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.design.theme.DanTalkTheme
import com.valentinilk.shimmer.shimmer

@Composable
fun ItemShimmer() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Spacer(Modifier.height(0.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .shimmer()
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(DanTalkTheme.colors.hint.copy(alpha = 0.4f))
            )
            Column(
                modifier = Modifier
                    .height(60.dp)
                    .shimmer(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "",
                    modifier = Modifier
                        .width(120.dp)
                        .background(DanTalkTheme.colors.hint.copy(alpha = 0.4f)),
                    fontSize = 16.sp
                )
                Text(
                    text = "",
                    modifier = Modifier
                        .width(80.dp)
                        .background(DanTalkTheme.colors.hint.copy(alpha = 0.4f)),
                    fontSize = 14.sp
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .shimmer(),
            color = DanTalkTheme.colors.spacer
        )
    }
}