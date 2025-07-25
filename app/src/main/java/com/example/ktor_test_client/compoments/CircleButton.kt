package com.example.ktor_test_client.compoments

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CircleButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = .4f),
    underscoreText: String = "",
    content: @Composable () -> Unit = { }
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            modifier = modifier
                .clip(CircleShape)
                .size(65.dp),
            colors = ButtonColors(
                containerColor = containerColor,
                contentColor = MaterialTheme.colorScheme.onSurface,
                disabledContentColor = MaterialTheme.colorScheme.onSecondary,
                disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer
            ),
            onClick = onClick,
        ) {
            content()
        }

        Text(
            text = underscoreText,
            fontSize = 12.sp,
            fontWeight = FontWeight.W700
        )
    }
}